package net.merchantpug.apugli.mixin.xplatform.client.accessor;

import net.minecraft.client.renderer.ItemInHandRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;
import net.minecraft.client.model.SkullModelBase;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.world.level.block.SkullBlock;

@Mixin(CustomHeadLayer.class)
public interface HeadFeatureRendererAccessor {

    @Accessor("skullModels")
    Map<SkullBlock.Type, SkullModelBase> getHeadModels();

    @Accessor
    float getScaleX();

    @Accessor
    float getScaleY();


    @Accessor
    float getScaleZ();

}
