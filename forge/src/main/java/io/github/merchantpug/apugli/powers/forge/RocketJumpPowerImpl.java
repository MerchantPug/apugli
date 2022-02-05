package io.github.merchantpug.apugli.powers.forge;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;

public class RocketJumpPowerImpl {
    public static double getReach(Entity entity, double baseReach) {
        double attributeValue = ((LivingEntity)entity).getAttributeValue(ForgeMod.REACH_DISTANCE.get());
        return (entity instanceof PlayerEntity && ((PlayerEntity) entity).abilities.creativeMode) ? attributeValue + 0.5 : attributeValue;
    }

    public static double getAttackRange(Entity entity, double baseAttackRange) {
        return baseAttackRange;
    }

    public static boolean isCharged(LivingEntity entity) {
        return ModList.get().isLoaded("toomanyorigins") && entity.hasStatusEffect(ForgeRegistries.POTIONS.getValue(new Identifier("toomanyorigins", "charged")));
    }
}