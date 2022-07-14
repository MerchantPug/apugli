package com.github.merchantpug.apugli.mixin;

import com.github.merchantpug.apugli.access.HiddenEffectStatus;
import com.github.merchantpug.apugli.access.ItemStackAccess;
import com.github.merchantpug.apugli.powers.*;
import io.github.apace100.origins.component.OriginComponent;
import com.github.merchantpug.apugli.powers.*;
import com.github.merchantpug.apugli.util.ItemStackFoodComponentUtil;
import com.github.merchantpug.apugli.util.ModComponents;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;
import java.util.stream.Collectors;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    @Shadow public abstract boolean isFallFlying();

    @Shadow public abstract boolean isDead();

    @Shadow protected abstract boolean isOnSoulSpeedBlock();

    @Shadow public abstract SoundEvent getEatSound(ItemStack itemStack);

    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "eatFood", at = @At("HEAD"), cancellable = true)
    private void eatFood(World world, ItemStack stack, CallbackInfoReturnable<ItemStack> cir) {
        if (((ItemStackAccess)(Object)stack).isItemStackFood()) {
            world.playSound(null, this.getX(), this.getY(), this.getZ(), this.getEatSound(stack), SoundCategory.NEUTRAL, 1.0f, 1.0f + (world.random.nextFloat() - world.random.nextFloat()) * 0.4f);
            ItemStackFoodComponentUtil.applyFoodEffects(stack, world, (LivingEntity)(Object)this);
            if (!((LivingEntity)(Object)this instanceof PlayerEntity) || !((PlayerEntity)(Object)this).abilities.creativeMode) {
                stack.decrement(1);
            }
            cir.setReturnValue(stack);
        }
    }

    @ModifyVariable(method = "addStatusEffect", at = @At("HEAD"), argsOnly = true)
    private StatusEffectInstance modifyStatusEffect(StatusEffectInstance effect) {
        StatusEffect effectType = effect.getEffectType();
        int originalAmp = effect.getAmplifier();
        int originalDur = effect.getDuration();

        int amplifier = Math.round(OriginComponent.modify(this, ModifyStatusEffectAmplifierPower.class, originalAmp, power -> power.doesApply(effectType)));
        int duration = Math.round(OriginComponent.modify(this, ModifyStatusEffectDurationPower.class, originalDur, power -> power.doesApply(effectType)));

        if (amplifier != originalAmp || duration != originalDur) {
            return new StatusEffectInstance(
                    effectType,
                    duration,
                    amplifier,
                    effect.isAmbient(),
                    effect.shouldShowParticles(),
                    effect.shouldShowIcon(),
                    ((HiddenEffectStatus) effect).getHiddenEffect()
            );
        }
        return effect;
    }

    @Unique private boolean hasModifiedDamage;

    @ModifyVariable(method = "damage", at = @At("HEAD"), argsOnly = true)
    private float modifyDamageBasedOnEnchantment(float originalValue, DamageSource source, float amount) {
        float[] additionalValue = {0.0F};
        LivingEntity thisAsLiving = (LivingEntity)(Object)this;

        if (source.getAttacker() != null && source.getAttacker() instanceof LivingEntity && !source.isProjectile()) {
            List<ModifyEnchantmentDamageDealtPower> damageDealtPowers = OriginComponent.getPowers(source.getAttacker(), ModifyEnchantmentDamageDealtPower.class).stream().filter(p -> p.doesApply(source, amount, thisAsLiving)).collect(Collectors.toList());

            damageDealtPowers.forEach(power -> additionalValue[0] += power.baseValue);

            for (ModifyEnchantmentDamageDealtPower power : damageDealtPowers) {
                for (int i = 0; i < EnchantmentHelper.getLevel(power.enchantment, ((LivingEntity)source.getAttacker()).getEquippedStack(EquipmentSlot.MAINHAND)); i++) {
                    additionalValue[0] = OriginComponent.modify(source.getAttacker(), ModifyEnchantmentDamageDealtPower.class,
                            additionalValue[0], enchantmentDamageTakenPower -> true, p -> p.executeActions(thisAsLiving));
                }
            }
        }

        List<ModifyEnchantmentDamageTakenPower> damageTakenPowers = OriginComponent.getPowers(this, ModifyEnchantmentDamageTakenPower.class).stream().filter(p -> p.doesApply(source, amount)).collect(Collectors.toList());

        damageTakenPowers.forEach(power -> additionalValue[0] += power.baseValue);

        if (source.getAttacker() != null && source.getAttacker() instanceof LivingEntity) {
            for (ModifyEnchantmentDamageTakenPower power : damageTakenPowers) {
                for (int i = 0; i < EnchantmentHelper.getLevel(power.enchantment, ((LivingEntity)source.getAttacker()).getEquippedStack(EquipmentSlot.MAINHAND)); i++) {
                    additionalValue[0] = OriginComponent.modify(this, ModifyEnchantmentDamageTakenPower.class,
                            additionalValue[0], enchantmentDamageTakenPower -> true, p -> p.executeActions(source.getAttacker()));
                }
            }
        }

        hasModifiedDamage = originalValue +  additionalValue[0] != originalValue;

        return originalValue + additionalValue[0];
    }

    @Inject(method = "damage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;isSleeping()Z"), cancellable = true)
    private void preventHitIfDamageIsZero(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if(hasModifiedDamage && amount == 0.0F) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "shouldDisplaySoulSpeedEffects", at = @At("HEAD"), cancellable = true)
    private void shouldDisplaySoulSpeedEffects(CallbackInfoReturnable<Boolean> cir) {
        if (OriginComponent.hasPower(this, ModifySoulSpeedPower.class)) {
            int soulSpeedValue = (int)OriginComponent.modify(this, ModifySoulSpeedPower.class, EnchantmentHelper.getEquipmentLevel(Enchantments.SOUL_SPEED, (LivingEntity)(Object)this));
            cir.setReturnValue(this.age % 5 == 0 && this.getVelocity().x != 0.0D && this.getVelocity().z != 0.0D && !this.isSpectator() && soulSpeedValue > 0 && ((LivingEntityAccessor)this).invokeIsOnSoulSpeedBlock());
        }
    }

    @Inject(method = "canHaveStatusEffect", at = @At("HEAD"), cancellable = true)
    private void makeImmuneToBlacklistedEffects(StatusEffectInstance effect, CallbackInfoReturnable<Boolean> cir) {
        for (EffectWhitelistPower power : OriginComponent.getPowers(this, EffectWhitelistPower.class)) {
            if(!power.doesApply(effect)) {
                cir.setReturnValue(false);
            }
        }
    }

    @Inject(method = "isOnSoulSpeedBlock", at = @At("HEAD"), cancellable = true)
    private void isOnSoulSpeedBlock(CallbackInfoReturnable<Boolean> cir) {
        OriginComponent.getPowers(this, ModifySoulSpeedPower.class).forEach(power -> {
            if (power.blockCondition != null) {
                cir.setReturnValue(power.blockCondition.test(new CachedBlockPosition(this.world, this.getVelocityAffectingPos(), true)));
            }
        });
    }

    @ModifyVariable(method = "addSoulSpeedBoostIfNeeded", at = @At("STORE"), ordinal = 0)
    private int replaceLevelOfSoulSpeed(int i) {
        return i = (int)OriginComponent.modify(this, ModifySoulSpeedPower.class, i);
    }

    @Inject(method = "getVelocityMultiplier", at = @At("HEAD"), cancellable = true)
    private void getVelocityMultiplier(CallbackInfoReturnable<Float> cir) {
        if (OriginComponent.hasPower(this, ModifySoulSpeedPower.class)) {
            int soulSpeedValue = (int)OriginComponent.modify(this, ModifySoulSpeedPower.class, EnchantmentHelper.getEquipmentLevel(Enchantments.SOUL_SPEED, (LivingEntity)(Object)this));
            if (soulSpeedValue <= 0 || !this.isOnSoulSpeedBlock()) {
                cir.setReturnValue(super.getVelocityMultiplier());
            } else {
                cir.setReturnValue(1.0F);
            }
        }
    }

    @Inject(method = "addSoulSpeedBoostIfNeeded", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;damage(ILnet/minecraft/entity/LivingEntity;Ljava/util/function/Consumer;)V"), locals = LocalCapture.CAPTURE_FAILSOFT, cancellable = true)
    private void itemStack(CallbackInfo ci, int i) {
        int baseValue = (int)OriginComponent.modify(this, ModifySoulSpeedPower.class, 0);
        if (OriginComponent.hasPower(this, ModifySoulSpeedPower.class) && i == baseValue) {
            ci.cancel();
        }
    }

    @Inject(method = "isUndead", at = @At("HEAD"), cancellable = true)
    private void invertInstantEffects(CallbackInfoReturnable<Boolean> cir) {
        if (OriginComponent.hasPower(this, InvertInstantEffectsPower.class)) {
            cir.setReturnValue(true);
        }
    }

    @Unique
    private int framesOnGround;

    @Inject(method = "baseTick", at = @At("HEAD"))
    private void tick(CallbackInfo ci) {
        if (this.isDead() || !((LivingEntity)(Object)this instanceof PlayerEntity)) return;
        if (OriginComponent.hasPower(this, HoverPower.class)) {
            this.setVelocity(this.getVelocity().multiply(1.0, 0.0, 1.0));
            this.fallDistance = 0.0F;
        }
        ModComponents.getOriginComponent((PlayerEntity)(Object) this).getPowers(EdibleItemPower.class, true).forEach(EdibleItemPower::tempTick);
        if (OriginComponent.hasPower(this, BunnyHopPower.class) && !this.world.isClient) {
            BunnyHopPower bunnyHopPower = OriginComponent.getPowers(this, BunnyHopPower.class).get(0);
            if (framesOnGround > 4) {
                bunnyHopPower.setValue(0);
                OriginComponent.sync((PlayerEntity)(Object)this);
            }
            if (this.onGround || this.isTouchingWater() || this.isInLava() || this.hasVehicle() || this.isFallFlying()) {
                if (framesOnGround <= 4) {
                    framesOnGround += 1;
                }
            } else {
                this.framesOnGround = 0;
            }
        }
    }

    @Inject(method = "travel", at = @At("HEAD"))
    private void travel(Vec3d movementInput, CallbackInfo ci) {
        if (this.isDead() || !((LivingEntity)(Object)this instanceof PlayerEntity)) return;
        if (OriginComponent.hasPower(this, BunnyHopPower.class)) {
            BunnyHopPower bunnyHopPower = OriginComponent.getPowers(this, BunnyHopPower.class).get(0);
            if (!this.world.isClient && this.framesOnGround == 0 && this.age % bunnyHopPower.tickRate == 0 && bunnyHopPower.getValue() < bunnyHopPower.getMax()) {
                bunnyHopPower.increment();
                OriginComponent.sync((PlayerEntity)(Object)this);
            }
            this.updateVelocity((float)bunnyHopPower.increasePerTick * bunnyHopPower.getValue(), movementInput);
        }
    }
}
