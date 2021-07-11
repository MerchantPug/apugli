package io.github.merchantpug.apugli.power;

import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import net.minecraft.entity.LivingEntity;

public class SetPlayerModelPower extends Power {
    public final boolean slim;

    public SetPlayerModelPower(PowerType<?> type, LivingEntity entity, boolean slim) {
        super(type, entity);
        this.slim = slim;
    }
}
