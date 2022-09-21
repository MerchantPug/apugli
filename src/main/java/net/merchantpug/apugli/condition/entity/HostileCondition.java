package net.merchantpug.apugli.condition.entity;

import net.merchantpug.apugli.Apugli;
import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.calio.data.SerializableData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.HostileEntity;

public class HostileCondition {
    public static boolean condition(SerializableData.Instance data, Entity entity) {
        return entity instanceof HostileEntity;
    }

    public static ConditionFactory<Entity> getFactory() {
        return new ConditionFactory<>(Apugli.identifier("hostile"), new SerializableData(),
                HostileCondition::condition
        );
    }
}
