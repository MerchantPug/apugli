package net.merchantpug.apugli.power.factory;

import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.merchantpug.apugli.access.ItemStackAccess;
import net.merchantpug.apugli.platform.Services;
import net.merchantpug.apugli.registry.power.ApugliPowers;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public interface ModifyEnchantmentLevelPowerFactory<P> extends ValueModifyingPowerFactory<P> {

    static SerializableData getSerializableData() {
        return ValueModifyingPowerFactory.getSerializableData()
                .add("enchantment", SerializableDataTypes.ENCHANTMENT)
                .add("item_condition", Services.CONDITION.itemDataType(), null);
    }

    default void onAdded(P power, Entity entity) {
        if (!(entity instanceof LivingEntity living)) return;
        ConcurrentHashMap<P, Integer> cache = getPowerModifierCache().computeIfAbsent(living.getStringUUID(), (_uuid) -> new ConcurrentHashMap<>());
        cache.compute(power, (p, _val) -> (int) Services.PLATFORM.applyModifiers(entity, getModifiers(p, entity), 0));
    };

    default void onRemoved(P power, Entity entity) {
        if (!(entity instanceof LivingEntity living)) return;
        getPowerModifierCache().computeIfAbsent(living.getStringUUID(), (_uuid) -> new ConcurrentHashMap<>()).remove(power);
        if (Services.POWER.getPowers(living, this).size() - 1 <= 0) {
            getEntityItemEnchants().remove(entity.getStringUUID());
        }
    }

    default boolean doesApply(P power, Enchantment enchantment, ItemStack self) {
        return enchantment.equals(getDataFromPower(power).get("enchantment")) && checkItemCondition(power, self);
    }

    default boolean checkItemCondition(P power, ItemStack self) {
        return !getDataFromPower(power).isPresent("item_condition") || Services.CONDITION.checkItem(getDataFromPower(power), "item_condition", self);
    }

    default Optional<Integer> findEnchantIndex(ResourceLocation id, ListTag enchants) {
        for(int i = 0; i < enchants.size(); ++i) {
            String string = enchants.getCompound(i).getString("id");
            ResourceLocation enchantId = ResourceLocation.tryParse(string);
            if(enchantId != null && enchantId.equals(id)) {
                return Optional.of(i);
            }
        }
        return Optional.empty();
    }

    default ListTag generateEnchantments(ListTag enchants, ItemStack self) {
        Entity entity = ((ItemStackAccess) (Object) self).getEntity();

        if(!(entity instanceof LivingEntity living)) return enchants;

        ListTag newEnchants = enchants.copy();

        List<P> powers = Services.POWER.getPowers(living, this);

        for (P power : powers) {
            Enchantment enchantment = getDataFromPower(power).get("enchantment");
            ResourceLocation id = Registry.ENCHANTMENT.getKey(enchantment);
            Optional<Integer> idx = findEnchantIndex(id, newEnchants);
            if(idx.isPresent()) {
                CompoundTag existingEnchant = newEnchants.getCompound(idx.get());
                int lvl = existingEnchant.getInt("lvl");
                int newLvl = (int) Services.PLATFORM.applyModifiers(living, ApugliPowers.MODIFY_ENCHANTMENT_LEVEL.get(), lvl, p -> ApugliPowers.MODIFY_ENCHANTMENT_LEVEL.get().doesApply(p, enchantment, self));
                existingEnchant.putInt("lvl", newLvl);
                newEnchants.set(idx.get(), existingEnchant);
            } else {
                CompoundTag newEnchant = new CompoundTag();
                newEnchant.putString("id", id.toString());
                newEnchant.putInt("lvl", (int) Services.PLATFORM.applyModifiers(living, ApugliPowers.MODIFY_ENCHANTMENT_LEVEL.get(), 0, p -> ApugliPowers.MODIFY_ENCHANTMENT_LEVEL.get().doesApply(p, enchantment, self)));
                newEnchants.add(newEnchant);
            }
        }

        return newEnchants;
    }

    // TODO: Generate enchantments when item condition state changes or when the power's active state changes.
    default ListTag getEnchantments(ItemStack self, ListTag originalTag) {
        Entity entity = ((ItemStackAccess) (Object) self).getEntity();
        if (entity instanceof LivingEntity living) {
            ConcurrentHashMap<ListTag, ListTag> itemEnchants = getEntityItemEnchants().computeIfAbsent(entity.getStringUUID(), (_uuid) -> new ConcurrentHashMap<>());
            if (shouldReapplyEnchantments(living, originalTag)) {
                itemEnchants.computeIfAbsent(originalTag, tag -> tag);
                return itemEnchants.compute(originalTag, (tag, tag2) -> generateEnchantments(tag, self));
            }
            return itemEnchants.getOrDefault(originalTag, originalTag);
        }
        return originalTag;
    }

    default <K, V> boolean updateIfDifferent(ConcurrentHashMap<K, V> map, K power, V value) {
        if (map.get(power) != value) {
            map.put(power, value);
            return true;
        }
        return false;
    }

    default boolean shouldReapplyEnchantments(LivingEntity living, ListTag originalTag) {
        List<P> powers = Services.POWER.getPowers(living, this, true);
        ConcurrentHashMap<ListTag, ListTag> enchants = getEntityItemEnchants().computeIfAbsent(living.getStringUUID(), (_uuid) -> new ConcurrentHashMap<>());
        ConcurrentHashMap<P, Integer> cache = getPowerModifierCache().computeIfAbsent(living.getStringUUID(), (_uuid) -> new ConcurrentHashMap<>());
        return !enchants.containsKey(originalTag) || powers.stream().anyMatch(power -> updateIfDifferent(cache, power, (int) Services.PLATFORM.applyModifiers(living, getModifiers(power, living), 0)));
    }

    ConcurrentHashMap<String, ConcurrentHashMap<ListTag, ListTag>> getEntityItemEnchants();
    ConcurrentHashMap<String, ConcurrentHashMap<P, Integer>> getPowerModifierCache();
}
