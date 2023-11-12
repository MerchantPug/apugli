package net.merchantpug.apugli.condition.factory.entity;

import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.util.Comparison;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataType;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.merchantpug.apugli.condition.factory.IConditionFactory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

public class BaseEnchantmentCondition implements IConditionFactory<Entity> {
    
    @Override
    public SerializableData getSerializableData() {
        return new SerializableData()
                .add("enchantment", SerializableDataTypes.ENCHANTMENT)
                .add("compare_to", SerializableDataTypes.INT)
                .add("comparison", ApoliDataTypes.COMPARISON, Comparison.GREATER_THAN_OR_EQUAL)
                .add("calculation", SerializableDataType.enumValue(CalculationType.class), CalculationType.SUM);
    }
    
    @Override
    public boolean check(SerializableData.Instance data, Entity entity) {
        int value = 0;
        if (entity instanceof LivingEntity le) {
            Enchantment enchantment = data.get("enchantment");
            CalculationType calculation = data.get("calculation");
            switch (calculation) {
                case SUM -> {
                    for (ItemStack stack : enchantment.getSlotItems(le).values()) {
                        value += EnchantmentHelper.deserializeEnchantments(stack.getEnchantmentTags()).getOrDefault(enchantment, 0);
                    }
                }
                case TOTAL -> {
                    for (ItemStack stack : enchantment.getSlotItems(le).values()) {
                        int potentialValue = EnchantmentHelper.deserializeEnchantments(stack.getEnchantmentTags()).getOrDefault(enchantment, 0);
                        if (potentialValue > value) {
                            value = potentialValue;
                        }
                    }
                }
            }
        }
        Comparison comparison = data.get("comparison");
        return comparison.compare(value, data.getInt("compare_to"));
    }

    public enum CalculationType {
        SUM,
        TOTAL
    }

}
