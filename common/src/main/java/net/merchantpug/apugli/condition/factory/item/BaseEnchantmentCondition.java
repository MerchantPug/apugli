package net.merchantpug.apugli.condition.factory.item;

import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.util.Comparison;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.merchantpug.apugli.condition.factory.IConditionFactory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

public class BaseEnchantmentCondition implements IConditionFactory<ItemStack> {
    
    @Override
    public SerializableData getSerializableData() {
        return new SerializableData()
                .add("enchantment", SerializableDataTypes.ENCHANTMENT)
                .add("compare_to", SerializableDataTypes.INT)
                .add("comparison", ApoliDataTypes.COMPARISON, Comparison.GREATER_THAN_OR_EQUAL);
    }
    
    @Override
    public boolean check(SerializableData.Instance data, ItemStack stack) {
        Enchantment enchantment = data.get("enchantment");
        int enchantLevel = EnchantmentHelper.deserializeEnchantments(stack.getEnchantmentTags()).getOrDefault(enchantment, 0);
        Comparison comparison = data.get("comparison");
        return comparison.compare(enchantLevel, data.getInt("compare_to"));
    }

}
