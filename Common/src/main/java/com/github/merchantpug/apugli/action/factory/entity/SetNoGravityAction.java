package com.github.merchantpug.apugli.action.factory.entity;

import com.github.merchantpug.apugli.action.factory.IActionFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.world.entity.Entity;

public class SetNoGravityAction implements IActionFactory<Entity> {
    
    @Override
    public SerializableData getSerializableData() {
        return new SerializableData()
            .add("value", SerializableDataTypes.BOOLEAN, null);
    }
    
    @Override
    public void execute(SerializableData.Instance data, Entity entity) {
        if(!data.isPresent("value")) {
            entity.setNoGravity(!entity.isNoGravity());
        } else {
            entity.setNoGravity(data.getBoolean("value"));
        }
    }

}
