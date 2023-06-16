package net.merchantpug.apugli.mixin.fabric.client;

import net.merchantpug.apugli.client.renderer.PowerCustomHeadLayer;
import net.merchantpug.apugli.client.renderer.PowerHumanoidArmorLayer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidMobRenderer.class)
public abstract class BipedEntityRendererMixin<T extends Mob, M extends HumanoidModel<T>> extends MobRenderer<T, M> {

    public BipedEntityRendererMixin(EntityRendererProvider.Context context, M entityModel, float f) {
        super(context, entityModel, f);
    }

    @Inject(method = "<init>(Lnet/minecraft/client/renderer/entity/EntityRendererProvider$Context;Lnet/minecraft/client/model/HumanoidModel;FFFF)V", at = @At("RETURN"))
    public void addLayers(EntityRendererProvider.Context ctx, HumanoidModel humanoidModel, float f, float g, float h, float i, CallbackInfo ci) {
        this.addLayer(new PowerCustomHeadLayer<>(this, ctx.getModelSet(), g, h, i));
        this.addLayer(new PowerHumanoidArmorLayer<>(this, this.getModel(), this.getModel()));
    }

}
