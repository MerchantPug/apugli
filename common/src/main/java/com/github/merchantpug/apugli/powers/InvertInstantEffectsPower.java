package com.github.merchantpug.apugli.powers;

import io.github.apace100.origins.power.Power;
import io.github.apace100.origins.power.PowerType;
import net.minecraft.entity.player.PlayerEntity;

public class InvertInstantEffectsPower extends Power {
    public InvertInstantEffectsPower(PowerType<?> type, PlayerEntity player) {
        super(type, player);
    }
}
