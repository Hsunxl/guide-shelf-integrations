package io.github.hsunxl.guideshelf.mixin;

import io.github.catomon.guideshelf.BookDiscovery;
import io.github.catomon.guideshelf.BookEntry;
import io.github.hsunxl.guideshelf.OptionalGuides;
import java.util.ArrayList;
import java.util.stream.Stream;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value=BookDiscovery.class,remap=false)
public abstract class BookDiscoveryMixin {
    // Merge BEFORE Guide Shelf's native ignoredBookIds filter, not at RETURN.
    @Redirect(method="findBooks",at=@At(value="INVOKE",target="Ljava/util/ArrayList;stream()Ljava/util/stream/Stream;"),require=1)
    private static Stream<BookEntry> integrations$discover(ArrayList<BookEntry> original) {
        return OptionalGuides.append(original).stream();
    }
    @Inject(method="open",at=@At("HEAD"),cancellable=true,require=1)
    private static void integrations$open(Minecraft mc,BookEntry book,CallbackInfo ci) {
        if(OptionalGuides.open(mc,book)) ci.cancel();
    }
}
