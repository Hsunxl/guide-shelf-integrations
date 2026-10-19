#!/usr/bin/env python3
"""Read-only ABI check against user-supplied mod jars. Does not initialize game classes."""
import argparse
from pathlib import Path
import subprocess


def inspect(jar, name, code=False):
    args=['javap','-classpath',str(jar),'-p','-s']
    if code: args.append('-c')
    args.append(name)
    return subprocess.run(args,check=True,capture_output=True,text=True,encoding='utf-8',errors='replace').stdout


def verify_shelf(jar):
    text=inspect(jar,'io.github.catomon.guideshelf.BookDiscovery',True)
    assert text.count('Method java/util/ArrayList.stream:()Ljava/util/stream/Stream;')==1, 'Discovery injection point changed'
    assert text.index('Method java/util/ArrayList.stream:')<text.index('InterfaceMethod java/util/stream/Stream.filter:'), 'Native ignore filter must follow merge'
    assert '(Lnet/minecraft/client/Minecraft;Lio/github/catomon/guideshelf/BookEntry;)V' in text, 'Open signature changed'
    assert 'Field io/github/catomon/guideshelf/ModConfig.ignoredBookIds:' in text, 'Ignore list not found'
    print('PASS Guide Shelf merge-before-filter and opener ABI:',Path(jar).name)


def main():
    parser=argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--shelf',type=Path,action='append',required=True)
    parser.add_argument('--oracle',type=Path)
    parser.add_argument('--ponder',type=Path)
    parser.add_argument('--guideme',type=Path)
    args=parser.parse_args()
    for jar in args.shelf: verify_shelf(jar)
    if args.oracle:
        text=inspect(args.oracle,'rearth.oracle.OracleClient')
        assert '(Ljava/lang/String;Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/client/gui/screens/Screen;)V' in text
        for field in ['LOADED_WIKIS','AVAILABLE_MODES']: assert field in text
        text=inspect(args.oracle,'rearth.oracle.ui.OracleScreen')
        for field in ['activeWiki','activeEntry','activeWikiMode']: assert field in text
        print('PASS Oracle open/reset ABI')
    if args.ponder:
        assert '()V' in inspect(args.ponder,'net.createmod.ponder.foundation.ui.PonderIndexScreen')
        assert '(Lnet/minecraft/client/gui/screens/Screen;Lnet/minecraft/client/gui/screens/Screen;)V' in inspect(args.ponder,'net.createmod.catnip.gui.ScreenOpener')
        print('PASS Ponder parent-screen ABI')
    if args.guideme:
        text=inspect(args.guideme,'guideme.Guides')
        assert 'getAll()' in text
        assert '(Lnet/minecraft/resources/ResourceLocation;)Lnet/minecraft/world/item/ItemStack;' in text
        print('PASS GuideME discovery ABI')


if __name__=='__main__': main()
