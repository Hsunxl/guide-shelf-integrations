package io.github.hsunxl.guideshelf;

import io.github.catomon.guideshelf.BookEntry;
import java.lang.reflect.*;
import java.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.neoforged.fml.ModList;

/** Optional APIs are resolved only inside a guarded provider; absent mods never link. */
public final class OptionalGuides {
    private static final Set<String> warned=new HashSet<>();
    private OptionalGuides() {}
    private static Class<?> type(String name) throws ClassNotFoundException { return Class.forName(name); }
    private static boolean loaded(String mod) { return ModList.get().isLoaded(mod); }
    private static void failure(String provider,Throwable ex) {
        if(warned.add(provider)) GuideShelfIntegrations.LOG.warn("Optional guide provider unavailable: {}",provider,ex);
    }
    private static BookEntry entry(String id,Item item,Component title) {
        return new BookEntry(ResourceLocation.parse(id),title,new ItemStack(item));
    }
    public static List<BookEntry> append(List<BookEntry> original) {
        var added=new ArrayList<BookEntry>();
        if(loaded("ponder")) {
            try {
                type("net.createmod.ponder.foundation.ui.PonderIndexScreen").getConstructor();
                added.add(entry("ponder:library",Items.SPYGLASS,Component.translatable("guide_shelf_integrations.ponder")));
            } catch(ReflectiveOperationException|LinkageError ex) { failure("ponder",ex); }
        }
        if(loaded("oracle_index")) {
            try {
                added.add(entry("oracle_index:library",Items.ENCHANTED_BOOK,Component.translatable("guide_shelf_integrations.oracle")));
                Map<?,?> wikis=(Map<?,?>)type("rearth.oracle.OracleClient").getField("LOADED_WIKIS").get(null);
                // Reload-sensitive discovery: never cache wiki entries or fabricate installed-mod IDs.
                wikis.keySet().stream().filter(String.class::isInstance).map(String.class::cast).sorted().forEach(wiki->{
                    String path=DiscoveryPolicy.wikiPath(wiki);
                    if(path!=null) added.add(entry("oracle_index:"+path,Items.BOOK,Component.translatable("guide_shelf_integrations.wiki",wiki)));
                });
            } catch(ReflectiveOperationException|RuntimeException|LinkageError ex) { failure("oracle",ex); }
        }
        if(loaded("guideme")) {
            try {
                Class<?> guides=type("guideme.Guides"), guide=type("guideme.Guide");
                var all=(Collection<?>)guides.getMethod("getAll").invoke(null);
                Method getId=guide.getMethod("getId"),create=guides.getMethod("createGuideItem",ResourceLocation.class);
                for(Object book:all) {
                    var id=(ResourceLocation)getId.invoke(book);
                    var stack=(ItemStack)create.invoke(null,id);
                    if(!stack.isEmpty()) added.add(new BookEntry(id,stack.getHoverName(),stack));
                }
            } catch(ReflectiveOperationException|RuntimeException|LinkageError ex) { failure("guideme",ex); }
        }
        return DiscoveryPolicy.merge(original,added,b->b.bookId().toString());
    }
    /** true only for IDs owned by this integration; Patchouli/GuideME keep the native opener. */
    public static boolean open(Minecraft mc,BookEntry book) {
        String id=book.bookId().toString();
        boolean ponder=id.equals("ponder:library");
        boolean oracle=id.equals("oracle_index:library") || id.startsWith("oracle_index:wiki/");
        if(!ponder&&!oracle) return false;
        if(mc.player==null || mc.level==null) return true;
        try {
            if(ponder) {
                if(!loaded("ponder")) throw new IllegalStateException("Ponder is not loaded");
                Screen screen=(Screen)type("net.createmod.ponder.foundation.ui.PonderIndexScreen").getConstructor().newInstance();
                type("net.createmod.catnip.gui.ScreenOpener").getMethod("open",Screen.class,Screen.class).invoke(null,mc.screen,screen);
            } else {
                if(!loaded("oracle_index")) throw new IllegalStateException("Oracle is not loaded");
                String wiki=id.equals("oracle_index:library")?null:id.substring("oracle_index:wiki/".length());
                Class<?> client=type("rearth.oracle.OracleClient"),screen=type("rearth.oracle.ui.OracleScreen");
                var wikis=(Map<?,?>)client.getField("LOADED_WIKIS").get(null);
                if(wiki!=null && !wikis.containsKey(wiki)) throw new IllegalStateException("Wiki is not loaded: "+wiki);
                Field activeWiki=screen.getField("activeWiki");
                if(wiki!=null&&!wiki.equals(activeWiki.get(null))) {
                    screen.getField("activeEntry").set(null,null);
                    Map<?,?> modes=(Map<?,?>)client.getField("AVAILABLE_MODES").get(null);
                    Object raw=modes.get(wiki);
                    Collection<?> available=raw instanceof Collection<?> c?c:List.of();
                    Object mode=available.stream().filter(m->m instanceof Enum<?> e&&e.name().equals("DOCS")).findFirst().orElse(null);
                    if(mode==null) mode=available.stream().findFirst().orElse(null);
                    if(mode==null) for(Object constant:type("rearth.oracle.docs.DocsMode").getEnumConstants())
                        if(((Enum<?>)constant).name().equals("DOCS")) mode=constant;
                    screen.getField("activeWikiMode").set(null,mode);
                }
                client.getMethod("openScreen",String.class,ResourceLocation.class,Screen.class).invoke(null,wiki,null,mc.screen);
            }
        } catch(ReflectiveOperationException|RuntimeException|LinkageError ex) {
            failure("open:"+id,ex);
            mc.player.displayClientMessage(Component.translatable("guide_shelf_integrations.failed",book.displayName()),false);
        }
        return true;
    }
}
