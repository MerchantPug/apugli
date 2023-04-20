package net.merchantpug.apugli.condition.factory.entity;

import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.merchantpug.apugli.condition.factory.IConditionFactory;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class CanTakeDamageCondition implements IConditionFactory<Entity> {

    @Override
    public SerializableData getSerializableData() {
        return new SerializableData()
                .add("source", SerializableDataTypes.DAMAGE_SOURCE);
    }

    @Override
    public boolean check(SerializableData.Instance data, Entity instance) {
        DamageSource source = data.get("source");
        if (instance.isInvulnerableTo(source))
            return false;
        else if (instance instanceof LivingEntity living)
            return !source.isFire() || !living.hasEffect(MobEffects.FIRE_RESISTANCE);
        return true;
    }

}
