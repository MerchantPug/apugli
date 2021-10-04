package io.github.merchantpug.apugli.condition.entity;

import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.merchantpug.apugli.Apugli;
import io.github.merchantpug.apugli.util.ApugliDataTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;

public class ApugliEntityGroupCondition {
    public static boolean condition(SerializableData.Instance data, Entity entity) {
        if (entity instanceof LivingEntity) {
            return ((LivingEntity)entity).getGroup() == data.get("group");
        }
        return false;
    }

    public static ConditionFactory<Entity> getFactory() {
        return new ConditionFactory<>(Apugli.identifier("entity_group"), new SerializableData()
                .add("group", ApugliDataTypes.APUGLI_ENTITY_GROUP),
                ApugliEntityGroupCondition::condition
        );
    }
}
