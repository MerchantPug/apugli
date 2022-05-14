package io.github.merchantpug.apugli.condition.entity;

import io.github.apace100.origins.power.factory.condition.ConditionFactory;
import io.github.apace100.origins.util.SerializableData;
import io.github.apace100.origins.util.SerializableDataType;
import io.github.merchantpug.apugli.Apugli;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;

public class CanHaveEffectCondition {
    public static boolean condition(SerializableData.Instance data, LivingEntity entity) {
        if (entity instanceof LivingEntity) {
            StatusEffect effect = (StatusEffect)data.get("effect");
            StatusEffectInstance instance = new StatusEffectInstance(effect);
            return ((LivingEntity)entity).canHaveStatusEffect(instance);
        }
        return false;
    }

    public static ConditionFactory<LivingEntity> getFactory() {
        return new ConditionFactory<>(Apugli.identifier("can_have_effect"), new SerializableData()
                .add("effect", SerializableDataType.STATUS_EFFECT),
                CanHaveEffectCondition::condition
        );
    }
}
