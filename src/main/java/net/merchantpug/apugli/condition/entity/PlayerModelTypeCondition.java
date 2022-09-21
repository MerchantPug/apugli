package net.merchantpug.apugli.condition.entity;

import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.calio.data.SerializableData;
import net.merchantpug.apugli.Apugli;
import net.merchantpug.apugli.util.ApugliDataTypes;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.Entity;

public class PlayerModelTypeCondition {
    public static boolean condition(SerializableData.Instance data, Entity entity) {
        if (!entity.getEntityWorld().isClient() || !(entity instanceof AbstractClientPlayerEntity)) return false;
        return ((AbstractClientPlayerEntity) entity).getModel().equals(data.get("model_type").toString());
    }

    public static ConditionFactory<Entity> getFactory() {
        return new ConditionFactory<>(Apugli.identifier("player_model_type"), new SerializableData()
                .add("model_type", ApugliDataTypes.PLAYER_MODEL_TYPE),
                PlayerModelTypeCondition::condition
        );
    }
}
