package io.github.merchantpug.apugli.power;

import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.ValueModifyingPower;
import net.minecraft.entity.LivingEntity;

public class ModifySoulSpeedPower extends ValueModifyingPower {
    public ModifySoulSpeedPower(PowerType<?> type, LivingEntity entity) {
        super(type, entity);
    }
}
