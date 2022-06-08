package com.github.merchantpug.apugli.power;

import com.github.merchantpug.apugli.Apugli;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;

public class EnergySwirlPower extends Power {
    private final Identifier textureLocation;
    private final float speed;

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
