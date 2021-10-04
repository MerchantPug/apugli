package io.github.merchantpug.apugli.power;

import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.merchantpug.apugli.Apugli;
import io.github.merchantpug.apugli.util.PlayerModelType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;

public class SetTexturePower extends Power {
    public final Identifier textureLocation;
    public final PlayerModelType model;

    public static PowerFactory<?> getFactory() {
        return new PowerFactory<EnergySwirlPower>(Apugli.identifier("energy_swirl"),
                new SerializableData()
                        .add("texture_location", SerializableDataTypes.IDENTIFIER)
                        .add("speed", SerializableDataTypes.FLOAT, 0.01F),
                data ->
                        (type, entity) ->
                                new EnergySwirlPower(type, entity, data.getId("texture_location"), data.getFloat("speed")))
                .allowCondition();
    }

    public SetTexturePower(PowerType<?> type, LivingEntity entity, Identifier textureLocation, PlayerModelType model) {
        super(type, entity);
        this.textureLocation = textureLocation;
        this.model = model;
    }
}
