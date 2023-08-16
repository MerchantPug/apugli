package net.merchantpug.apugli.mixin.forge.common;

import com.mojang.math.Vector3f;
import net.merchantpug.apugli.access.HumanoidMobRendererAccess;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidMobRenderer.class)
@Implements(@Interface(iface = HumanoidMobRendererAccess.class, prefix = "apugli$"))
public class HumanoidMobRendererMixin {
    @Unique
    public Vector3f apugli$headSize;

    @Inject(method = "<init>(Lnet/minecraft/client/renderer/entity/EntityRendererProvider$Context;Lnet/minecraft/client/model/HumanoidModel;FFFF)V", at = @At("TAIL"))
    private void captureHeadSize(EntityRendererProvider.Context pContext, HumanoidModel pModel, float pShadowRadius, float pScaleX, float pScaleY, float pScaleZ, CallbackInfo ci) {
        apugli$headSize = new Vector3f(pScaleX, pScaleY, pScaleZ);
    }

    public Vector3f apugli$getHeadSize() {
        return apugli$headSize;
    }

    public void apugli$setHeadSize(Vector3f value) {
        apugli$headSize = value;
    }
}
