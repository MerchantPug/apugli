package com.github.merchantpug.apugli.util;

import io.github.apace100.calio.ClassUtil;
import io.github.apace100.calio.data.ClassDataRegistry;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import the.great.migration.merchantpug.apugli.entity.feature.EnergySwirlOverlayFeatureRenderer;
import the.great.migration.merchantpug.apugli.entity.feature.EntityTextureOverlayFeatureRenderer;

public class ApugliClassDataClient {

    public static void registerAll() {
        ClassDataRegistry<RenderLayer<?, ?>> featureRenderer =
                ClassDataRegistry.getOrCreate(ClassUtil.castClass(RenderLayer.class), "FeatureRenderer");

        featureRenderer.addMapping("energy_swirl_power_overlay", EnergySwirlOverlayFeatureRenderer.class);
        featureRenderer.addMapping("entity_texture_overlay", EntityTextureOverlayFeatureRenderer.class);
    }
}
