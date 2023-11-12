package net.merchantpug.apugli.condition.factory.entity;

import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.merchantpug.apugli.condition.factory.IConditionFactory;
import net.merchantpug.apugli.platform.Services;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class CanTakeDamageCondition implements IConditionFactory<Entity> {

    @Override
    public SerializableData getSerializableData() {
        return new SerializableData()
                .add("damage_type", SerializableDataTypes.DAMAGE_TYPE, null)
                .add("source", ApoliDataTypes.DAMAGE_SOURCE_DESCRIPTION, null);
    }

    @Override
    public boolean check(SerializableData.Instance data, Entity instance) {
        DamageSource source = Services.PLATFORM.createDamageSource(instance.damageSources(), data, "damage_type", "source");
        if (instance.isInvulnerableTo(source))
            return false;
        else if (instance instanceof LivingEntity living)
            return !source.is(DamageTypeTags.IS_FIRE) || !living.hasEffect(MobEffects.FIRE_RESISTANCE);
        return true;
    }

}
