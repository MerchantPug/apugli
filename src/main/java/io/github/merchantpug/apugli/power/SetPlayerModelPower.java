package io.github.merchantpug.apugli.power;

import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import net.minecraft.entity.LivingEntity;

public class SetPlayerModelPower extends Power {
    public final String slim;

    public SetPlayerModelPower(PowerType<?> type, LivingEntity entity, String slim) {
        super(type, entity);
        this.slim = slim;
    }
}
