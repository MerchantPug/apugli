package io.github.merchantpug.apugli.util;

import io.github.apace100.calio.ClassUtil;
import io.github.apace100.calio.data.ClassDataRegistry;
import io.github.merchantpug.apugli.entity.feature.*;
import net.minecraft.client.render.entity.feature.FeatureRenderer;

public class ApugliClassDataClient {

    public static void registerAll() {
        ClassDataRegistry<FeatureRenderer<?, ?>> featureRenderer =
                ClassDataRegistry.getOrCreate(ClassUtil.castClass(FeatureRenderer.class), "FeatureRenderer");

        featureRenderer.addMapping("energy_swirl_power_overlay", EnergySwirlOverlayFeatureRenderer.class);
        featureRenderer.addMapping("entity_texture_overlay", EntityTextureOverlayFeatureRenderer.class);
        featureRenderer.addMapping("modified_armor", StackArmorFeatureRenderer.class);
        featureRenderer.addMapping("modified_head", StackHeadFeatureRenderer.class);
        featureRenderer.addMapping("modified_held_item", StackHeldItemFeatureRenderer.class);
    }
}
