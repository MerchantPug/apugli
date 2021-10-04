package io.github.merchantpug.apugli.power;

import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.merchantpug.apugli.Apugli;
import io.github.merchantpug.apugli.util.ApugliDataTypes;
import io.github.merchantpug.apugli.util.PlayerModelType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;

public class EnergySwirlPower extends Power {
    private final Identifier textureLocation;
    private final float speed;

    public static PowerFactory<?> getFactory() {
        return new PowerFactory<>(Apugli.identifier("set_texture"),
                new SerializableData()
                        .add("texture_location", SerializableDataTypes.IDENTIFIER, null)
                        .add("player_model", ApugliDataTypes.PLAYER_MODEL_TYPE, null),
                data ->
                        (type, player) ->
                                new SetTexturePower(type, player, data.getId("texture_location"), (PlayerModelType)data.get("player_model")))
                .allowCondition();
    }

    public EnergySwirlPower(PowerType<?> type, LivingEntity entity, Identifier textureLocation, float speed) {
        super(type, entity);
        this.textureLocation = textureLocation;
        this.speed = speed;
    }

    public Identifier getTextureLocation() {
        return textureLocation;
    }

    public float getSpeed() {
        return speed;
    }
}
