package net.merchantpug.apugli.condition.factory.entity;

import net.merchantpug.apugli.condition.factory.IConditionFactory;
import io.github.apace100.calio.data.SerializableData;
import net.merchantpug.apugli.data.ApugliDataTypes;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.Entity;

public class PlayerModelTypeCondition implements IConditionFactory<Entity> {
    
    @Override
    public SerializableData getSerializableData() {
        return new SerializableData()
            .add("model_type", ApugliDataTypes.PLAYER_MODEL_TYPE);
    }
    
    @Override
    public boolean check(SerializableData.Instance data, Entity entity) {
        if(!entity.level().isClientSide() || !(entity instanceof AbstractClientPlayer)) return false;
        return ((AbstractClientPlayer) entity).getModelName().equals(data.get("model_type").toString());
    }

}
