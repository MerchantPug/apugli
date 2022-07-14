package com.github.merchantpug.apugli.powers;

import io.github.apace100.origins.component.OriginComponent;
import io.github.apace100.origins.power.PowerType;
import io.github.apace100.origins.power.ValueModifyingPower;
import io.github.apace100.origins.power.factory.PowerFactory;
import io.github.apace100.origins.util.SerializableData;
import io.github.apace100.origins.util.SerializableDataType;
import com.github.merchantpug.apugli.Apugli;
import com.github.merchantpug.apugli.access.ItemStackAccess;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class ModifyEnchantmentLevelPower extends ValueModifyingPower {
    private static final ConcurrentHashMap<String, ConcurrentHashMap<ListTag, ListTag>> entityItemEnchants = new ConcurrentHashMap<>();
    private final Enchantment enchantment;

    public ModifyEnchantmentLevelPower(PowerType<?> type, PlayerEntity player, Enchantment enchantment) {
        super(type, player);
        this.enchantment = enchantment;
    }

    public Enchantment getEnchantment() {
        return this.enchantment;
    }

    public boolean doesApply(Enchantment enchantment) {
        return enchantment.equals(this.enchantment);
    }

    private static Optional<Integer> findEnchantIndex(Identifier id, ListTag enchants) {
        for (int i = 0; i < enchants.size(); ++i) {
            String string = enchants.getCompound(i).getString("id");
            Identifier enchantId = Identifier.tryParse(string);
            if (enchantId != null && enchantId.equals(id)) {
                return Optional.of(i);
            }
        }
        return Optional.empty();
    }

    public static ListTag generateEnchantments(ListTag enchants, ItemStack self) {
        Entity entity = ((ItemStackAccess) (Object) self).getEntity();

        if (!(entity instanceof PlayerEntity)) return enchants;

        ListTag newEnchants = enchants.copy();
        List<ModifyEnchantmentLevelPower> powers = OriginComponent.getPowers(entity, ModifyEnchantmentLevelPower.class);

        if (powers.isEmpty()) return enchants;
        
        for (ModifyEnchantmentLevelPower power : powers) {
            Identifier id = Registry.ENCHANTMENT.getId(power.getEnchantment());
            Optional<Integer> idx = findEnchantIndex(id, newEnchants);
            if (idx.isPresent()) {
                CompoundTag existingEnchant = newEnchants.getCompound(idx.get());
                int lvl = existingEnchant.getInt("lvl");
                int newLvl = (int) OriginComponent.modify(entity, ModifyEnchantmentLevelPower.class, lvl, powerFilter -> powerFilter.doesApply(power.getEnchantment()));
                existingEnchant.putInt("lvl", newLvl);
                newEnchants.set(idx.get(), existingEnchant);
            } else {
                CompoundTag newEnchant = new CompoundTag();
                newEnchant.putString("id", id.toString());
                newEnchant.putInt("lvl", (int) OriginComponent.modify(entity, ModifyEnchantmentLevelPower.class, 0, powerFilter -> powerFilter.doesApply(power.getEnchantment())));
                newEnchants.add(newEnchant);
            }
        };
        return newEnchants;
    }

    public static void updateEnchantments(ItemStack self) {
        Entity entity = ((ItemStackAccess) (Object) self).getEntity();
        if (entity == null) return;
        ConcurrentHashMap<ListTag, ListTag> itemEnchants = entityItemEnchants.computeIfAbsent(entity.getUuidAsString(), (_uuid) -> new ConcurrentHashMap<>());
        ListTag tag = self.getEnchantments();
        ListTag enchantments = ModifyEnchantmentLevelPower.generateEnchantments(tag, self);
        itemEnchants.put(tag, enchantments);
    }

    public static ListTag getEnchantments(ItemStack self) {
        Entity entity = ((ItemStackAccess) (Object) self).getEntity();
        if (entity == null) return self.getEnchantments();
        ConcurrentHashMap<ListTag, ListTag> itemEnchants = entityItemEnchants.computeIfAbsent(entity.getUuidAsString(), (_uuid) -> new ConcurrentHashMap<>());
        ListTag enchants = itemEnchants.compute(self.getEnchantments(), (tag, tag2) -> ModifyEnchantmentLevelPower.generateEnchantments(tag, self));
        return enchants;
    }

    public static PowerFactory<?> getFactory() {
        return new PowerFactory<ModifyEnchantmentLevelPower>(
                Apugli.identifier("modify_enchantment_level"),
                new SerializableData()
                        .add("enchantment", SerializableDataType.ENCHANTMENT)
                        .add("modifier", SerializableDataType.ATTRIBUTE_MODIFIER, null)
                        .add("modifiers", SerializableDataType.ATTRIBUTE_MODIFIERS, null),
                data -> (type, player) -> {
                    ModifyEnchantmentLevelPower power = new ModifyEnchantmentLevelPower(type, player, (Enchantment) data.get("enchantment"));
                    if (data.isPresent("modifier")) {
                        power.addModifier((EntityAttributeModifier) data.get("modifier"));
                    }
                    if (data.isPresent("modifiers")) {
                        ((List<EntityAttributeModifier>)data.get("modifiers")).forEach(power::addModifier);
                    }
                    return power;
                })
                .allowCondition();
    }
}