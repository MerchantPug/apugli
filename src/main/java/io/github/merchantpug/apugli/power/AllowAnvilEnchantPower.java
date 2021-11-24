package io.github.merchantpug.apugli.power;

import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.apoli.util.Comparison;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataType;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.merchantpug.apugli.Apugli;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class AllowAnvilEnchantPower extends Power {
    private List<Enchantment> enchantments = new ArrayList<>();
    private int compareTo;
    private Comparison comparison;
    private final Predicate<ItemStack> itemCondition;

    public static PowerFactory<?> getFactory() {
        return new PowerFactory<AllowAnvilEnchantPower>(
                Apugli.identifier("allow_anvil_enchant"),
                new SerializableData()
                        .add("enchantment", SerializableDataTypes.ENCHANTMENT, null)
                        .add("enchantments", SerializableDataType.list(SerializableDataTypes.ENCHANTMENT), null)
                        .add("compare_to", SerializableDataTypes.INT, 0)
                        .add("comparison", ApoliDataTypes.COMPARISON, Comparison.GREATER_THAN)
                        .add("item_condition", ApoliDataTypes.ITEM_CONDITION),
                data -> (type, player) -> {
                    AllowAnvilEnchantPower power = new AllowAnvilEnchantPower(type, player, data.getInt("compare_to"), data.get("comparison"), data.get("item_condition"));
                    if(data.isPresent("enchantment")) {
                        power.addEnchantment((Enchantment)data.get("enchantment"));
                    }
                    if(data.isPresent("effects")) {
                        ((List<Enchantment>)data.get("enchantments")).forEach(power::addEnchantment);
                    }
                    return power;
                }
        ).allowCondition();
    }

    public boolean doesApply(Enchantment enchantment, ItemStack itemStack, ItemStack itemStack2) {
        return enchantments.contains(enchantment) && itemCondition.test(itemStack) && (comparison.compare(EnchantmentHelper.getLevel(enchantment, itemStack2), compareTo) || itemStack2.getItem() instanceof EnchantedBookItem && EnchantedBookItem.getEnchantmentNbt(itemStack2).stream().anyMatch(nbt -> {
            if (!(nbt instanceof NbtCompound)) return false;
            return comparison.compare(EnchantmentHelper.getLevelFromNbt((NbtCompound)nbt), compareTo);
        }));
    }

    private void addEnchantment(Enchantment enchantment) {
        enchantments.add(enchantment);
    }

    public AllowAnvilEnchantPower(PowerType<?> type, LivingEntity entity, int compareTo, Comparison comparison, Predicate<ItemStack> itemCondition) {
        super(type, entity);
        this.compareTo = compareTo;
        this.comparison = comparison;
        this.itemCondition = itemCondition;
    }
}
