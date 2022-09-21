package net.merchantpug.apugli.condition.entity;

import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.merchantpug.apugli.Apugli;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;

public class CanHaveEffectCondition {
    public static boolean condition(SerializableData.Instance data, Entity entity) {
        if (entity instanceof LivingEntity) {
            StatusEffect effect = (StatusEffect)data.get("effect");
            StatusEffectInstance instance = new StatusEffectInstance(effect);
            return ((LivingEntity)entity).canHaveStatusEffect(instance);
        }
        return false;
    }

    public static ConditionFactory<Entity> getFactory() {
        return new ConditionFactory<>(Apugli.identifier("can_have_effect"), new SerializableData()
                .add("effect", SerializableDataTypes.STATUS_EFFECT),
                CanHaveEffectCondition::condition
        );
    }
}
