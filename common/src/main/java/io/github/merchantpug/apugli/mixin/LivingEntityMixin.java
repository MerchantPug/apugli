package io.github.merchantpug.apugli.mixin;

import com.mojang.datafixers.util.Pair;
import io.github.apace100.origins.component.OriginComponent;
import io.github.merchantpug.apugli.Apugli;
import io.github.merchantpug.apugli.access.HiddenEffectStatus;
import io.github.merchantpug.apugli.access.ItemStackAccess;
import io.github.merchantpug.apugli.powers.*;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
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

import java.util.Iterator;
import java.util.List;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    @Shadow public abstract boolean isFallFlying();

    @Shadow public abstract boolean isDead();

    @Shadow protected abstract boolean isOnSoulSpeedBlock();

    @Shadow public abstract float getHealth();

    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "applyFoodEffects", at = @At("HEAD"), cancellable = true)
    private void applyFoodEffects(ItemStack stack, World world, LivingEntity targetEntity, CallbackInfo ci) {
        if (((ItemStackAccess)(Object)stack).isItemStackFood()) {
            List<Pair<StatusEffectInstance, Float>> list = ((ItemStackAccess)(Object)stack).getItemStackFoodComponent().getStatusEffects();
            Iterator var6 = list.iterator();

            while(var6.hasNext()) {
                Pair<StatusEffectInstance, Float> pair = (Pair)var6.next();
                if (!world.isClient && pair.getFirst() != null && world.random.nextFloat() < pair.getSecond()) {
                    targetEntity.addStatusEffect(new StatusEffectInstance(pair.getFirst()));
                }
            }
            ci.cancel();
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
        if (OriginComponent.hasPower(this, BunnyHopPower.class) && !this.world.isClient) {
            Apugli.LOGGER.info("Frames on Ground: " + framesOnGround);
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
            Apugli.LOGGER.info(bunnyHopPower.getValue());
            this.updateVelocity((float)bunnyHopPower.increasePerTick * bunnyHopPower.getValue(), movementInput);
        }
    }
}
