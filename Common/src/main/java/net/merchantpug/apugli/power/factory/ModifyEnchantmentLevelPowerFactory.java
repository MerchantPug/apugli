package net.merchantpug.apugli.power.factory;

import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.merchantpug.apugli.Apugli;
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

public interface ModifyEnchantmentLevelPowerFactory<P> extends ValueModifyingPowerFactory<P> {

    static SerializableData getSerializableData() {
        return ValueModifyingPowerFactory.getSerializableData()
                .add("enchantment", SerializableDataTypes.ENCHANTMENT);
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

    default boolean doesApply(P power, Enchantment enchantment) {
        return enchantment.equals(getDataFromPower(power).get("enchantment"));
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

        List<P> powers = Services.POWER.getPowers(living, ApugliPowers.MODIFY_ENCHANTMENT_LEVEL.get());

        for (P power : powers) {
            Enchantment enchantment = getDataFromPower(power).get("enchantment");
            ResourceLocation id = Registry.ENCHANTMENT.getKey(enchantment);
            Optional<Integer> idx = findEnchantIndex(id, newEnchants);
            if(idx.isPresent()) {
                CompoundTag existingEnchant = newEnchants.getCompound(idx.get());
                int lvl = existingEnchant.getInt("lvl");
                int newLvl = (int) Services.PLATFORM.applyModifiers(living, ApugliPowers.MODIFY_ENCHANTMENT_LEVEL.get(), lvl, p -> ApugliPowers.MODIFY_ENCHANTMENT_LEVEL.get().doesApply(p, enchantment));
                existingEnchant.putInt("lvl", newLvl);
                newEnchants.set(idx.get(), existingEnchant);
            } else {
                CompoundTag newEnchant = new CompoundTag();
                newEnchant.putString("id", id.toString());
                newEnchant.putInt("lvl", (int) Services.PLATFORM.applyModifiers(living, ApugliPowers.MODIFY_ENCHANTMENT_LEVEL.get(), 0, p -> ApugliPowers.MODIFY_ENCHANTMENT_LEVEL.get().doesApply(p, enchantment)));
                newEnchants.add(newEnchant);
            }
            ConcurrentHashMap<ListTag, ConcurrentHashMap<P, Pair<Integer, Boolean>>> currentAndPreviousModifiers = getPreviousPowerState().computeIfAbsent(living.getStringUUID(), (_uuid) -> new ConcurrentHashMap<>());
            currentAndPreviousModifiers.computeIfAbsent(self.getEnchantmentTags(), (tag) -> new ConcurrentHashMap<>());
            currentAndPreviousModifiers.get(self.getEnchantmentTags()).put(power, Pair.of((int) Services.PLATFORM.applyModifiers(living, ApugliPowers.MODIFY_ENCHANTMENT_LEVEL.get(), 0), Services.POWER.isActive(power, living)));
        }

        return newEnchants;
    }

    default ListTag getEnchantments(ItemStack self, ListTag originalTag) {
        Entity entity = ((ItemStackAccess) (Object) self).getEntity();
        if (entity != null) {
            if (entity instanceof LivingEntity living) {
                ConcurrentHashMap<ListTag, ListTag> itemEnchants = getEntityItemEnchants().computeIfAbsent(entity.getStringUUID(), (_uuid) -> new ConcurrentHashMap<>());
                itemEnchants.computeIfAbsent(originalTag, (tag) -> tag);
                if (shouldReapplyEnchantments(living, self)) {
                    return itemEnchants.compute(originalTag, (tag, tag2) -> generateEnchantments(tag, self));
                }
            }
        }
        return originalTag;
    }

    default boolean shouldReapplyEnchantments(LivingEntity living, ItemStack self) {
        ConcurrentHashMap<ListTag, ConcurrentHashMap<P, Pair<Integer, Boolean>>> previousModifierValue = getPreviousPowerState().computeIfAbsent(living.getStringUUID(), (_uuid) -> new ConcurrentHashMap<>());
        previousModifierValue.computeIfAbsent(self.getEnchantmentTags(), (tag) -> new ConcurrentHashMap<>());
        ConcurrentHashMap<P, Pair<Integer, Boolean>> powerMap = previousModifierValue.computeIfAbsent(self.getEnchantmentTags(), (tag) -> new ConcurrentHashMap<>());
        Apugli.LOG.info("Are any modifiers different?: " + powerMap.entrySet().stream().anyMatch(entry -> Services.PLATFORM.applyModifiers(living, getModifiers(entry.getKey(), living), 0) != entry.getValue().getLeft()));
        Apugli.LOG.info("Is any active state different?: " + powerMap.entrySet().stream().anyMatch(entry ->Services.POWER.isActive(entry.getKey(), living) != entry.getValue().getRight()));
        return powerMap.entrySet().stream().anyMatch(entry -> Services.PLATFORM.applyModifiers(living, getModifiers(entry.getKey(), living), 0) != entry.getValue().getLeft() || Services.POWER.isActive(entry.getKey(), living) != entry.getValue().getRight());
    }

    ConcurrentHashMap<String, ConcurrentHashMap<ListTag, ListTag>> getEntityItemEnchants();

    ConcurrentHashMap<String, ConcurrentHashMap<ListTag, ConcurrentHashMap<P, Pair<Integer, Boolean>>>> getPreviousPowerState();

}
