package net.merchantpug.apugli.power;

import net.merchantpug.apugli.Apugli;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.merchantpug.apugli.util.ApugliDataTypes;
import net.merchantpug.apugli.util.PlayerModelType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;

public class SetTexturePower extends Power {
    public final Identifier textureLocation;
    public final PlayerModelType model;

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

    public SetTexturePower(PowerType<?> type, LivingEntity entity, Identifier textureLocation, PlayerModelType model) {
        super(type, entity);
        this.textureLocation = textureLocation;
        this.model = model;
    }
}
