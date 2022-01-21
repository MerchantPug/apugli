package io.github.merchantpug.apugli.powers.forge;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.common.ForgeMod;

public class RocketJumpPowerImpl {
    public static double getReach(Entity entity, double baseReach) {
        double attributeValue = ((LivingEntity)entity).getAttributeValue(ForgeMod.REACH_DISTANCE.get());
        return (entity instanceof PlayerEntity && ((PlayerEntity) entity).abilities.creativeMode) ? attributeValue + 0.5 : attributeValue;
    }

    public static double getAttackRange(Entity entity, double baseAttackRange) {
        return baseAttackRange;
    }
}
