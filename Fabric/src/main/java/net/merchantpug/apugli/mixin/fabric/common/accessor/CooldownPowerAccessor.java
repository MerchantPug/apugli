package net.merchantpug.apugli.mixin.fabric.common.accessor;

import io.github.apace100.apoli.power.CooldownPower;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(CooldownPower.class)
public interface CooldownPowerAccessor {

    @Accessor(value = "lastUseTime", remap = false)
    long getLastUseTime();

}
