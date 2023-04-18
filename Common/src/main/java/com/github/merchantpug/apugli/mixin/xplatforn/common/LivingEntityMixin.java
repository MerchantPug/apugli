<<<<<<<< HEAD:src/main/java/net/merchantpug/apugli/mixin/LivingEntityMixin.java
package net.merchantpug.apugli.mixin;

import net.merchantpug.apugli.access.ItemStackAccess;
import net.merchantpug.apugli.component.ApugliEntityComponents;
import net.merchantpug.apugli.component.HitsOnTargetComponent;
import net.merchantpug.apugli.power.*;
import io.github.apace100.apoli.component.PowerHolderComponent;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.Pair;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
========
package com.github.merchantpug.apugli.mixin.xplatforn.common;

import com.github.merchantpug.apugli.access.ItemStackAccess;
import com.github.merchantpug.apugli.access.LivingEntityAccess;
import com.github.merchantpug.apugli.mixin.xplatforn.common.accessor.LivingEntityAccessor;
import com.github.merchantpug.apugli.registry.power.ApugliPowers;
import dev.migrate.merchantpug.apugli.power.*;
import great.migrate.merchantpug.apugli.power.*;
import the.great.migration.merchantpug.apugli.power.*;
import com.github.merchantpug.apugli.util.ApugliConfig;
import com.github.merchantpug.apugli.util.HitsOnTargetUtil;
import io.github.apace100.apoli.component.PowerHolderComponent;
import com.github.merchantpug.apugli.util.ItemStackFoodComponentUtil;
import net.minecraft.entity.*;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Tuple;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
>>>>>>>> pr/25:Common/src/main/java/com/github/merchantpug/apugli/mixin/xplatforn/common/LivingEntityMixin.java
import org.jetbrains.annotations.Nullable;
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
import java.util.stream.Stream;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

    @Shadow public abstract boolean isFallFlying();

    @Shadow protected abstract boolean isOnSoulSpeedBlock();

    @Shadow public abstract boolean isDead();

    @Shadow protected abstract void defineSynchedData();

    @Shadow public abstract SoundEvent getEatSound(ItemStack stack);

    @Shadow public abstract boolean isAlive();

    @Shadow public abstract @Nullable LivingEntity getPrimeAdversary();

<<<<<<<< HEAD:src/main/java/net/merchantpug/apugli/mixin/LivingEntityMixin.java
    @Shadow public abstract boolean addStatusEffect(StatusEffectInstance effect);

    @Shadow public abstract boolean blockedByShield(DamageSource source);

    @Shadow public abstract void baseTick();

    public LivingEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

========
    public LivingEntityMixin(EntityType<? extends LivingEntity> entityType, Level world) {
        super(entityType, world);
    }

    @Unique private final HashMap<Entity, Tuple<Integer, Integer>> hitsHashmap = new HashMap<>();
