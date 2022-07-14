package com.github.merchantpug.apugli.powers;

import io.github.apace100.origins.power.Power;
import io.github.apace100.origins.power.PowerType;
import io.github.apace100.origins.power.factory.PowerFactory;
import io.github.apace100.origins.util.Comparison;
import io.github.apace100.origins.util.SerializableData;
import io.github.apace100.origins.util.SerializableDataType;
import com.github.merchantpug.apugli.Apugli;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;

import java.util.ArrayList;
import java.util.List;
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
                        .add("enchantment", SerializableDataType.ENCHANTMENT, null)
                        .add("enchantments", SerializableDataType.list(SerializableDataType.ENCHANTMENT), null)
                        .add("compare_to", SerializableDataType.INT, 0)
                        .add("comparison", SerializableDataType.COMPARISON, Comparison.GREATER_THAN)
                        .add("item_condition", SerializableDataType.ITEM_CONDITION),
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
        return enchantments.contains(enchantment) && itemCondition.test(itemStack) && (comparison.compare(EnchantmentHelper.getLevel(enchantment, itemStack2), compareTo) || itemStack2.getItem() instanceof EnchantedBookItem && EnchantedBookItem.getEnchantmentTag(itemStack2).stream().anyMatch(nbt -> {
            if (!(nbt instanceof CompoundTag)) return false;
            return comparison.compare(((CompoundTag) nbt).getInt("lvl"), compareTo);
        }));
    }

    private void addEnchantment(Enchantment enchantment) {
        enchantments.add(enchantment);
    }

    public AllowAnvilEnchantPower(PowerType<?> type, PlayerEntity player, int compareTo, Comparison comparison, Predicate<ItemStack> itemCondition) {
        super(type, player);
        this.compareTo = compareTo;
        this.comparison = comparison;
        this.itemCondition = itemCondition;
    }
}
