package com.github.merchantpug.apugli.power;

import com.github.merchantpug.apugli.Apugli;
import com.github.merchantpug.apugli.access.ItemStackAccess;
import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.ValueModifyingPower;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.apoli.util.modifier.Modifier;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class ModifyEnchantmentLevelPower extends ValueModifyingPower {
    private static final ConcurrentHashMap<String, ConcurrentHashMap<NbtList, NbtList>> ENTITY_ITEM_ENCHANTS = new ConcurrentHashMap<>();
    private final Enchantment enchantment;

    public ModifyEnchantmentLevelPower(PowerType<?> type, LivingEntity player, Enchantment enchantment) {
        super(type, player);
        this.enchantment = enchantment;
    }

    public Enchantment getEnchantment() {
        return this.enchantment;
    }

    public boolean doesApply(Enchantment enchantment) {
        return enchantment.equals(this.enchantment);
    }

    private static Optional<Integer> findEnchantIndex(Identifier id, NbtList enchants) {
        for (int i = 0; i < enchants.size(); ++i) {
            String string = enchants.getCompound(i).getString("id");
            Identifier enchantId = Identifier.tryParse(string);
            if (enchantId != null && enchantId.equals(id)) {
                return Optional.of(i);
            }
        }
        return Optional.empty();
    }

    public static NbtList generateEnchantments(NbtList enchants, ItemStack self) {
        Entity entity = ((ItemStackAccess) (Object) self).getEntity();

        if (!(entity instanceof PlayerEntity)) return enchants;

        NbtList newEnchants = enchants.copy();
        List<ModifyEnchantmentLevelPower> powers = PowerHolderComponent.getPowers(entity, ModifyEnchantmentLevelPower.class);

        if (powers.isEmpty()) return enchants;
        
        for (ModifyEnchantmentLevelPower power : powers) {
            Identifier id = Registry.ENCHANTMENT.getId(power.getEnchantment());
            Optional<Integer> idx = findEnchantIndex(id, newEnchants);
            if (idx.isPresent()) {
                NbtCompound existingEnchant = newEnchants.getCompound(idx.get());
                int lvl = existingEnchant.getInt("lvl");
                int newLvl = (int) PowerHolderComponent.modify(entity, ModifyEnchantmentLevelPower.class, lvl, powerFilter -> powerFilter.doesApply(power.getEnchantment()));
                existingEnchant.putInt("lvl", newLvl);
                newEnchants.set(idx.get(), existingEnchant);
            } else {
                NbtCompound newEnchant = new NbtCompound();
                newEnchant.putString("id", id.toString());
                newEnchant.putInt("lvl", (int) PowerHolderComponent.modify(entity, ModifyEnchantmentLevelPower.class, 0, powerFilter -> powerFilter.doesApply(power.getEnchantment())));
                newEnchants.add(newEnchant);
            }
        };
        return newEnchants;
    }

    public static void updateEnchantments(ItemStack self) {
        Entity entity = ((ItemStackAccess) (Object) self).getEntity();
        if (entity == null) return;
        ConcurrentHashMap<NbtList, NbtList> itemEnchants = ENTITY_ITEM_ENCHANTS.computeIfAbsent(entity.getUuidAsString(), (_uuid) -> new ConcurrentHashMap<>());
        NbtList tag = self.getEnchantments();
        NbtList enchantments = ModifyEnchantmentLevelPower.generateEnchantments(tag, self);
        itemEnchants.put(tag, enchantments);
    }

    public static NbtList getEnchantments(ItemStack self) {
        Entity entity = ((ItemStackAccess) (Object) self).getEntity();
        if (entity == null) return self.getEnchantments();
        ConcurrentHashMap<NbtList, NbtList> itemEnchants = ENTITY_ITEM_ENCHANTS.computeIfAbsent(entity.getUuidAsString(), (_uuid) -> new ConcurrentHashMap<>());
        NbtList enchants = itemEnchants.compute(self.getEnchantments(), (tag, tag2) -> ModifyEnchantmentLevelPower.generateEnchantments(tag, self));
        enchants.forEach(Apugli.LOGGER::info);
        return enchants;
    }

    public static PowerFactory<?> getFactory() {
        return new PowerFactory<ModifyEnchantmentLevelPower>(
                Apugli.identifier("modify_enchantment_level"),
                new SerializableData()
                        .add("enchantment", SerializableDataTypes.ENCHANTMENT)
                        .add("modifier", Modifier.DATA_TYPE, null)
                        .add("modifiers", Modifier.LIST_TYPE, null),
                data -> (type, player) -> {
                    ModifyEnchantmentLevelPower power = new ModifyEnchantmentLevelPower(type, player, (Enchantment) data.get("enchantment"));
                    if (data.isPresent("modifier")) {
                        power.addModifier((Modifier) data.get("modifier"));
                    }
                    if (data.isPresent("modifiers")) {
                        ((List<Modifier>)data.get("modifiers")).forEach(power::addModifier);
                    }
                    return power;
                })
                .allowCondition();
    }
}