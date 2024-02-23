package net.merchantpug.apugli.util;

import net.merchantpug.apugli.access.ItemStackAccess;
import net.merchantpug.apugli.platform.Services;
import net.merchantpug.apugli.power.EdibleItemPower;
import net.merchantpug.apugli.registry.power.ApugliPowers;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class CoreUtil {

    public static int getModifiedEnchantmentLevel(int original, ItemStack stack, Enchantment enchantment) {
        if (((ItemStackAccess)(Object)stack).apugli$getEntity() instanceof LivingEntity living && ApugliPowers.MODIFY_ENCHANTMENT_LEVEL.get().getEntityItemEnchants().containsKey(living.getUUID())) {
            return (int) Services.PLATFORM.applyModifiers(living, ApugliPowers.MODIFY_ENCHANTMENT_LEVEL.get(), original, p -> ApugliPowers.MODIFY_ENCHANTMENT_LEVEL.get().doesApply(p, enchantment, living.level(), stack));
        }
        return original;
    }

    public static Map<Enchantment, Integer> getModifiedEnchantments(Map<Enchantment, Integer> original, ItemStack stack) {
        if (((ItemStackAccess)(Object)stack).apugli$getEntity() instanceof LivingEntity living && ApugliPowers.MODIFY_ENCHANTMENT_LEVEL.get().getEntityItemEnchants().containsKey(living.getUUID())) {
            ListTag serializedEnchantments = serializeEnchantments(original);
            return EnchantmentHelper.deserializeEnchantments(ApugliPowers.MODIFY_ENCHANTMENT_LEVEL.get().getEnchantments(stack, serializedEnchantments));
        }
        return original;
    }

    private static ListTag serializeEnchantments(Map<Enchantment, Integer> deserialized) {
        ListTag tag = new ListTag();
        List<Map.Entry<Enchantment, Integer>> entryList = deserialized.entrySet().stream().toList();
        for (int i = 0; i < entryList.size(); ++i) {
            ResourceLocation key = EnchantmentHelper.getEnchantmentId(entryList.get(i).getKey());
            if (key == null) continue;
            tag.add(i, EnchantmentHelper.storeEnchantment(key, entryList.get(i).getValue()));
        }
        return tag;
    }

    public static boolean doEdibleItemPowersApply(ItemStack stack, @Nullable LivingEntity entity) {
        return entity != null && Services.POWER.getPowers(entity, ApugliPowers.EDIBLE_ITEM.get()).stream().anyMatch(p -> p.doesApply(entity.level(), stack));
    }

    public static FoodProperties getEdibleItemPowerFoodProperties(ItemStack stack, @Nullable LivingEntity entity) {
        if (entity == null) {
            return stack.getItem().getFoodProperties(stack, null);
        }
        Optional<EdibleItemPower> power = Services.POWER.getPowers(entity, ApugliPowers.EDIBLE_ITEM.get()).stream().filter(p -> p.doesApply(entity.level(), stack)).findFirst();
        return power.map(EdibleItemPower::getFoodComponent).orElse(stack.getItem().getFoodProperties(stack, entity));
    }

}
