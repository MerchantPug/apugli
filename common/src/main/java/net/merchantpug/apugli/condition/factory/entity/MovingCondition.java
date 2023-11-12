package net.merchantpug.apugli.condition.factory.entity;

import io.github.apace100.calio.data.SerializableData;
import net.merchantpug.apugli.access.EntityAccess;
import net.merchantpug.apugli.condition.factory.IConditionFactory;
import net.minecraft.world.entity.Entity;

public class MovingCondition implements IConditionFactory<Entity> {

    public boolean check(SerializableData.Instance data, Entity entity) {
        return ((EntityAccess)entity).apugli$isMoving();
    }

}
