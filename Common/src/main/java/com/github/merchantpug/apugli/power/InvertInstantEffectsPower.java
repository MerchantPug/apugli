package net.merchantpug.apugli.power;

import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import net.minecraft.world.entity.LivingEntity;

public class InvertInstantEffectsPower extends Power {
    public InvertInstantEffectsPower(PowerType<?> type, LivingEntity entity) {
        super(type, entity);
    }
}
