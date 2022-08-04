package com.github.merchantpug.apugli.power;

import com.github.merchantpug.apugli.Apugli;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;

import javax.annotation.Nullable;

public class EnergySwirlPower extends Power {
    private final Identifier textureLocation;
    private final String textureUrl;
    private final float size;
    private final float speed;

    public static PowerFactory<?> getFactory() {
        return new PowerFactory<EnergySwirlPower>(Apugli.identifier("energy_swirl"),
                new SerializableData()
                        .add("texture_location", SerializableDataTypes.IDENTIFIER, null)
                        .add("texture_url", SerializableDataTypes.STRING, null)
                        .add("size", SerializableDataTypes.FLOAT, 1.0F)
                        .add("speed", SerializableDataTypes.FLOAT, 0.01F),
                data ->
                        (type, entity) ->
                                new EnergySwirlPower(type, entity, data.getId("texture_location"), data.getString("texture_url"), data.getFloat("size"), data.getFloat("speed")))
                .allowCondition();
    }

    public EnergySwirlPower(PowerType<?> type, LivingEntity entity, Identifier textureLocation, String textureUrl, float size, float speed) {
        super(type, entity);
        if (textureLocation == null && textureUrl == null) {
            Apugli.LOGGER.warn("EnergySwirlPower '" + this.getType().getIdentifier() + "' does not have a valid `texture_location` or `texture_url` field. This power will not render.");
        }
        this.textureLocation = textureLocation;
        this.textureUrl = textureUrl;
        this.size = size;
        this.speed = speed;
    }

    @Nullable public Identifier getTextureLocation() {
        return textureLocation;
    }

    @Nullable public String getTextureUrl() {
        return textureUrl;
    }

    public Identifier getUrlTextureIdentifier() {
        return new Identifier(Apugli.MODID, "energyswirlpower/" + this.getType().getIdentifier().getNamespace() + "/" + this.getType().getIdentifier().getPath());
    }

    public float getSize() {
        return size;
    }

    public float getSpeed() {
        return speed;
    }
}
