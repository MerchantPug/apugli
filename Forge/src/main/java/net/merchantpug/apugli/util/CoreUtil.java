package net.merchantpug.apugli.util;

import net.merchantpug.apugli.platform.Services;
import net.merchantpug.apugli.power.EdibleItemPower;
import net.merchantpug.apugli.registry.power.ApugliPowers;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class CoreUtil {

    public static boolean doEdibleItemPowersApply(ItemStack stack, @Nullable LivingEntity entity) {
        return entity != null && Services.POWER.getPowers(entity, ApugliPowers.EDIBLE_ITEM.get()).stream().anyMatch(p -> p.doesApply(stack));
    }

    public static FoodProperties getEdibleItemPowerFoodProperties(ItemStack stack, @Nullable LivingEntity entity) {
        Optional<EdibleItemPower> power = Services.POWER.getPowers(entity, ApugliPowers.EDIBLE_ITEM.get()).stream().filter(p -> p.doesApply(stack)).findFirst();
        return power.map(EdibleItemPower::getFoodComponent).orElse(stack.getItem().getFoodProperties(stack, entity));
    }

}
