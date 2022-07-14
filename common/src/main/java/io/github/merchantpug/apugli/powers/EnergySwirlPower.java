package io.github.merchantpug.apugli.powers;

import io.github.apace100.origins.power.Power;
import io.github.apace100.origins.power.PowerType;
import io.github.apace100.origins.power.factory.PowerFactory;
import io.github.apace100.origins.util.SerializableData;
import io.github.apace100.origins.util.SerializableDataType;
import io.github.merchantpug.apugli.Apugli;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

public class EnergySwirlPower extends Power {
    private final Identifier textureLocation;
    private final float speed;

    public static PowerFactory<?> getFactory() {
        return new PowerFactory<EnergySwirlPower>(Apugli.identifier("energy_swirl"),
                new SerializableData()
                        .add("texture_location", SerializableDataType.IDENTIFIER)
                        .add("speed", SerializableDataType.FLOAT, 0.01F),
                data ->
                        (type, player) ->
                                new EnergySwirlPower(type, player, data.getId("texture_location"), data.getFloat("speed")))
                .allowCondition();
    }

    public EnergySwirlPower(PowerType<?> type, PlayerEntity player, Identifier textureLocation, float speed) {
        super(type, player);
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
