package net.merchantpug.apugli.condition.factory.entity;

import io.github.apace100.calio.data.SerializableData;
import net.merchantpug.apugli.condition.factory.IConditionFactory;
import net.merchantpug.apugli.platform.Services;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;

import java.util.function.Predicate;

public abstract class AttackConditionsCondition implements IConditionFactory<Entity> {

    @Override
    public SerializableData getSerializableData() {
        return new SerializableData()
                .add("bientity_condition", Services.CONDITION.biEntityDataType());
    }

    public boolean check(SerializableData.Instance data, Entity attacker, Entity target) {
        Predicate<Tuple<Entity, Entity>> pair = Services.CONDITION.biEntityPredicate(data, "bientity_condition");
        if (pair != null) {
            return pair.test(new Tuple<>(attacker, target));
        }
        return false;
    }

}
