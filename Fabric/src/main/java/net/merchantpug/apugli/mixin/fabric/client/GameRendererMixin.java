package net.merchantpug.apugli.mixin.fabric.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.merchantpug.apugli.platform.Services;
import net.merchantpug.apugli.registry.power.ApugliPowers;
import net.merchantpug.apugli.util.FOVUtil;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

    @Unique
    private Camera apugli$capturedActiveRenderInfo;
    @Unique
    private float apugli$capturedPartialTicks;
    @Unique
    private boolean apugli$capturedUseFOVSetting;


    @Inject(method = "getFov", at = @At("HEAD"))
    private void captureFovValues(Camera activeRenderInfo, float partialTicks, boolean useFOVSetting, CallbackInfoReturnable<Double> cir) {
        this.apugli$capturedActiveRenderInfo = activeRenderInfo;
        this.apugli$capturedPartialTicks = partialTicks;
        this.apugli$capturedUseFOVSetting = useFOVSetting;
    }

    @ModifyReturnValue(method = "getFov", at = @At(value = "RETURN", ordinal = 1))
    private double modifyFov(double original) {
        if (this.apugli$capturedUseFOVSetting && this.apugli$capturedActiveRenderInfo.getEntity() instanceof LivingEntity living) {
            double fov = FOVUtil.undoModifications(original, this.apugli$capturedActiveRenderInfo, this.apugli$capturedPartialTicks);
            double retVal = ApugliPowers.MODIFY_FOV.get().getFov(fov, this.apugli$capturedActiveRenderInfo, living);
            this.apugli$capturedActiveRenderInfo = null;
            return retVal;
        }
        return original;
    }

    @ModifyExpressionValue(method = "method_18144", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;isPickable()Z"))
    private static boolean preventPickingOfPreventedEntities(boolean original, Entity entity) {
        return original && Services.POWER.getPowers(Minecraft.getInstance().player, ApugliPowers.PREVENT_ENTITY_SELECTION.get()).stream().noneMatch(p -> p.shouldPrevent(entity));
    }
}
