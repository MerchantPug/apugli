package net.merchantpug.apugli.util;

import io.github.apace100.calio.ClassUtil;
import io.github.apace100.calio.data.ClassDataRegistry;
import net.merchantpug.apugli.client.renderer.EntityTextureOverlayLayer;
import net.minecraft.client.renderer.entity.layers.EnergySwirlLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;

public class ApugliClassDataClient {
    public static void registerAll() {
        ClassDataRegistry<RenderLayer<?, ?>> featureRenderer =
                ClassDataRegistry.getOrCreate(ClassUtil.castClass(RenderLayer.class), "FeatureRenderer");

        featureRenderer.addMapping("energy_swirl_power_overlay", EnergySwirlLayer.class);
        featureRenderer.addMapping("entity_texture_overlay", EntityTextureOverlayLayer.class);
    }
}
