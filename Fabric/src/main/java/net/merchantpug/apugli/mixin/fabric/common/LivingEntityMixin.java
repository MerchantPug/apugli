package net.merchantpug.apugli.mixin.fabric.common;

import net.merchantpug.apugli.component.ApugliEntityComponents;
import net.merchantpug.apugli.component.HitsOnTargetComponent;
import net.merchantpug.apugli.registry.power.ApugliPowers;
import net.minecraft.util.Tuple;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Inject(method = "hurt", at = @At("RETURN"))
    private void runDamageFunctions(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue() || source.getEntity() == null) return;

        LivingEntity thisAsLiving = (LivingEntity)(Object)this;

        if ((Object) this instanceof TamableAnimal tamable) {
            ApugliPowers.ACTION_WHEN_TAME_HIT.get().whenTameHit(tamable, source.getEntity(), source, amount);
        }
        if (source.getEntity() instanceof TamableAnimal tamable) {
            ApugliPowers.ACTION_WHEN_TAME_HIT.get().whenTameHit(tamable, thisAsLiving, source, amount);
        }
        HitsOnTargetComponent hitsComponent = ApugliEntityComponents.HITS_ON_TARGET_COMPONENT.get(thisAsLiving);
        hitsComponent.setHits(source.getEntity(), hitsComponent.getHits().getOrDefault(source.getEntity().getId(), new Tuple<>(0, 0)).getA() + 1, 0);
        ApugliEntityComponents.HITS_ON_TARGET_COMPONENT.sync(thisAsLiving);
    }

    @Unique
    private boolean apugli$hasModifiedDamage;

    @ModifyVariable(method = "hurt", at = @At("HEAD"), argsOnly = true)
    private float modifyDamageBasedOnEnchantment(float originalValue, DamageSource source, float amount) {
        float additionalValue = 0.0F;
        LivingEntity thisAsLiving = (LivingEntity) (Object) this;

        if (source.getEntity() != null && source.getEntity() instanceof LivingEntity attacker && !source.isProjectile()) {
            additionalValue = ApugliPowers.MODIFY_ENCHANTMENT_DAMAGE_DEALT.get().getModifiedDamage(attacker, source, amount, thisAsLiving);
        }

        if (source.getEntity() != null && source.getEntity() instanceof LivingEntity) {
            additionalValue = ApugliPowers.MODIFY_ENCHANTMENT_DAMAGE_TAKEN.get().getModifiedDamage(thisAsLiving, source, amount);
        }

        apugli$hasModifiedDamage = originalValue + additionalValue != originalValue;

        return originalValue + additionalValue;
    }

    @Inject(method = "hurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;isSleeping()Z"), cancellable = true)
    private void preventHitIfDamageIsZero(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (apugli$hasModifiedDamage && amount == 0.0F) {
            cir.setReturnValue(false);
        }
    }

}
