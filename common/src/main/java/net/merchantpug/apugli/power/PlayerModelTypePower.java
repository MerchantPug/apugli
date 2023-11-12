package net.merchantpug.apugli.power;

import net.merchantpug.apugli.power.factory.SimplePowerFactory;
import net.merchantpug.apugli.data.ApugliDataTypes;
import net.merchantpug.apugli.util.PlayerModelType;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.calio.data.SerializableData;
import net.minecraft.world.entity.LivingEntity;

public class PlayerModelTypePower extends Power {
    private final PlayerModelType model;

    public PlayerModelTypePower(PowerType<?> type, LivingEntity entity, PlayerModelType model) {
        super(type, entity);
        this.model = model;
    }

    public PlayerModelType getModel() {
        return model;
    }

    public static class Factory extends SimplePowerFactory<PlayerModelTypePower> {

        public Factory() {
            super("player_model_type",
                    new SerializableData()
                            .add("model", ApugliDataTypes.PLAYER_MODEL_TYPE),
                    data -> (type, player) -> new PlayerModelTypePower(type, player, data.get("model")));
            allowCondition();
        }

        @Override
        public Class<PlayerModelTypePower> getPowerClass() {
            return PlayerModelTypePower.class;
        }

    }

}
