package net.merchantpug.apugli.power;

import net.merchantpug.apugli.Apugli;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.merchantpug.apugli.power.factory.SimplePowerFactory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

public class EnergySwirlPower extends TextureOrUrlPower {
    private final float size;
    private final float speed;

    public static PowerFactory<?> getFactory() {
        return new PowerFactory<EnergySwirlPower>(Apugli.asResource("energy_swirl"),
                new SerializableData(),
                data ->
                        (type, entity) ->
                                new EnergySwirlPower(type, entity, data.getId("texture_location"), data.getString("texture_url"), data.getFloat("size"), data.getFloat("speed")))
                .allowCondition();
    }

    public EnergySwirlPower(PowerType<?> type, LivingEntity entity, ResourceLocation textureLocation, String textureUrl, float size, float speed) {
        super(type, entity, textureLocation, textureUrl);
        this.size = size;
        this.speed = speed;
    }

    public float getSize() {
        return size;
    }

    public float getSpeed() {
        return speed;
    }

    @Override
    public String getPowerClassString() {
        return "EnergySwirlPower";
    }

    public static class Factory extends SimplePowerFactory<EnergySwirlPower> {

        public Factory() {
            super("energy_swirl",
                    TextureOrUrlPower.getSerializableData()
                            .add("size", SerializableDataTypes.FLOAT, 1.0F)
                            .add("speed", SerializableDataTypes.FLOAT, 0.01F),
                    data -> (type, player) -> new EnergySwirlPower(type, player,
                            data.getId("texture_location"),
                            data.getString("texture_url"),
                            data.getFloat("size"),
                            data.getFloat("speed")));
            allowCondition();
        }

        @Override
        public Class<EnergySwirlPower> getPowerClass() {
            return EnergySwirlPower.class;
        }

    }
}
