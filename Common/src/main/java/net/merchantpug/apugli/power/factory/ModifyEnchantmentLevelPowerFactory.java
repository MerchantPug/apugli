package net.merchantpug.apugli.power.factory;

import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.merchantpug.apugli.platform.Services;
import net.merchantpug.apugli.util.ComparableItemStack;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public interface ModifyEnchantmentLevelPowerFactory<P> extends ValueModifyingPowerFactory<P> {
    static SerializableData getSerializableData() {
        return ValueModifyingPowerFactory.getSerializableData()
                .add("enchantment", SerializableDataTypes.ENCHANTMENT)
                .add("item_condition", Services.CONDITION.itemDataType(), null);
    }

    default void onAdded(P power, Entity entity) {
        if (!(entity instanceof LivingEntity living)) return;
        getEntityItemEnchants().computeIfAbsent(living.getStringUUID(), (_uuid) -> new ConcurrentHashMap<>());
        ConcurrentHashMap<P, Tuple<Integer, Boolean>> cache = getPowerModifierCache().computeIfAbsent(living.getStringUUID(), (_uuid) -> new ConcurrentHashMap<>());
        cache.compute(power, (p, _val) -> new Tuple<>(0, false));
    }

    default void onRemoved(P power, Entity entity) {
        if (!(entity instanceof LivingEntity living)) return;
        getPowerModifierCache().computeIfAbsent(living.getStringUUID(), (_uuid) -> new ConcurrentHashMap<>()).remove(power);
        if (Services.POWER.getPowers(living, this).size() - 1 <= 0) {
            getEntityItemEnchants().remove(entity.getStringUUID());
        }
    }

    default boolean doesApply(P power, Enchantment enchantment, Level level, ItemStack self) {
        return enchantment.equals(getDataFromPower(power).get("enchantment")) && checkItemCondition(power, level, self);
    }

    default boolean checkItemCondition(P power, Level level, ItemStack self) {
        return !getDataFromPower(power).isPresent("item_condition") || Services.CONDITION.checkItem(getDataFromPower(power), "item_condition", level, self);
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
        Entity entity = Services.PLATFORM.getEntityFromItemStack(self);

        if(!(entity instanceof LivingEntity living)) return enchants;

        ListTag newEnchants = enchants.copy();

        List<P> powers = Services.POWER.getPowers(living, this);

        for (P power : powers) {
            Enchantment enchantment = getDataFromPower(power).get("enchantment");
            ResourceLocation id = BuiltInRegistries.ENCHANTMENT.getKey(enchantment);
            if (!doesApply(power, enchantment, entity.level(), self)) continue;
            Optional<Integer> idx = findEnchantIndex(id, newEnchants);
            if(idx.isPresent()) {
                CompoundTag existingEnchant = newEnchants.getCompound(idx.get());
                int lvl = existingEnchant.getInt("lvl");
                int newLvl = (int) Services.PLATFORM.applyModifiers(living, this.getModifiers(power, living), lvl);
                existingEnchant.putInt("lvl", newLvl);
                newEnchants.set(idx.get(), existingEnchant);
            } else {
                CompoundTag newEnchant = new CompoundTag();
                newEnchant.putString("id", id.toString());
                newEnchant.putInt("lvl", (int) Services.PLATFORM.applyModifiers(living, this.getModifiers(power, living), 0));
                newEnchants.add(newEnchant);
            }
        }

        return newEnchants;
    }

    default ListTag getEnchantments(ItemStack self, ListTag originalTag) {
        Entity entity = Services.PLATFORM.getEntityFromItemStack(self);
        if (entity instanceof LivingEntity living && getEntityItemEnchants().containsKey(entity.getStringUUID())) {
            ConcurrentHashMap<ComparableItemStack, ListTag> itemEnchants = getEntityItemEnchants().get(entity.getStringUUID());
            ComparableItemStack comparableStack = new ComparableItemStack(self.copy());
            if (shouldReapplyEnchantments(living, comparableStack)) {
                itemEnchants.computeIfAbsent(comparableStack, tag -> originalTag);
                return itemEnchants.compute(comparableStack, (tagEnchants, tag) -> generateEnchantments(originalTag, self));
            }
            return itemEnchants.getOrDefault(comparableStack, originalTag);
        }
        return originalTag;
    }

    default Map<Enchantment, Integer> getItemEnchantments(ItemStack self) {
        Entity entity = Services.PLATFORM.getEntityFromItemStack(self);
        if (entity instanceof LivingEntity living && getEntityItemEnchants().containsKey(living.getStringUUID())) {
            ConcurrentHashMap<ComparableItemStack, ListTag> itemEnchants = getEntityItemEnchants().get(entity.getStringUUID());
            ComparableItemStack comparableItemStack = new ComparableItemStack(self);
            return EnchantmentHelper.deserializeEnchantments(itemEnchants.computeIfAbsent(comparableItemStack, stack -> stack.stack().getEnchantmentTags()));
        }
        return EnchantmentHelper.getEnchantments(self);
    }

    default int getEnchantmentLevel(Enchantment enchantment, LivingEntity living) {
        if (getEntityItemEnchants().containsKey(living.getStringUUID())) {
            int i = 0;
            for (ItemStack stack : living.getAllSlots()) {
                int j = getItemEnchantmentLevel(enchantment, stack);
                if (j > i) {
                    i = j;
                }
            }
            return i;
        }
        return EnchantmentHelper.getEnchantmentLevel(enchantment, living);
    }

    default int getItemEnchantmentLevel(Enchantment enchantment, ItemStack self) {
        Entity entity = Services.PLATFORM.getEntityFromItemStack(self);
        if (entity instanceof LivingEntity living && getEntityItemEnchants().containsKey(living.getStringUUID())) {
            ConcurrentHashMap<ComparableItemStack, ListTag> itemEnchants = getEntityItemEnchants().get(entity.getStringUUID());
            ComparableItemStack comparableStack = new ComparableItemStack(self);
            ResourceLocation id = BuiltInRegistries.ENCHANTMENT.getKey(enchantment);
            ListTag newEnchants = itemEnchants.getOrDefault(comparableStack, self.getEnchantmentTags());
            Optional<Integer> idx = findEnchantIndex(id, newEnchants);
            if(idx.isPresent()) {
                CompoundTag existingEnchant = newEnchants.getCompound(idx.get());
                return existingEnchant.getInt("lvl");
            }
            return 0;
        }
        return EnchantmentHelper.getItemEnchantmentLevel(enchantment, self);
    }

    default boolean updateIfDifferent(ConcurrentHashMap<P, Tuple<Integer, Boolean>> map, P power, int modifierValue, boolean conditionValue) {
        map.computeIfAbsent(power, (p) -> new Tuple<>(0, false));
        boolean value = false;
        if (map.get(power).getA() != modifierValue) {
            map.get(power).setA(modifierValue);
            value = true;
        }
        if (map.get(power).getB() != conditionValue) {
            map.get(power).setB(conditionValue);
            value = true;
        }
        return value;
    }

    default boolean shouldReapplyEnchantments(LivingEntity living, ComparableItemStack stack) {
        List<P> powers = Services.POWER.getPowers(living, this, true);
        ConcurrentHashMap<ComparableItemStack, ListTag> enchants = getEntityItemEnchants().get(living.getStringUUID());
        ConcurrentHashMap<P, Tuple<Integer, Boolean>> cache = getPowerModifierCache().computeIfAbsent(living.getStringUUID(), (_uuid) -> new ConcurrentHashMap<>());
        return !enchants.containsKey(stack) || powers.stream().anyMatch(power -> updateIfDifferent(cache, power, (int) Services.PLATFORM.applyModifiers(living, getModifiers(power, living), 0), Services.POWER.isActive(power, living) && checkItemCondition(power, living.level(), stack.stack())));
    }

    ConcurrentHashMap<String, ConcurrentHashMap<ComparableItemStack, ListTag>> getEntityItemEnchants();
    ConcurrentHashMap<String, ConcurrentHashMap<P, Tuple<Integer, Boolean>>> getPowerModifierCache();
}
