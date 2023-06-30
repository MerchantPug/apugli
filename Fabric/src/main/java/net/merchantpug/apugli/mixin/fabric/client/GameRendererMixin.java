package net.merchantpug.apugli.mixin.fabric.client;

import net.merchantpug.apugli.registry.power.ApugliPowers;
import net.merchantpug.apugli.util.FOVUtil;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

    @Inject(method = "getFov", at = @At(value = "RETURN", ordinal = 1), cancellable = true)
    private void modifyFov(Camera activeRenderInfo, float partialTicks, boolean useFOVSetting, CallbackInfoReturnable<Double> cir) {

        if (useFOVSetting && activeRenderInfo.getEntity() instanceof LivingEntity living) {
            double fov = FOVUtil.undoModifications(cir.getReturnValue(), activeRenderInfo, partialTicks);
            cir.setReturnValue(ApugliPowers.MODIFY_FOV.get().getFov(fov, activeRenderInfo, living));
        }
    }
}
