package net.merchantpug.apugli.util;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.material.FogType;

public class FOVUtil {

    public static double undoModifications(double d, Camera activeRenderInfo, double partialTicks) {
        FogType fogType = activeRenderInfo.getFluidInCamera();
        if (fogType == FogType.LAVA || fogType == FogType.WATER) {
            d /= Mth.lerp(Minecraft.getInstance().options.fovEffectScale().get(), 1.0, 0.8571428656578064);
        }

        if (activeRenderInfo.getEntity() instanceof LivingEntity && ((LivingEntity)activeRenderInfo.getEntity()).isDeadOrDying()) {
            double f = Math.min((float)((LivingEntity)activeRenderInfo.getEntity()).deathTime + partialTicks, 20.0F);
            d *= (1.0F - 500.0F / (f + 500.0F)) * 2.0F + 1.0F;
        }
        return d;
    }

    public static double redoModifications(double d, Camera activeRenderInfo, double partialTicks) {
        if (activeRenderInfo.getEntity() instanceof LivingEntity && ((LivingEntity)activeRenderInfo.getEntity()).isDeadOrDying()) {
            double f = Math.min((float)((LivingEntity)activeRenderInfo.getEntity()).deathTime + partialTicks, 20.0F);
            d /= (1.0F - 500.0F / (f + 500.0F)) * 2.0F + 1.0F;
        }

        FogType fogType = activeRenderInfo.getFluidInCamera();
        if (fogType == FogType.LAVA || fogType == FogType.WATER) {
            d *= Mth.lerp(Minecraft.getInstance().options.fovEffectScale().get(), 1.0, 0.8571428656578064);
        }
        return d;
    }

}
