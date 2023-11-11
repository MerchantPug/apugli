package net.merchantpug.apugli.mixin.xplatform.common;

import net.merchantpug.apugli.platform.Services;
import net.merchantpug.apugli.power.InstantEffectImmunityPower;
import io.github.apace100.apoli.component.PowerHolderComponent;
import net.merchantpug.apugli.registry.power.ApugliPowers;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MobEffect.class)
public class StatusEffectMixin {
    @Inject(method = "applyInstantenousEffect", at = @At("HEAD"), cancellable = true)
    private void apugli$cancelInstantEffectWhenBlacklisted(Entity source, Entity attacker, LivingEntity target, int amplifier, double proximity, CallbackInfo ci) {
        if (Services.POWER.getPowers(target, ApugliPowers.INSTANT_EFFECT_IMMUNITY.get()).stream().anyMatch(p -> p.doesApply((MobEffect)(Object)this))) {
            ci.cancel();
        }
    }
}
