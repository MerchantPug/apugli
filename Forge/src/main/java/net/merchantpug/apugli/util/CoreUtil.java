package net.merchantpug.apugli.util;

import net.merchantpug.apugli.mixin.xplatform.common.accessor.ItemAccessor;
import net.merchantpug.apugli.platform.Services;
import net.merchantpug.apugli.power.EdibleItemPower;
import net.merchantpug.apugli.registry.power.ApugliPowers;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class CoreUtil {

    public static boolean doEdibleItemPowersApply(ItemStack stack, @Nullable LivingEntity entity) {
        return entity != null && Services.POWER.getPowers(entity, ApugliPowers.EDIBLE_ITEM.get()).stream().anyMatch(p -> p.doesApply(stack));
    }

    public static FoodProperties getEdibleItemPowerFoodProperties(ItemStack stack, LivingEntity entity) {
        Optional<EdibleItemPower> power = Services.POWER.getPowers(entity, ApugliPowers.EDIBLE_ITEM.get()).stream().filter(p -> p.doesApply(stack)).findFirst();
        if (entity instanceof Player player && stack.getItem() instanceof BucketItem bucketItem) {
            BlockHitResult blockHitResult = ItemAccessor.callRaycast(entity.getLevel(), player, bucketItem.getFluid() == Fluids.EMPTY ? ClipContext.Fluid.SOURCE_ONLY : ClipContext.Fluid.NONE);
            if (blockHitResult.getType() == HitResult.Type.BLOCK) {
                return stack.getItem().getFoodProperties(stack, entity);
            }
        }
        return power.map(EdibleItemPower::getFoodComponent).orElse(null);
    }

}
