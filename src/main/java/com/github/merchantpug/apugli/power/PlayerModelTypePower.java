package com.github.merchantpug.apugli.power;

import com.github.merchantpug.apugli.Apugli;
import com.github.merchantpug.apugli.util.ApugliDataTypes;
import com.github.merchantpug.apugli.util.PlayerModelType;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableData;
import net.minecraft.entity.LivingEntity;

public class PlayerModelTypePower extends Power {
    public final PlayerModelType model;

    public static PowerFactory<?> getFactory() {
        return new PowerFactory<>(Apugli.identifier("player_model_type"),
                new SerializableData()
                        .add("model", ApugliDataTypes.PLAYER_MODEL_TYPE),
                data ->
                        (type, player) ->
                                new PlayerModelTypePower(type, player, data.get("model")))
                .allowCondition();
    }

    public PlayerModelTypePower(PowerType<?> type, LivingEntity entity, PlayerModelType model) {
        super(type, entity);
        this.model = model;
    }
}
