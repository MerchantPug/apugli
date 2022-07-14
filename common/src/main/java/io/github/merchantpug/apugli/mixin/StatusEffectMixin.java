package io.github.merchantpug.apugli.mixin;

import io.github.apace100.origins.component.OriginComponent;
import io.github.merchantpug.apugli.powers.EffectWhitelistPower;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(StatusEffect.class)
public class StatusEffectMixin {
    @Inject(method = "applyInstantEffect", at = @At("HEAD"), cancellable = true)
    private void cancelInstantEffectWhenBlacklisted(Entity source, Entity attacker, LivingEntity target, int amplifier, double proximity, CallbackInfo ci) {
        for (EffectWhitelistPower power : OriginComponent.getPowers(target, EffectWhitelistPower.class)) {
            if(!power.doesApply((StatusEffect)(Object)this)) {
                ci.cancel();
            }
        }
    }
}