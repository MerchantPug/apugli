package net.merchantpug.apugli.condition.entity;

import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.merchantpug.apugli.Apugli;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffects;

public class CanTakeDamageCondition {
    public static boolean condition(SerializableData.Instance data, Entity entity) {
        DamageSource source = data.get("source");
        if (entity.isInvulnerableTo(source))
            return false;
        else if (entity instanceof LivingEntity living)
            return !source.isFire() || !living.hasStatusEffect(StatusEffects.FIRE_RESISTANCE);
        return true;
    }

    public static ConditionFactory<Entity> getFactory() {
        return new ConditionFactory<>(Apugli.identifier("can_take_damage"), new SerializableData()
                .add("source", SerializableDataTypes.DAMAGE_SOURCE),
                CanTakeDamageCondition::condition
        );
    }
}
