package io.github.merchantpug.apugli.mixin.client;

import io.github.merchantpug.apugli.entity.feature.StackArmorFeatureRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.BipedEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.PiglinEntityRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.PiglinEntityModel;
import net.minecraft.entity.mob.MobEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(PiglinEntityRenderer.class)
public class PiglinEntityRendererMixin extends BipedEntityRenderer<MobEntity, PiglinEntityModel<MobEntity>> {

    public PiglinEntityRendererMixin(EntityRendererFactory.Context ctx, PiglinEntityModel<MobEntity> model, float shadowRadius) {
        super(ctx, model, shadowRadius);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void construct(EntityRendererFactory.Context ctx, EntityModelLayer mainLayer, EntityModelLayer innerArmorLayer, EntityModelLayer outerArmorLayer, boolean zombie, CallbackInfo ci) {
        this.addFeature(new StackArmorFeatureRenderer<>(this, new BipedEntityModel<>(ctx.getPart(innerArmorLayer)), new BipedEntityModel(ctx.getPart(outerArmorLayer))));
    }
}
