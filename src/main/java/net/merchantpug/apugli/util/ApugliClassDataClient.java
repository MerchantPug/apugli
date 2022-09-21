package net.merchantpug.apugli.util;

import net.merchantpug.apugli.entity.feature.EnergySwirlOverlayFeatureRenderer;
import net.merchantpug.apugli.entity.feature.EntityTextureOverlayFeatureRenderer;
import io.github.apace100.calio.ClassUtil;
import io.github.apace100.calio.data.ClassDataRegistry;
import net.minecraft.client.render.entity.feature.FeatureRenderer;

public class ApugliClassDataClient {

    public static void registerAll() {
        ClassDataRegistry<FeatureRenderer<?, ?>> featureRenderer =
                ClassDataRegistry.getOrCreate(ClassUtil.castClass(FeatureRenderer.class), "FeatureRenderer");

        featureRenderer.addMapping("energy_swirl_power_overlay", EnergySwirlOverlayFeatureRenderer.class);
        featureRenderer.addMapping("entity_texture_overlay", EntityTextureOverlayFeatureRenderer.class);
    }
}
