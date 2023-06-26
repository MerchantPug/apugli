package net.merchantpug.apugli.mixin.xplatform.common;

import net.merchantpug.apugli.platform.Services;
import net.merchantpug.apugli.power.CustomDeathSoundPower;
import net.merchantpug.apugli.power.CustomHurtSoundPower;
import net.merchantpug.apugli.registry.power.ApugliPowers;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

    @Shadow
    protected abstract void defineSynchedData();

    @Shadow
    public abstract boolean isAlive();

    @Shadow
    public abstract void baseTick();

    @Shadow
    protected abstract boolean onSoulSpeedBlock();

    public LivingEntityMixin(EntityType<? extends LivingEntity> entityType, Level world) {
        super(entityType, world);
    }

    @Redirect(method = "handleEntityEvent", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;playSound(Lnet/minecraft/sounds/SoundEvent;FF)V", ordinal = 1))
    private void playHurtSoundsStatus(LivingEntity instance, SoundEvent soundEvent, float v, float p) {
        List<CustomHurtSoundPower> powers = Services.POWER.getPowers(instance, ApugliPowers.CUSTOM_HURT_SOUND.get());
        if (!powers.isEmpty()) {
            if (powers.stream().anyMatch(CustomHurtSoundPower::isMuted)) return;
            powers.forEach(power -> power.playSound(instance));
            return;
        }
        instance.playSound(soundEvent, v, p);
    }

    @Redirect(method = "handleEntityEvent", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;playSound(Lnet/minecraft/sounds/SoundEvent;FF)V", ordinal = 2))
    private void playDeathSoundsStatus(LivingEntity instance, SoundEvent soundEvent, float v, float p) {
        List<CustomDeathSoundPower> powers = Services.POWER.getPowers(instance, ApugliPowers.CUSTOM_DEATH_SOUND.get());
        if (!powers.isEmpty()) {
            if (powers.stream().anyMatch(CustomDeathSoundPower::isMuted)) return;
            powers.forEach(power -> power.playSound(instance));
            return;
        }
        instance.playSound(soundEvent, v, p);
    }

    @Redirect(method = "hurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;playSound(Lnet/minecraft/sounds/SoundEvent;FF)V"))
    private void playDeathSounds(LivingEntity instance, SoundEvent soundEvent, float v, float p) {
        List<CustomDeathSoundPower> powers = Services.POWER.getPowers(instance, ApugliPowers.CUSTOM_DEATH_SOUND.get());
        if (!powers.isEmpty()) {
            if (powers.stream().anyMatch(CustomDeathSoundPower::isMuted)) return;
            powers.forEach(power -> power.playSound(instance));
            return;
        }
        instance.playSound(soundEvent, v, p);
    }

    @Inject(method = "playHurtSound", at = @At("HEAD"), cancellable = true)
    private void playHurtSounds(DamageSource source, CallbackInfo ci) {
        List<CustomHurtSoundPower> powers = Services.POWER.getPowers((LivingEntity)(Object)this, ApugliPowers.CUSTOM_HURT_SOUND.get());
        if (powers.isEmpty()) return;
        if (powers.stream().anyMatch(CustomHurtSoundPower::isMuted)) ci.cancel();
        powers.forEach(power -> power.playSound(this));
        ci.cancel();
    }

    @Inject(method = "canSpawnSoulSpeedParticle", at = @At("HEAD"), cancellable = true)
    private void modifyShouldDisplaySoulSpeedEffects(CallbackInfoReturnable<Boolean> cir) {
        LivingEntity thisAsLiving = (LivingEntity)(Object)this;
        if (Services.POWER.hasPower(thisAsLiving, ApugliPowers.MODIFY_SOUL_SPEED.get())) {
            int soulSpeedValue = (int) Services.PLATFORM.applyModifiers(thisAsLiving, ApugliPowers.MODIFY_SOUL_SPEED.get(), EnchantmentHelper.getEnchantmentLevel(Enchantments.SOUL_SPEED, (LivingEntity) (Object) this));
            boolean doesApply;
            if (Services.POWER.getPowers(thisAsLiving, ApugliPowers.MODIFY_SOUL_SPEED.get()).stream().filter(power -> ApugliPowers.MODIFY_SOUL_SPEED.get().getDataFromPower(power).isPresent("block_condition")).toList().size() == 0) {
                doesApply = this.onSoulSpeedBlock();
            } else {
                doesApply = Services.POWER.getPowers(thisAsLiving, ApugliPowers.MODIFY_SOUL_SPEED.get()).stream().filter(power -> ApugliPowers.MODIFY_SOUL_SPEED.get().getDataFromPower(power).isPresent("block_condition")).anyMatch(power -> Services.CONDITION.checkBlock(ApugliPowers.MODIFY_SOUL_SPEED.get().getDataFromPower(power), "block_condition", this.level(), this.getBlockPosBelowThatAffectsMyMovement()));
            }
            cir.setReturnValue(this.tickCount % 5 == 0 && this.getDeltaMovement().x != 0.0D && this.getDeltaMovement().z != 0.0D && !this.isSpectator() && soulSpeedValue > 0 && doesApply);
        }
    }

    @Inject(method = "onSoulSpeedBlock", at = @At("HEAD"), cancellable = true)
    private void modifyIsOnSoulSpeedBlock(CallbackInfoReturnable<Boolean> cir) {
        LivingEntity thisAsLiving = (LivingEntity)(Object)this;
        if (Services.POWER.getPowers(thisAsLiving, ApugliPowers.MODIFY_SOUL_SPEED.get()).stream().filter(power -> ApugliPowers.MODIFY_SOUL_SPEED.get().getDataFromPower(power).isPresent("block_condition")).toList().size() > 0) {
            if (Services.POWER.getPowers(thisAsLiving, ApugliPowers.MODIFY_SOUL_SPEED.get()).stream().anyMatch(power -> ApugliPowers.MODIFY_SOUL_SPEED.get().getDataFromPower(power).isPresent("block_condition") && Services.CONDITION.checkBlock(ApugliPowers.MODIFY_SOUL_SPEED.get().getDataFromPower(power), "block_condition", this.level(), this.getBlockPosBelowThatAffectsMyMovement()))) {
                cir.setReturnValue(true);
            } else if (Services.POWER.getPowers(thisAsLiving, ApugliPowers.MODIFY_SOUL_SPEED.get()).stream().noneMatch(power -> ApugliPowers.MODIFY_SOUL_SPEED.get().getDataFromPower(power).isPresent("block_condition") && Services.CONDITION.checkBlock(ApugliPowers.MODIFY_SOUL_SPEED.get().getDataFromPower(power), "block_condition", this.level(), this.getBlockPosBelowThatAffectsMyMovement()))) {
                cir.setReturnValue(false);
            }
        }
    }

    @ModifyVariable(method = "tryAddSoulSpeed", at = @At("STORE"), ordinal = 0)
    private int replaceLevelOfSoulSpeed(int i) {
        int baseValue = this.level().getBlockState(this.getBlockPosBelowThatAffectsMyMovement()).is(BlockTags.SOUL_SPEED_BLOCKS) ? i : 0;
        if (!this.level().getBlockState(this.getBlockPosBelowThatAffectsMyMovement()).is(BlockTags.SOUL_SPEED_BLOCKS) || i < baseValue) {
            return i = (int) Services.PLATFORM.applyModifiers((LivingEntity)(Object)this, ApugliPowers.MODIFY_SOUL_SPEED.get(), baseValue);
        }
        return i;
    }

    @Inject(method = "getBlockSpeedFactor", at = @At("HEAD"), cancellable = true)
    private void modifyVelocityMultiplier(CallbackInfoReturnable<Float> cir) {
        LivingEntity thisAsLiving = (LivingEntity)(Object)this;
        if (Services.POWER.hasPower(thisAsLiving, ApugliPowers.MODIFY_SOUL_SPEED.get())) {
            int soulSpeedValue = (int) Services.PLATFORM.applyModifiers(thisAsLiving, ApugliPowers.MODIFY_SOUL_SPEED.get(), EnchantmentHelper.getEnchantmentLevel(Enchantments.SOUL_SPEED, thisAsLiving));
            if (soulSpeedValue <= 0 || (Services.POWER.getPowers(thisAsLiving, ApugliPowers.MODIFY_SOUL_SPEED.get()).stream().filter(power -> ApugliPowers.MODIFY_SOUL_SPEED.get().getDataFromPower(power).isPresent("block_condition")).toList().size() == 0 && !this.onSoulSpeedBlock()) || Services.POWER.getPowers(thisAsLiving, ApugliPowers.MODIFY_SOUL_SPEED.get()).stream().anyMatch(power -> ApugliPowers.MODIFY_SOUL_SPEED.get().getDataFromPower(power).isPresent("block_condition") && Services.CONDITION.checkBlock(ApugliPowers.MODIFY_SOUL_SPEED.get().getDataFromPower(power), "block_condition", this.level(), this.getBlockPosBelowThatAffectsMyMovement()))) {
                cir.setReturnValue(super.getBlockSpeedFactor());
            } else {
                cir.setReturnValue(1.0F);
            }
        }
    }

    @Inject(method = "tryAddSoulSpeed", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;hurtAndBreak(ILnet/minecraft/world/entity/LivingEntity;Ljava/util/function/Consumer;)V"), locals = LocalCapture.CAPTURE_FAILSOFT, cancellable = true)
    private void itemStack(CallbackInfo ci, int i) {
        int baseValue = (int) Services.PLATFORM.applyModifiers((LivingEntity)(Object)this, ApugliPowers.MODIFY_SOUL_SPEED.get(), 0);
        if (Services.POWER.hasPower((LivingEntity)(Object)this, ApugliPowers.MODIFY_SOUL_SPEED.get()) && i == baseValue) {
            ci.cancel();
        }
    }

    @Inject(method = "isInvertedHealAndHarm", at = @At("HEAD"), cancellable = true)
    private void invertInstantEffects(CallbackInfoReturnable<Boolean> cir) {
        if (Services.POWER.hasPower((LivingEntity)(Object)this, ApugliPowers.INVERT_INSTANT_EFFECTS.get())) {
            cir.setReturnValue(true);
        }
    }

}
