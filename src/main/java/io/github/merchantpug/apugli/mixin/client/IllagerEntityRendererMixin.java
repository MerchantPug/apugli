package io.github.merchantpug.apugli.mixin.client;

import io.github.merchantpug.apugli.entity.feature.StackHeadFeatureRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.IllagerEntityRenderer;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.IllagerEntityModel;
import net.minecraft.entity.mob.IllagerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(IllagerEntityRenderer.class)
public abstract class IllagerEntityRendererMixin<T extends IllagerEntity> extends MobEntityRenderer<T, IllagerEntityModel<T>> {

    public IllagerEntityRendererMixin(EntityRendererFactory.Context context, IllagerEntityModel<T> entityModel, float f) {
        super(context, entityModel, f);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void construct(EntityRendererFactory.Context ctx, IllagerEntityModel<T> model, float shadowRadius, CallbackInfo ci) {
        this.addFeature(new StackHeadFeatureRenderer<>(this, ctx.getModelLoader()));
    }
}
