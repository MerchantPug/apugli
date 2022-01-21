package io.github.merchantpug.apugli.powers;

import io.github.apace100.origins.power.Power;
import io.github.apace100.origins.power.PowerType;
import io.github.apace100.origins.power.factory.PowerFactory;
import io.github.apace100.origins.util.SerializableData;
import io.github.apace100.origins.util.SerializableDataType;
import io.github.merchantpug.apugli.Apugli;
import io.github.merchantpug.apugli.util.ApugliDataTypes;
import io.github.merchantpug.apugli.util.PlayerModelType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

public class SetTexturePower extends Power {
    public final Identifier textureLocation;
    public final PlayerModelType model;

    public static PowerFactory<?> getFactory() {
        return new PowerFactory<>(Apugli.identifier("set_texture"),
                new SerializableData()
                        .add("texture_location", SerializableDataType.IDENTIFIER, null)
                        .add("player_model", ApugliDataTypes.PLAYER_MODEL_TYPE, null),
                data ->
                        (type, player) ->
                                new SetTexturePower(type, player, data.getId("texture_location"), (PlayerModelType)data.get("player_model")))
                .allowCondition();
    }

    public SetTexturePower(PowerType<?> type, PlayerEntity entity, Identifier textureLocation, PlayerModelType model) {
        super(type, entity);
        this.textureLocation = textureLocation;
        this.model = model;
    }
}
