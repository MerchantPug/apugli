package io.github.merchantpug.apugli.mixin;

import io.github.apace100.apoli.power.CooldownPower;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(CooldownPower.class)
public interface CooldownPowerAccessor {
    @Accessor
    long getLastUseTime();
}
