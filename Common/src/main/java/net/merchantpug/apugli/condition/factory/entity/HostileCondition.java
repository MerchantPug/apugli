package net.merchantpug.apugli.condition.factory.entity;

import net.merchantpug.apugli.condition.factory.IConditionFactory;
import io.github.apace100.calio.data.SerializableData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Monster;

public class HostileCondition implements IConditionFactory<Entity> {
    
    @Override
    public boolean check(SerializableData.Instance data, Entity entity) {
        return entity instanceof Monster;
    }
    
}
