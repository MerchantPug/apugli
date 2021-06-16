package io.github.merchantpug.apugli.power;

import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;

public class EnergySwirlOverlayPower extends Power {
    private final Identifier textureLocation;
    private final float speed;

    public EnergySwirlOverlayPower(PowerType<?> type, LivingEntity entity, Identifier textureLocation, float speed) {
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
