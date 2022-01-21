package io.github.merchantpug.apugli.mixin;

import io.github.merchantpug.apugli.access.HiddenEffectStatus;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.annotation.Nullable;
import net.minecraft.entity.effect.StatusEffectInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(StatusEffectInstance.class)
public class StatusEffectInstanceMixin implements HiddenEffectStatus {
    @Shadow
    @Nullable
    private StatusEffectInstance hiddenEffect;

    public @Nullable StatusEffectInstance getHiddenEffect() {
        return this.hiddenEffect;
    }
}