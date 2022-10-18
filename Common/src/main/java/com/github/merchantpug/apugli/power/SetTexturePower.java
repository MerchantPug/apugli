package com.github.merchantpug.apugli.power;

import the.great.migration.merchantpug.apugli.Apugli;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import com.github.merchantpug.apugli.util.ApugliDataTypes;
import com.github.merchantpug.apugli.util.PlayerModelType;

public class SetTexturePower extends Power {
    public final ResourceLocation textureLocation;
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

    public SetTexturePower(PowerType<?> type, LivingEntity entity, ResourceLocation textureLocation, PlayerModelType model) {
        super(type, entity);
        this.textureLocation = textureLocation;
        this.model = model;
    }
}
