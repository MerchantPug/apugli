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
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

// TODO: Correct my attempt at caching.
public interface ModifyEnchantmentLevelPowerFactory<P> extends ValueModifyingPowerFactory<P> {

    static SerializableData getSerializableData() {
        return ValueModifyingPowerFactory.getSerializableData()
                .add("enchantment", SerializableDataTypes.ENCHANTMENT)
                .add("item_condition", Services.CONDITION.itemDataType());
    }

    default void onRemoved(P power, Entity entity) {
        if (!(entity instanceof LivingEntity living)) return;
        if (!Services.POWER.hasPower(living, this)) {
            getEntityItemEnchants().remove(entity.getStringUUID());
            getPreviousPowerState().remove(entity.getStringUUID());
        } else {
            for (Map.Entry<ListTag, ConcurrentHashMap<P, Pair<Integer, Boolean>>> map : getPreviousPowerState().get(living.getStringUUID()).entrySet()) {
                map.getValue().remove(power);
            }
        }
    }

    default boolean doesApply(P power, Enchantment enchantment, ItemStack self) {
        return enchantment.equals(getDataFromPower(power).get("enchantment")) && Services.CONDITION.checkItem(getDataFromPower(power), "item_condition", self);
    }

    default boolean checkItemCondition(P power, ItemStack self) {
        return Services.CONDITION.checkItem(getDataFromPower(power), "item_condition", self);
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

        List<P> powers = Services.POWER.getPowers(living, this, true);

        for (P power : powers) {
            ConcurrentHashMap<P, Pair<Integer, Boolean>>  previousPowerState = getPreviousPowerState().computeIfAbsent(living.getStringUUID(), (_uuid) -> new ConcurrentHashMap<>()).computeIfAbsent(self.getEnchantmentTags(), (tag) -> new ConcurrentHashMap<>());
            previousPowerState.computeIfAbsent(power, (p) -> Pair.of((int) Services.PLATFORM.applyModifiers(living, ApugliPowers.MODIFY_ENCHANTMENT_LEVEL.get(), 0), Services.POWER.isActive(p, living) && checkItemCondition(p, self)));

            if (!Services.POWER.isActive(power, living)) continue;

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

    default ListTag getEnchantments(ItemStack self, ListTag originalTag) {
        Entity entity = ((ItemStackAccess) (Object) self).getEntity();
        if (entity instanceof LivingEntity living) {
            ConcurrentHashMap<ListTag, ListTag> itemEnchants = getEntityItemEnchants().computeIfAbsent(entity.getStringUUID(), (_uuid) -> new ConcurrentHashMap<>());
            if (shouldReapplyEnchantments(living, self, originalTag)) {
                return itemEnchants.compute(originalTag, (tag, tag2) -> generateEnchantments(tag, self));
            }
            return itemEnchants.get(originalTag);
        }
        return originalTag;
    }

    default boolean shouldReapplyEnchantments(LivingEntity living, ItemStack self, ListTag originalTag) {
        getPreviousPowerState().computeIfAbsent(living.getStringUUID(), (_uuid) -> new ConcurrentHashMap<>());
        ConcurrentHashMap<P, Pair<Integer, Boolean>> powerMap = getPreviousPowerState().get(living.getStringUUID()).computeIfAbsent(originalTag, (tag) -> new ConcurrentHashMap<>());
        if (powerMap.isEmpty()) {
            for (P power : Services.POWER.getPowers(living, this, true)) {
                powerMap.computeIfAbsent(power, p -> Pair.of(0, false));
            }
        }
        return powerMap.entrySet().stream().anyMatch(entry -> Services.PLATFORM.applyModifiers(living, getModifiers(entry.getKey(), living), 0) != entry.getValue().getLeft() || Services.POWER.isActive(entry.getKey(), living) && checkItemCondition(entry.getKey(), self) != entry.getValue().getRight());
    }

    ConcurrentHashMap<String, ConcurrentHashMap<ListTag, ListTag>> getEntityItemEnchants();

    ConcurrentHashMap<String, ConcurrentHashMap<ListTag, ConcurrentHashMap<P, Pair<Integer, Boolean>>>> getPreviousPowerState();

}
