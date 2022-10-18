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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements LivingEntityAccess {

    @Shadow public abstract boolean isFallFlying();

    @Shadow protected abstract boolean isOnSoulSpeedBlock();

    @Shadow public abstract boolean isDead();

    @Shadow protected abstract void defineSynchedData();

    @Shadow public abstract SoundEvent getEatSound(ItemStack stack);

    @Shadow public abstract boolean isAlive();

    @Shadow public abstract @Nullable LivingEntity getPrimeAdversary();

    public LivingEntityMixin(EntityType<? extends LivingEntity> entityType, Level world) {
        super(entityType, world);
    }

    @Unique private final HashMap<Entity, Tuple<Integer, Integer>> hitsHashmap = new HashMap<>();

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

    @Inject(method = "hurt", at = @At("RETURN"))
    private void runDamageFunctions(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if(!cir.getReturnValue() || source.getEntity() == null) return;
        if((Object)this instanceof TamableAnimal tamable) {
            ApugliPowers.ACTION_WHEN_TAME_HIT.get().whenTameHit(tamable, source.getEntity(), source, amount);
        }
        if(source.getEntity() instanceof TamableAnimal tamable) {
            ApugliPowers.ACTION_WHEN_TAME_HIT.get().whenTameHit(tamable, this, source, amount);
        }
        setHits(source.getEntity(), hitsHashmap.getOrDefault(source.getEntity(), new Tuple<>(0, 0)).getA() + 1, 0);
        HitsOnTargetUtil.sendPacket((LivingEntity)(Object)this, source.getEntity(), HitsOnTargetUtil.PacketType.SET, getHits().get(source.getEntity()).getA(), 0);
    }

    @Unique private boolean apugli$hasModifiedDamage;

    @ModifyVariable(method = "damage", at = @At("HEAD"), argsOnly = true)
    private float modifyDamageBasedOnEnchantment(float originalValue, DamageSource source, float amount) {
        float[] additionalValue = {0.0F};
        LivingEntity thisAsLiving = (LivingEntity)(Object)this;

        if(source.getEntity() != null && source.getEntity() instanceof LivingEntity && !source.isProjectile()) {
            List<ModifyEnchantmentDamageDealtPower> damageDealtPowers = PowerHolderComponent.getPowers(source.getEntity(), ModifyEnchantmentDamageDealtPower.class).stream().filter(p -> p.doesApply(source, amount, thisAsLiving)).toList();

            damageDealtPowers.forEach(power -> additionalValue[0] += power.baseValue);

            for(ModifyEnchantmentDamageDealtPower power : damageDealtPowers) {
                for(int i = 0; i < EnchantmentHelper.getItemEnchantmentLevel(power.enchantment, ((LivingEntity)source.getEntity()).getItemBySlot(EquipmentSlot.MAINHAND)); i++) {
                    additionalValue[0] = PowerHolderComponent.modify(source.getEntity(), ModifyEnchantmentDamageDealtPower.class,
                            additionalValue[0], enchantmentDamageTakenPower -> true, p -> p.executeActions(thisAsLiving));
                }
            }
        }

        List<ModifyEnchantmentDamageTakenPower> damageTakenPowers = PowerHolderComponent.getPowers(this, ModifyEnchantmentDamageTakenPower.class).stream().filter(p -> p.doesApply(source, amount)).toList();

        damageTakenPowers.forEach(power -> additionalValue[0] += power.baseValue);

        if(source.getEntity() != null && source.getEntity() instanceof LivingEntity) {
            for(ModifyEnchantmentDamageTakenPower power : damageTakenPowers) {
                for(int i = 0; i < EnchantmentHelper.getItemEnchantmentLevel(power.enchantment, ((LivingEntity)source.getEntity()).getItemBySlot(EquipmentSlot.MAINHAND)); i++) {
                    additionalValue[0] = PowerHolderComponent.modify(this, ModifyEnchantmentDamageTakenPower.class,
                            additionalValue[0], enchantmentDamageTakenPower -> true, p -> p.executeActions(source.getEntity()));
                }
            }
        }

        apugli$hasModifiedDamage = originalValue +  additionalValue[0] != originalValue;

        return originalValue + additionalValue[0];
    }

    @Inject(method = "eatFood", at = @At("HEAD"), cancellable = true)
    private void eatFood(Level world, ItemStack stack, CallbackInfoReturnable<ItemStack> cir) {
        if(((ItemStackAccess)(Object)stack).isItemStackFood()) {
            world.playSound(null, this.getX(), this.getY(), this.getZ(), this.getEatSound(stack), SoundSource.NEUTRAL, 1.0f, 1.0f + (world.random.nextFloat() - world.random.nextFloat()) * 0.4f);
            ItemStackFoodComponentUtil.applyFoodEffects(stack, world, (LivingEntity)(Object)this);
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
    private void shouldDisplaySoulSpeedEffects(CallbackInfoReturnable<Boolean> cir) {
        if(PowerHolderComponent.hasPower(this, ModifySoulSpeedPower.class)) {
            int soulSpeedValue = (int)PowerHolderComponent.modify(this, ModifySoulSpeedPower.class, EnchantmentHelper.getEnchantmentLevel(Enchantments.SOUL_SPEED, (LivingEntity)(Object)this));
            cir.setReturnValue(this.tickCount % 5 == 0 && this.getDeltaMovement().x != 0.0D && this.getDeltaMovement().z != 0.0D && !this.isSpectator() && soulSpeedValue > 0 && ((LivingEntityAccessor)this).invokeIsOnSoulSpeedBlock());
        }
    }

    @Inject(method = "isOnSoulSpeedBlock", at = @At("HEAD"), cancellable = true)
    private void isOnSoulSpeedBlock(CallbackInfoReturnable<Boolean> cir) {
        PowerHolderComponent.getPowers(this, ModifySoulSpeedPower.class).forEach(power -> {
            if(power.blockCondition != null) {
                cir.setReturnValue(power.blockCondition.test(new BlockInWorld(this.level, this.getBlockPosBelowThatAffectsMyMovement(), true)));
            }
        });
    }

    @ModifyVariable(method = "addSoulSpeedBoostIfNeeded", at = @At("STORE"), ordinal = 0)
    private int replaceLevelOfSoulSpeed(int i) {
        return i = (int)PowerHolderComponent.modify(this, ModifySoulSpeedPower.class, i);
    }

    @Inject(method = "getVelocityMultiplier", at = @At("HEAD"), cancellable = true)
    private void getVelocityMultiplier(CallbackInfoReturnable<Float> cir) {
        if(PowerHolderComponent.hasPower(this, ModifySoulSpeedPower.class)) {
            int soulSpeedValue = (int)PowerHolderComponent.modify(this, ModifySoulSpeedPower.class, EnchantmentHelper.getEnchantmentLevel(Enchantments.SOUL_SPEED, (LivingEntity)(Object)this));
            if(soulSpeedValue <= 0 || !this.isOnSoulSpeedBlock()) {
                cir.setReturnValue(super.getBlockSpeedFactor());
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

    @Override
    public HashMap<Entity, Tuple<Integer, Integer>> getHits() {
        return this.hitsHashmap;
    }

    @Override
    public void setHits(Entity entity, int hitValue, int timer) {
        this.hitsHashmap.put(entity, new Tuple<>(hitValue, timer));
    }
}
