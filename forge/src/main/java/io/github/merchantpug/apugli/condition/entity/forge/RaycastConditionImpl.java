package io.github.merchantpug.apugli.condition.entity.forge;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraftforge.common.ForgeMod;

public class RaycastConditionImpl {
    public static double getReach(Entity entity, double baseReach) {
        return ((LivingEntity)entity).getAttributeValue(ForgeMod.REACH_DISTANCE.get());
    }

    public static double getAttackRange(Entity entity, double baseAttackRange) {
        return baseAttackRange;
    }
}
