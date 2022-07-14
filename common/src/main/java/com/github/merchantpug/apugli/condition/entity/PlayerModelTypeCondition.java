package com.github.merchantpug.apugli.condition.entity;

import com.github.merchantpug.apugli.Apugli;
import com.github.merchantpug.apugli.util.ApugliDataTypes;
import io.github.apace100.origins.power.factory.condition.ConditionFactory;
import io.github.apace100.origins.util.SerializableData;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.LivingEntity;

public class PlayerModelTypeCondition {
    public static boolean condition(SerializableData.Instance data, LivingEntity entity) {
        if (!entity.getEntityWorld().isClient() || !(entity instanceof AbstractClientPlayerEntity)) return false;
        return ((AbstractClientPlayerEntity) entity).getModel().equals(data.get("model_type").toString());
    }

    public static ConditionFactory<LivingEntity> getFactory() {
        return new ConditionFactory<>(Apugli.identifier("player_model_type"), new SerializableData()
                .add("model_type", ApugliDataTypes.PLAYER_MODEL_TYPE),
                PlayerModelTypeCondition::condition
        );
    }
}