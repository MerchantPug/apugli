package io.github.merchantpug.apugli.condition.entity.fabric;

import com.jamieswhiteshirt.reachentityattributes.ReachEntityAttributes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.function.Predicate;

public class RaycastConditionImpl {
    public static double getReach(Entity entity, double baseReach) {
        return entity instanceof LivingEntity ? ReachEntityAttributes.getReachDistance((LivingEntity)entity, baseReach) : baseReach;
    }

    public static double getAttackRange(Entity entity, double baseAttackRange) {
        return entity instanceof LivingEntity ? ReachEntityAttributes.getAttackRange((LivingEntity)entity, baseAttackRange) : baseAttackRange;
    }
}
