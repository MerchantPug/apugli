package io.github.merchantpug.apugli.power;

import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import net.minecraft.entity.LivingEntity;

public class UnenchantedSoulSpeedPower extends Power {
    private final int modifier;

    public UnenchantedSoulSpeedPower(PowerType<?> type, LivingEntity entity, int modifier) {
        super(type, entity);
        this.modifier = modifier;
    }

    public int getModifier() {
        return modifier;
    }
}
