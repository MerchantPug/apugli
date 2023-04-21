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

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public interface ModifyEnchantmentLevelPowerFactory<P> extends ValueModifyingPowerFactory<P> {
    ConcurrentHashMap<String, ConcurrentHashMap<ListTag, ListTag>> ENTITY_ITEM_ENCHANTS = new ConcurrentHashMap<>();
    
    static SerializableData getSerializableData() {
        return ValueModifyingPowerFactory.getSerializableData()
                .add("enchantment", SerializableDataTypes.ENCHANTMENT);
    }

    boolean doesApply(P power, Entity entity, Enchantment enchantment);

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
                int newLvl = (int) Services.PLATFORM.applyModifiers(living, ApugliPowers.MODIFY_ENCHANTMENT_LEVEL.get(), lvl, p -> ApugliPowers.MODIFY_ENCHANTMENT_LEVEL.get().doesApply(p, entity, enchantment));
                existingEnchant.putInt("lvl", newLvl);
                newEnchants.set(idx.get(), existingEnchant);
            } else {
                CompoundTag newEnchant = new CompoundTag();
                newEnchant.putString("id", id.toString());
                newEnchant.putInt("lvl", (int) Services.PLATFORM.applyModifiers(living, ApugliPowers.MODIFY_ENCHANTMENT_LEVEL.get(), 0, p -> ApugliPowers.MODIFY_ENCHANTMENT_LEVEL.get().doesApply(p, entity, enchantment)));
                newEnchants.add(newEnchant);
            }
        }

        return newEnchants;
    }


    default ListTag getEnchantments(ItemStack self) {
        Entity entity = ((ItemStackAccess) (Object) self).getEntity();
        if(entity == null) return self.getEnchantmentTags();
        ConcurrentHashMap<ListTag, ListTag> itemEnchants = ENTITY_ITEM_ENCHANTS.computeIfAbsent(entity.getStringUUID(), (_uuid) -> new ConcurrentHashMap<>());
        return itemEnchants.compute(self.getEnchantmentTags(), (tag, tag2) -> generateEnchantments(tag, self));
    }

}
