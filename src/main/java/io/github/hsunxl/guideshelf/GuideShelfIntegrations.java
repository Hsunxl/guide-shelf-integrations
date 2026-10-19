package io.github.hsunxl.guideshelf;

import com.mojang.logging.LogUtils;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;

@Mod(value="guide_shelf_integrations",dist=Dist.CLIENT)
public final class GuideShelfIntegrations {
    public static final Logger LOG=LogUtils.getLogger();
    public GuideShelfIntegrations() { LOG.info("Guide Shelf integrations: native Patchouli/GuideME plus optional Ponder/Oracle"); }
}
