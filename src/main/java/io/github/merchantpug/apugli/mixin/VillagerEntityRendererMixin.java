package io.github.merchantpug.apugli.mixin;

import io.github.merchantpug.apugli.entity.feature.StackHeadFeatureRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.VillagerEntityRenderer;
import net.minecraft.client.render.entity.model.VillagerResemblingModel;
import net.minecraft.entity.passive.VillagerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(VillagerEntityRenderer.class)
public abstract class VillagerEntityRendererMixin extends MobEntityRenderer<VillagerEntity, VillagerResemblingModel<VillagerEntity>> {
    public VillagerEntityRendererMixin(EntityRendererFactory.Context context, VillagerResemblingModel<VillagerEntity> entityModel, float f) {
        super(context, entityModel, f);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void construct(EntityRendererFactory.Context context, CallbackInfo ci) {
        this.addFeature(new StackHeadFeatureRenderer<>(this, context.getModelLoader()));
    }
}
