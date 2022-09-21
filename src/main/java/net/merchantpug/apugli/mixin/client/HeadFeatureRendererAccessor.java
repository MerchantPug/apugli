package net.merchantpug.apugli.mixin.client;

import net.minecraft.block.SkullBlock;
import net.minecraft.client.render.block.entity.SkullBlockEntityModel;
import net.minecraft.client.render.entity.feature.HeadFeatureRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(HeadFeatureRenderer.class)
public interface HeadFeatureRendererAccessor {
    @Accessor
    Map<SkullBlock.SkullType, SkullBlockEntityModel> getHeadModels();
}