>>>>>>>> pr/25:Common/src/main/java/com/github/merchantpug/apugli/mixin/xplatforn/common/LivingEntityMixin.java

    @Redirect(method = "handleStatus", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;playSound(Lnet/minecraft/sound/SoundEvent;FF)V", ordinal = 1))
    private void playHurtSoundsStatus(LivingEntity instance, SoundEvent soundEvent, float v, float p) {
        List<CustomHurtSoundPower> powers = PowerHolderComponent.getPowers(instance, CustomHurtSoundPower.class);
        if(!powers.isEmpty()) {
            if(powers.stream().anyMatch(CustomHurtSoundPower::isMuted)) return;
            powers.forEach(power -> power.playHurtSound(instance));
            return;
        }
        instance.playSound(soundEvent, v, p);
    }

    @Redirect(method = "handleStatus", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;playSound(Lnet/minecraft/sound/SoundEvent;FF)V", ordinal = 2))
    private void playDeathSoundsStatus(LivingEntity instance, SoundEvent soundEvent, float v, float p) {
        List<CustomDeathSoundPower> powers = PowerHolderComponent.getPowers(instance, CustomDeathSoundPower.class);
        if(!powers.isEmpty()) {
            if(powers.stream().anyMatch(CustomDeathSoundPower::isMuted)) return;
            powers.forEach(power -> power.playDeathSound(instance));
            return;
        }
        instance.playSound(soundEvent, v, p);
    }

    @Redirect(method = "damage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;playSound(Lnet/minecraft/sound/SoundEvent;FF)V"))
    private void playDeathSounds(LivingEntity instance, SoundEvent soundEvent, float v, float p) {
        List<CustomDeathSoundPower> powers = PowerHolderComponent.getPowers(instance, CustomDeathSoundPower.class);
        if(!powers.isEmpty()) {
            if(powers.stream().anyMatch(CustomDeathSoundPower::isMuted)) return;
            powers.forEach(power -> power.playDeathSound(instance));
            return;
        }
        instance.playSound(soundEvent, v, p);
    }

    @Inject(method = "playHurtSound", at = @At("HEAD"), cancellable = true)
    private void playHurtSounds(DamageSource source, CallbackInfo ci) {
        List<CustomHurtSoundPower> powers = PowerHolderComponent.getPowers(this, CustomHurtSoundPower.class);
        if(powers.isEmpty()) return;
        if(powers.stream().anyMatch(CustomHurtSoundPower::isMuted)) ci.cancel();
        powers.forEach(power -> power.playHurtSound(this));
        ci.cancel();
    }

<<<<<<<< HEAD:src/main/java/net/merchantpug/apugli/mixin/LivingEntityMixin.java
    @Inject(method = "damage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;applyDamage(Lnet/minecraft/entity/damage/DamageSource;F)V"))
    private void runDamageFunctionsBeforeDamaged(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (source.getAttacker() == null || amount == 0.0F) return;
        PowerHolderComponent.withPowers(source.getAttacker(), ActionOnHarmPower.class, p -> true, p -> p.onHurt((LivingEntity)(Object)this, source, amount));
        PowerHolderComponent.withPowers(this, ActionWhenHarmedPower.class, p -> true, p -> p.whenHurt(source.getAttacker(), source, amount));
    }

    @Inject(method = "damage", at = @At("RETURN"))
    private void runDamageFunctions(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue() || source.getAttacker() == null) return;
        LivingEntity thisAsLiving = (LivingEntity)(Object)this;
        if (thisAsLiving instanceof TameableEntity tameable) {
            PowerHolderComponent.withPowers(tameable.getOwner(), ActionWhenTameHitPower.class, p -> true, p -> p.whenHit(tameable, source.getAttacker(), source, amount));
========
    @Inject(method = "hurt", at = @At("RETURN"))
    private void runDamageFunctions(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if(!cir.getReturnValue() || source.getEntity() == null) return;
        if((Object)this instanceof TamableAnimal tamable) {
            ApugliPowers.ACTION_WHEN_TAME_HIT.get().whenTameHit(tamable, source.getEntity(), source, amount);
>>>>>>>> pr/25:Common/src/main/java/com/github/merchantpug/apugli/mixin/xplatforn/common/LivingEntityMixin.java
        }
        if(source.getEntity() instanceof TamableAnimal tamable) {
            ApugliPowers.ACTION_WHEN_TAME_HIT.get().whenTameHit(tamable, this, source, amount);
        }
<<<<<<<< HEAD:src/main/java/net/merchantpug/apugli/mixin/LivingEntityMixin.java
        HitsOnTargetComponent hitsComponent = ApugliEntityComponents.HITS_ON_TARGET_COMPONENT.get(thisAsLiving);
        hitsComponent.setHits(source.getAttacker(), hitsComponent.getHits().getOrDefault(source.getAttacker().getId(), new Pair<>(0, 0)).getLeft() + 1, 0);
        ApugliEntityComponents.HITS_ON_TARGET_COMPONENT.sync(thisAsLiving);
========
        setHits(source.getEntity(), hitsHashmap.getOrDefault(source.getEntity(), new Tuple<>(0, 0)).getA() + 1, 0);
        HitsOnTargetUtil.sendPacket((LivingEntity)(Object)this, source.getEntity(), HitsOnTargetUtil.PacketType.SET, getHits().get(source.getEntity()).getA(), 0);
>>>>>>>> pr/25:Common/src/main/java/com/github/merchantpug/apugli/mixin/xplatforn/common/LivingEntityMixin.java
    }

    @Unique private boolean apugli$hasModifiedDamage;

    @ModifyVariable(method = "damage", at = @At("HEAD"), argsOnly = true)
    private float modifyDamageBasedOnEnchantment(float originalValue, DamageSource source, float amount) {
        float[] additionalValue = {0.0F};
        LivingEntity thisAsLiving = (LivingEntity)(Object)this;

        if(source.getEntity() != null && source.getEntity() instanceof LivingEntity && !source.isProjectile()) {
            List<ModifyEnchantmentDamageDealtPower> damageDealtPowers = PowerHolderComponent.getPowers(source.getEntity(), ModifyEnchantmentDamageDealtPower.class).stream().filter(p -> p.doesApply(source, amount, thisAsLiving)).toList();

            damageDealtPowers.forEach(power -> additionalValue[0] += power.baseValue);

<<<<<<<< HEAD:src/main/java/net/merchantpug/apugli/mixin/LivingEntityMixin.java
            for (ModifyEnchantmentDamageDealtPower power : damageDealtPowers) {
                for (int i = 0; i < EnchantmentHelper.getLevel(power.enchantment, ((LivingEntity)source.getAttacker()).getEquippedStack(EquipmentSlot.MAINHAND)); ++i) {
                    additionalValue[0] = PowerHolderComponent.modify(source.getAttacker(), ModifyEnchantmentDamageDealtPower.class,
========
            for(ModifyEnchantmentDamageDealtPower power : damageDealtPowers) {
                for(int i = 0; i < EnchantmentHelper.getItemEnchantmentLevel(power.enchantment, ((LivingEntity)source.getEntity()).getItemBySlot(EquipmentSlot.MAINHAND)); i++) {
                    additionalValue[0] = PowerHolderComponent.modify(source.getEntity(), ModifyEnchantmentDamageDealtPower.class,
>>>>>>>> pr/25:Common/src/main/java/com/github/merchantpug/apugli/mixin/xplatforn/common/LivingEntityMixin.java
                            additionalValue[0], enchantmentDamageTakenPower -> true, p -> p.executeActions(thisAsLiving));
                }
            }
        }

        List<ModifyEnchantmentDamageTakenPower> damageTakenPowers = PowerHolderComponent.getPowers(this, ModifyEnchantmentDamageTakenPower.class).stream().filter(p -> p.doesApply(source, amount)).toList();

        damageTakenPowers.forEach(power -> additionalValue[0] += power.baseValue);

<<<<<<<< HEAD:src/main/java/net/merchantpug/apugli/mixin/LivingEntityMixin.java
        if (source.getAttacker() != null && source.getAttacker() instanceof LivingEntity) {
            for (ModifyEnchantmentDamageTakenPower power : damageTakenPowers) {
                for (int i = 0; i < EnchantmentHelper.getLevel(power.enchantment, ((LivingEntity)source.getAttacker()).getEquippedStack(EquipmentSlot.MAINHAND)); ++i) {
========
        if(source.getEntity() != null && source.getEntity() instanceof LivingEntity) {
            for(ModifyEnchantmentDamageTakenPower power : damageTakenPowers) {
                for(int i = 0; i < EnchantmentHelper.getItemEnchantmentLevel(power.enchantment, ((LivingEntity)source.getEntity()).getItemBySlot(EquipmentSlot.MAINHAND)); i++) {
>>>>>>>> pr/25:Common/src/main/java/com/github/merchantpug/apugli/mixin/xplatforn/common/LivingEntityMixin.java
                    additionalValue[0] = PowerHolderComponent.modify(this, ModifyEnchantmentDamageTakenPower.class,
                            additionalValue[0], enchantmentDamageTakenPower -> true, p -> p.executeActions(source.getEntity()));
                }
            }
        }

        apugli$hasModifiedDamage = originalValue +  additionalValue[0] != originalValue;

        return originalValue + additionalValue[0];
    }

    @Inject(method = "eatFood", at = @At("HEAD"), cancellable = true)
<<<<<<<< HEAD:src/main/java/net/merchantpug/apugli/mixin/LivingEntityMixin.java
    private void eatStackFood(World world, ItemStack stack, CallbackInfoReturnable<ItemStack> cir) {
        if (((ItemStackAccess)(Object)stack).getItemStackFoodComponent() != null) {
            world.playSound(null, this.getX(), this.getY(), this.getZ(), this.getEatSound(stack), SoundCategory.NEUTRAL, 1.0f, 1.0f + (world.random.nextFloat() - world.random.nextFloat()) * 0.4f);
            List<com.mojang.datafixers.util.Pair<StatusEffectInstance, Float>> list = ((ItemStackAccess)(Object)stack).getItemStackFoodComponent().getStatusEffects();
            for (com.mojang.datafixers.util.Pair<StatusEffectInstance, Float> pair : list) {
                if (world.isClient || pair.getFirst() == null || !(world.random.nextFloat() < pair.getSecond()))
                    continue;
                this.addStatusEffect(new StatusEffectInstance(pair.getFirst()));
            }
========
    private void eatFood(Level world, ItemStack stack, CallbackInfoReturnable<ItemStack> cir) {
        if(((ItemStackAccess)(Object)stack).isItemStackFood()) {
            world.playSound(null, this.getX(), this.getY(), this.getZ(), this.getEatSound(stack), SoundSource.NEUTRAL, 1.0f, 1.0f + (world.random.nextFloat() - world.random.nextFloat()) * 0.4f);
            ItemStackFoodComponentUtil.applyFoodEffects(stack, world, (LivingEntity)(Object)this);
>>>>>>>> pr/25:Common/src/main/java/com/github/merchantpug/apugli/mixin/xplatforn/common/LivingEntityMixin.java
            ItemStack newStack = stack.copy();
            if(!((LivingEntity)(Object)this instanceof Player) || !((Player)(Object)this).getAbilities().instabuild) {
                newStack.shrink(1);
            }
            this.gameEvent(GameEvent.EAT);
            cir.setReturnValue(newStack);
        }
    }

    @Unique private float apugli$damageAmountOnDeath;

    @Inject(method = "damage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;onDeath(Lnet/minecraft/entity/damage/DamageSource;)V"))
    private void captureDamageAmount(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        apugli$damageAmountOnDeath = amount;
    }

    @Inject(method = "onDeath", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/damage/DamageSource;getAttacker()Lnet/minecraft/entity/Entity;"))
    private void runActionsOnTargetDeath(DamageSource source, CallbackInfo ci) {
        if(this.level.isClientSide) return;

        if(source.getEntity() == null || !source.getEntity().equals(this.getPrimeAdversary())) {
            PowerHolderComponent.getPowers(this.getPrimeAdversary(), ActionOnTargetDeathPower.class).stream().filter(p -> p.includesPrimeAdversary).forEach(p -> p.onTargetDeath((LivingEntity)(Object)this, source, apugli$damageAmountOnDeath));
            return;
        }

        PowerHolderComponent.getPowers(source.getEntity(), ActionOnTargetDeathPower.class).forEach(p -> p.onTargetDeath((LivingEntity)(Object)this, source, apugli$damageAmountOnDeath));
    }

    @Inject(method = "damage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;isSleeping()Z"), cancellable = true)
    private void preventHitIfDamageIsZero(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if(apugli$hasModifiedDamage && amount == 0.0F) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "shouldDisplaySoulSpeedEffects", at = @At("HEAD"), cancellable = true)
<<<<<<<< HEAD:src/main/java/net/merchantpug/apugli/mixin/LivingEntityMixin.java
    private void modifyShouldDisplaySoulSpeedEffects(CallbackInfoReturnable<Boolean> cir) {
        if (PowerHolderComponent.hasPower(this, ModifySoulSpeedPower.class)) {
            int soulSpeedValue = (int)PowerHolderComponent.modify(this, ModifySoulSpeedPower.class, EnchantmentHelper.getEquipmentLevel(Enchantments.SOUL_SPEED, (LivingEntity)(Object)this));
            boolean doesApply;
            if (PowerHolderComponent.getPowers(this, ModifySoulSpeedPower.class).stream().filter(power -> power.blockCondition != null).toList().size() == 0) {
                doesApply = this.isOnSoulSpeedBlock();
            } else {
                doesApply = PowerHolderComponent.getPowers(this, ModifySoulSpeedPower.class).stream().filter(power -> power.blockCondition != null).anyMatch(power -> power.blockCondition.test(new CachedBlockPosition(this.world, this.getVelocityAffectingPos(), true)));
            }
            cir.setReturnValue(this.age % 5 == 0 && this.getVelocity().x != 0.0D && this.getVelocity().z != 0.0D && !this.isSpectator() && soulSpeedValue > 0 && doesApply);
========
    private void shouldDisplaySoulSpeedEffects(CallbackInfoReturnable<Boolean> cir) {
        if(PowerHolderComponent.hasPower(this, ModifySoulSpeedPower.class)) {
            int soulSpeedValue = (int)PowerHolderComponent.modify(this, ModifySoulSpeedPower.class, EnchantmentHelper.getEnchantmentLevel(Enchantments.SOUL_SPEED, (LivingEntity)(Object)this));
            cir.setReturnValue(this.tickCount % 5 == 0 && this.getDeltaMovement().x != 0.0D && this.getDeltaMovement().z != 0.0D && !this.isSpectator() && soulSpeedValue > 0 && ((LivingEntityAccessor)this).invokeIsOnSoulSpeedBlock());
>>>>>>>> pr/25:Common/src/main/java/com/github/merchantpug/apugli/mixin/xplatforn/common/LivingEntityMixin.java
        }
    }

    @Inject(method = "isOnSoulSpeedBlock", at = @At("HEAD"), cancellable = true)
<<<<<<<< HEAD:src/main/java/net/merchantpug/apugli/mixin/LivingEntityMixin.java
    private void modifyIsOnSoulSpeedBlock(CallbackInfoReturnable<Boolean> cir) {
        if (PowerHolderComponent.getPowers(this, ModifySoulSpeedPower.class).stream().filter(power -> power.blockCondition != null).toList().size() > 0) {
            if (PowerHolderComponent.getPowers(this, ModifySoulSpeedPower.class).stream().anyMatch(power -> power.blockCondition != null && power.blockCondition.test(new CachedBlockPosition(this.world, this.getVelocityAffectingPos(), true)))) {
                cir.setReturnValue(true);
            } else if (PowerHolderComponent.getPowers(this, ModifySoulSpeedPower.class).stream().filter(power -> power.blockCondition != null).noneMatch(power -> power.blockCondition.test(new CachedBlockPosition(this.world, this.getVelocityAffectingPos(), true)))) {
                cir.setReturnValue(false);
========
    private void isOnSoulSpeedBlock(CallbackInfoReturnable<Boolean> cir) {
        PowerHolderComponent.getPowers(this, ModifySoulSpeedPower.class).forEach(power -> {
            if(power.blockCondition != null) {
                cir.setReturnValue(power.blockCondition.test(new BlockInWorld(this.level, this.getBlockPosBelowThatAffectsMyMovement(), true)));
>>>>>>>> pr/25:Common/src/main/java/com/github/merchantpug/apugli/mixin/xplatforn/common/LivingEntityMixin.java
            }
        }
    }

    @ModifyVariable(method = "addSoulSpeedBoostIfNeeded", at = @At("STORE"), ordinal = 0)
    private int replaceLevelOfSoulSpeed(int i) {
        int baseValue = this.world.getBlockState(this.getVelocityAffectingPos()).isIn(BlockTags.SOUL_SPEED_BLOCKS) ? i : 0;
        if (!this.world.getBlockState(this.getVelocityAffectingPos()).isIn(BlockTags.SOUL_SPEED_BLOCKS) || i < baseValue) {
            return i = (int)PowerHolderComponent.modify(this, ModifySoulSpeedPower.class, baseValue);
        }
        return i;
    }

    @Inject(method = "getVelocityMultiplier", at = @At("HEAD"), cancellable = true)
<<<<<<<< HEAD:src/main/java/net/merchantpug/apugli/mixin/LivingEntityMixin.java
    private void modifyVelocityMultiplier(CallbackInfoReturnable<Float> cir) {
        if (PowerHolderComponent.hasPower(this, ModifySoulSpeedPower.class)) {
            int soulSpeedValue = (int)PowerHolderComponent.modify(this, ModifySoulSpeedPower.class, EnchantmentHelper.getEquipmentLevel(Enchantments.SOUL_SPEED, (LivingEntity)(Object)this));
            if (soulSpeedValue <= 0 || (PowerHolderComponent.getPowers(this, ModifySoulSpeedPower.class).stream().filter(power -> power.blockCondition != null).toList().size() == 0 && !this.isOnSoulSpeedBlock()) || PowerHolderComponent.getPowers(this, ModifySoulSpeedPower.class).stream().filter(power -> power.blockCondition != null).anyMatch(power -> power.blockCondition.test(new CachedBlockPosition(this.world, this.getVelocityAffectingPos(), true)))) {
                cir.setReturnValue(super.getVelocityMultiplier());
========
    private void getVelocityMultiplier(CallbackInfoReturnable<Float> cir) {
        if(PowerHolderComponent.hasPower(this, ModifySoulSpeedPower.class)) {
            int soulSpeedValue = (int)PowerHolderComponent.modify(this, ModifySoulSpeedPower.class, EnchantmentHelper.getEnchantmentLevel(Enchantments.SOUL_SPEED, (LivingEntity)(Object)this));
            if(soulSpeedValue <= 0 || !this.isOnSoulSpeedBlock()) {
                cir.setReturnValue(super.getBlockSpeedFactor());
>>>>>>>> pr/25:Common/src/main/java/com/github/merchantpug/apugli/mixin/xplatforn/common/LivingEntityMixin.java
            } else {
                cir.setReturnValue(1.0F);
            }
        }
    }

    @Inject(method = "addSoulSpeedBoostIfNeeded", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;damage(ILnet/minecraft/entity/LivingEntity;Ljava/util/function/Consumer;)V"), locals = LocalCapture.CAPTURE_FAILSOFT, cancellable = true)
    private void itemStack(CallbackInfo ci, int i) {
        int baseValue = (int)PowerHolderComponent.modify(this, ModifySoulSpeedPower.class, 0);
        if(PowerHolderComponent.hasPower(this, ModifySoulSpeedPower.class) && i == baseValue) {
            ci.cancel();
        }
    }

    @Inject(method = "isUndead", at = @At("HEAD"), cancellable = true)
    private void invertInstantEffects(CallbackInfoReturnable<Boolean> cir) {
        if(PowerHolderComponent.hasPower(this, InvertInstantEffectsPower.class)) {
            cir.setReturnValue(true);
        }
    }

    @Unique private int apugli$framesOnGround;

    @Inject(method = "baseTick", at = @At("HEAD"))
    private void tick(CallbackInfo ci) {
<<<<<<<< HEAD:src/main/java/net/merchantpug/apugli/mixin/LivingEntityMixin.java
        if (!this.isAlive()) return;
        if (PowerHolderComponent.hasPower(this, HoverPower.class)) {
            this.setVelocity(this.getVelocity().multiply(1.0, 0.0, 1.0));
            this.fallDistance = 0.0F;
        }
        if (PowerHolderComponent.hasPower(this, BunnyHopPower.class) && !this.world.isClient) {
========
        if(!this.isAlive()) return;
        if(!this.level.isClientSide && (LivingEntity)(Object)this instanceof LivingEntity) {
            if(this.hitsHashmap.size() > 0) {
                Iterator<Map.Entry<Entity, Tuple<Integer, Integer>>> it = hitsHashmap.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry<Entity, Tuple<Integer, Integer>> values = it.next();
                    Entity entity = values.getKey();
                    int hitAmount = values.getValue().getA();
                    int currentTime = values.getValue().getB();
                    if(currentTime > ApugliConfig.resetTimerTicks) {
                        it.remove();
                        if(entity instanceof LivingEntity) {
                            HitsOnTargetUtil.sendPacket((LivingEntity) (Object) this, (LivingEntity)entity, HitsOnTargetUtil.PacketType.REMOVE, 0, 0);
                        }
                        continue;
                    }
                    values.setValue(new Tuple<>(hitAmount, currentTime + 1));
                }
            }
        }
        if(PowerHolderComponent.hasPower(this, HoverPower.class)) {
            this.setDeltaMovement(this.getDeltaMovement().multiply(1.0, 0.0, 1.0));
            this.fallDistance = 0.0F;
        }
        PowerHolderComponent.KEY.get(this).getPowers(EdibleItemPower.class, true).forEach(EdibleItemPower::tempTick);
        if(PowerHolderComponent.hasPower(this, BunnyHopPower.class) && !this.level.isClientSide) {
>>>>>>>> pr/25:Common/src/main/java/com/github/merchantpug/apugli/mixin/xplatforn/common/LivingEntityMixin.java
            BunnyHopPower bunnyHopPower = PowerHolderComponent.getPowers(this, BunnyHopPower.class).get(0);
            if(apugli$framesOnGround > 4) {
                bunnyHopPower.setValue(0);
                PowerHolderComponent.syncPower(this, bunnyHopPower.getType());
            }
            if(this.onGround || this.isInWater() || this.isInLava() || this.isPassenger() || this.isFallFlying()) {
                if(apugli$framesOnGround <= 4) {
                    apugli$framesOnGround += 1;
                }
            } else {
                this.apugli$framesOnGround = 0;
            }
        }
    }

    @Inject(method = "travel", at = @At("HEAD"))
    private void travel(Vec3 movementInput, CallbackInfo ci) {
        if(this.isDead()) return;
        if(PowerHolderComponent.hasPower(this, BunnyHopPower.class)) {
            BunnyHopPower bunnyHopPower = PowerHolderComponent.getPowers(this, BunnyHopPower.class).get(0);
            if(!this.level.isClientSide) {
                if(this.apugli$framesOnGround <= 4) {
                    if(this.apugli$framesOnGround == 0) {
                        if(this.tickCount % bunnyHopPower.tickRate == 0) {
                            if(bunnyHopPower.getValue() < bunnyHopPower.getMax()) {
                                bunnyHopPower.increment();
                                PowerHolderComponent.syncPower(this, bunnyHopPower.getType());
                            }
                        }
                    }
                }
            }
            this.moveRelative((float)bunnyHopPower.increasePerTick * bunnyHopPower.getValue(), movementInput);
        }
    }
<<<<<<<< HEAD:src/main/java/net/merchantpug/apugli/mixin/LivingEntityMixin.java
========

    @Override
    public HashMap<Entity, Tuple<Integer, Integer>> getHits() {
        return this.hitsHashmap;
    }

    @Override
    public void setHits(Entity entity, int hitValue, int timer) {
        this.hitsHashmap.put(entity, new Tuple<>(hitValue, timer));
    }
>>>>>>>> pr/25:Common/src/main/java/com/github/merchantpug/apugli/mixin/xplatforn/common/LivingEntityMixin.java
}
