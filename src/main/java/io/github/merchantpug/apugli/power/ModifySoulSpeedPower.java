package io.github.merchantpug.apugli.power;

import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.ValueModifyingPower;
import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.entity.LivingEntity;

public class ModifySoulSpeedPower extends ValueModifyingPower {
    public final ConditionFactory<CachedBlockPosition>.Instance blockCondition;

    public ModifySoulSpeedPower(PowerType<?> type, LivingEntity entity, ConditionFactory<CachedBlockPosition>.Instance blockCondition) {
        super(type, entity);
        this.blockCondition = blockCondition;
    }
}
