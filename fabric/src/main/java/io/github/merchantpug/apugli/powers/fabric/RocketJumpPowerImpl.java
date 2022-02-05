package io.github.merchantpug.apugli.powers.fabric;

import com.jamieswhiteshirt.reachentityattributes.ReachEntityAttributes;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class RocketJumpPowerImpl {
    public static double getReach(Entity entity, double baseReach) {
        return entity instanceof LivingEntity ? ReachEntityAttributes.getReachDistance((LivingEntity)entity, baseReach) : baseReach;
    }

    public static double getAttackRange(Entity entity, double baseAttackRange) {
        return entity instanceof LivingEntity ? ReachEntityAttributes.getAttackRange((LivingEntity)entity, baseAttackRange) : baseAttackRange;
    }

    public static boolean isCharged(LivingEntity entity) {
        boolean tmoCharged = FabricLoader.getInstance().isModLoaded("toomanyorigins") && entity.hasStatusEffect(Registry.STATUS_EFFECT.get(new Identifier("toomanyorigins", "charged")));
        boolean cursedCharged = FabricLoader.getInstance().isModLoaded("cursedorigins") && entity.hasStatusEffect(Registry.STATUS_EFFECT.get(new Identifier("cursedorigins", "charged")));
        return tmoCharged || cursedCharged;
    }
}
