package io.github.merchantpug.apugli.condition.entity.forge;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.ForgeMod;

import java.util.function.Predicate;

public class RaycastConditionImpl {
    public static double getReach(Entity entity, double baseReach) {
        return ((LivingEntity)entity).getAttributeValue(ForgeMod.REACH_DISTANCE.get());
    }

    public static double getAttackRange(Entity entity, double baseAttackRange) {
        return baseAttackRange;
    }
}
