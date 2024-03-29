package net.merchantpug.apugli.condition.factory.entity;

import io.github.apace100.calio.data.SerializableData;
import net.merchantpug.apugli.condition.factory.IConditionFactory;
import net.minecraft.world.entity.Entity;

public class GroundedCondition implements IConditionFactory<Entity> {

    @Override
    public boolean check(SerializableData.Instance data, Entity entity) {
        return entity.onGround();
    }

}
