package net.merchantpug.apugli.condition.factory.entity;

import io.github.apace100.calio.data.SerializableData;
import net.merchantpug.apugli.condition.factory.IConditionFactory;
import net.minecraft.world.entity.Entity;

public class RainingCondition implements IConditionFactory<Entity> {

    @Override
    public boolean check(SerializableData.Instance data, Entity entity) {
        return entity.level().isRaining();
    }

}