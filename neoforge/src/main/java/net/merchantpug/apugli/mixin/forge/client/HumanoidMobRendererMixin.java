package net.merchantpug.apugli.mixin.forge.client;

import net.merchantpug.apugli.access.HumanoidMobRendererAccess;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidMobRenderer.class)
public class HumanoidMobRendererMixin implements HumanoidMobRendererAccess {
    @Unique
    public Vector3f apugli$headSize;

    @Inject(method = "<init>(Lnet/minecraft/client/renderer/entity/EntityRendererProvider$Context;Lnet/minecraft/client/model/HumanoidModel;FFFF)V", at = @At("TAIL"))
    private void apugli$captureHeadSize(EntityRendererProvider.Context pContext, HumanoidModel pModel, float pShadowRadius, float pScaleX, float pScaleY, float pScaleZ, CallbackInfo ci) {
        this.apugli$setHeadSize(new Vector3f(pScaleX, pScaleY, pScaleZ));
    }

    public Vector3f apugli$getHeadSize() {
        return apugli$headSize;
    }

    public void apugli$setHeadSize(Vector3f value) {
        apugli$headSize = value;
    }
}
