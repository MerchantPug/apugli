package com.github.merchantpug.apugli.mixin;

import io.github.apace100.origins.power.CooldownPower;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(CooldownPower.class)
public interface CooldownPowerAccessor {
    @Accessor(remap = false)
    long getLastUseTime();
}