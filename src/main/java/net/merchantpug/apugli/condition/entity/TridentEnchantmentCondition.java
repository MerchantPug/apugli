package net.merchantpug.apugli.condition.entity;

import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.apoli.util.Comparison;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.merchantpug.apugli.Apugli;
import net.merchantpug.apugli.mixin.TridentEntityAccessor;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.TridentEntity;

public class TridentEnchantmentCondition {
    public static boolean condition(SerializableData.Instance data, Entity entity) {
        int value = 0;
        if (entity instanceof TridentEntity trident) {
            value = EnchantmentHelper.getLevel(data.get("enchantment"), ((TridentEntityAccessor) trident).getTridentStack());
        }
        return ((Comparison)data.get("comparison")).compare(value, data.getInt("compare_to"));
    }

    public static ConditionFactory<Entity> getFactory() {
        return new ConditionFactory<>(Apugli.identifier("trident_enchantment"), new SerializableData()
                .add("enchantment", SerializableDataTypes.ENCHANTMENT)
                .add("comparison", ApoliDataTypes.COMPARISON, Comparison.GREATER_THAN_OR_EQUAL)
                .add("compare_to", SerializableDataTypes.INT),
                TridentEnchantmentCondition::condition
        );
    }
}
