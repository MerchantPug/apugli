package net.merchantpug.apugli.condition.factory.entity;

import net.merchantpug.apugli.condition.factory.IConditionFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class CanHaveEffectCondition implements IConditionFactory<Entity> {
    
    @Override
    public SerializableData getSerializableData() {
        return new SerializableData()
            .add("effect", SerializableDataTypes.STATUS_EFFECT);
    }
    
    @Override
    public boolean check(SerializableData.Instance data, Entity entity) {
        if(entity instanceof LivingEntity living) {
            MobEffectInstance instance = new MobEffectInstance((MobEffect) data.get("effect"));
            return living.canBeAffected(instance);
        }
        return false;
    }

}
