package net.merchantpug.apugli.condition.factory.entity;

import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.util.Comparison;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.merchantpug.apugli.condition.factory.IConditionFactory;
import net.merchantpug.apugli.mixin.xplatform.common.accessor.TridentEntityAccessor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

public class TridentEnchantmentCondition implements IConditionFactory<Entity> {
    @Override
    public SerializableData getSerializableData() {
        return new SerializableData()
                .add("enchantment", SerializableDataTypes.ENCHANTMENT)
                .add("comparison", ApoliDataTypes.COMPARISON, Comparison.GREATER_THAN_OR_EQUAL)
                .add("compare_to", SerializableDataTypes.INT);
    }

    @Override
    public boolean check(SerializableData.Instance data, Entity instance) {
        int value = 0;
        if (instance instanceof ThrownTrident trident) {
            value = EnchantmentHelper.getItemEnchantmentLevel(data.get("enchantment"), ((TridentEntityAccessor) trident).getTridentStack());
        }
        return ((Comparison)data.get("comparison")).compare(value, data.getInt("compare_to"));
    }
}
