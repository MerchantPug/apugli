package net.merchantpug.apugli.mixin.fabric.common;

import com.mojang.datafixers.util.Pair;
import net.merchantpug.apugli.component.ApugliEntityComponents;
import net.merchantpug.apugli.component.HitsOnTargetComponent;
import net.merchantpug.apugli.network.ApugliPackets;
import net.merchantpug.apugli.network.s2c.SyncHitsOnTargetLessenedPacket;
import net.merchantpug.apugli.platform.Services;
import net.merchantpug.apugli.power.ActionOnJumpPower;
import net.merchantpug.apugli.power.EdibleItemPower;
import net.merchantpug.apugli.registry.power.ApugliPowers;
import net.merchantpug.apugli.util.IndividualisedEmptyStackUtil;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Tuple;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Optional;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

    @Shadow public abstract SoundEvent getEatingSound(ItemStack itemStack);

    @Shadow public abstract boolean addEffect(MobEffectInstance mobEffectInstance);

    @Shadow @Nullable public abstract LivingEntity getKillCredit();

    @Shadow public abstract boolean isDeadOrDying();

    @Shadow public abstract MobType getMobType();

    @Shadow public abstract void push(Entity pEntity);

    @Shadow public abstract ItemStack getItemBySlot(EquipmentSlot slot);

    public LivingEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "jumpFromGround", at = @At("TAIL"))
    private void handleGroundJump(CallbackInfo ci) {
        Services.POWER.getPowers((LivingEntity)(Object)this, ApugliPowers.ACTION_ON_JUMP.get()).forEach(ActionOnJumpPower::executeAction);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void setItemStackEntities(CallbackInfo ci) {
        IndividualisedEmptyStackUtil.addEntityToStack((LivingEntity)(Object)this);
    }

    @Inject(method = "hurt", at = @At("RETURN"))
    private void runDamageFunctions(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue()) return;

        LivingEntity thisAsLiving = (LivingEntity)(Object)this;

        if (thisAsLiving.getLastHurtByMob() != null) {
            ApugliPowers.ACTION_ON_ATTACKER_HURT.get().execute(thisAsLiving, source, amount);
            ApugliPowers.ACTION_ON_TARGET_HURT.get().execute(thisAsLiving, source, amount);
        }

        if (source.getEntity() == null) return;

        if (thisAsLiving instanceof TamableAnimal tamable) {
            ApugliPowers.ACTION_WHEN_TAME_HIT.get().execute(tamable, source, amount);
        }

        if (source.getEntity() instanceof TamableAnimal tamable) {
            ApugliPowers.ACTION_ON_TAME_HIT.get().execute(tamable, source, amount, thisAsLiving);
        }

        HitsOnTargetComponent hitsComponent = ApugliEntityComponents.HITS_ON_TARGET_COMPONENT.get(thisAsLiving);
        hitsComponent.setHits(source.getEntity().getId(), hitsComponent.getHits().getOrDefault(source.getEntity().getId(), new Tuple<>(0, 0)).getA() + 1, 0);
        if (!(thisAsLiving instanceof ServerPlayer serverPlayer)) return;
        ApugliPackets.sendS2CTrackingAndSelf(new SyncHitsOnTargetLessenedPacket(source.getEntity().getId(), hitsComponent.getPreviousHits(), hitsComponent.getHits()), serverPlayer);
    }

    @Unique
    private float apugli$damageAmountOnDeath;

    @Inject(method = "hurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;die(Lnet/minecraft/world/damagesource/DamageSource;)V"))
    private void captureDamageAmount(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        apugli$damageAmountOnDeath = amount;
    }

    @Inject(method = "die", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/damagesource/DamageSource;getEntity()Lnet/minecraft/world/entity/Entity;"))
    private void runActionsOnTargetDeath(DamageSource source, CallbackInfo ci) {
        if (this.getLevel().isClientSide) return;

        if (source.getEntity() != null && !source.getEntity().equals(this.getKillCredit()) && this.getKillCredit() != null) {
            ApugliPowers.ACTION_ON_TARGET_DEATH.get().onTargetDeath(this.getKillCredit(), (LivingEntity) (Object) this, source, apugli$damageAmountOnDeath, true);
            return;
        }

        if (!(source.getEntity() instanceof LivingEntity living)) return;
        ApugliPowers.ACTION_ON_TARGET_DEATH.get().onTargetDeath(living, (LivingEntity) (Object) this, source, apugli$damageAmountOnDeath, false);
    }

    @Unique
    private boolean apugli$hasModifiedDamage;

    @ModifyVariable(method = "hurt", at = @At("HEAD"), argsOnly = true)
    private float modifyDamageBasedOnEnchantment(float originalValue, DamageSource source, float amount) {
        float additionalValue = 0.0F;
        LivingEntity thisAsLiving = (LivingEntity) (Object) this;

        if (source.getEntity() instanceof LivingEntity attacker && source.getDirectEntity() == attacker) {
            additionalValue += ApugliPowers.MODIFY_ENCHANTMENT_DAMAGE_DEALT.get().applyModifiers(attacker, source, amount, thisAsLiving);
        }

        if (source.getEntity() instanceof LivingEntity attacker) {
            additionalValue += ApugliPowers.MODIFY_ENCHANTMENT_DAMAGE_TAKEN.get().applyModifiers(thisAsLiving, source, attacker, amount);
        }

        apugli$hasModifiedDamage = originalValue + additionalValue != originalValue;

        if (additionalValue > 0.0F && source.getEntity() instanceof Player attacker) {
            float enchantmentDamageBonus = EnchantmentHelper.getDamageBonus(attacker.getMainHandItem(), this.getMobType());
            if (enchantmentDamageBonus <= 0.0F && !this.getLevel().isClientSide) {
                attacker.magicCrit(this);
            }
        }

        return originalValue + additionalValue;
    }

    @Inject(method = "hurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;isSleeping()Z"), cancellable = true)
    private void preventHitIfDamageIsZero(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (apugli$hasModifiedDamage && amount == 0.0F) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "hurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;actuallyHurt(Lnet/minecraft/world/damagesource/DamageSource;F)V"))
    private void runDamageFunctionsBeforeDamaged(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (amount == 0.0F) return;
        if ((source.getEntity() instanceof LivingEntity living)) {
            Services.POWER.getPowers(living, ApugliPowers.ACTION_ON_HARM.get()).forEach(p -> ApugliPowers.ACTION_ON_HARM.get().execute(p, living, source, amount, (LivingEntity)(Object)this));
            Services.POWER.getPowers(living, ApugliPowers.DAMAGE_NEARBY_ON_HIT.get()).forEach(p -> ApugliPowers.DAMAGE_NEARBY_ON_HIT.get().execute(p, living, amount, (LivingEntity)(Object)this));
        }

        Services.POWER.getPowers((LivingEntity)(Object)this, ApugliPowers.ACTION_WHEN_HARMED.get()).forEach(p -> ApugliPowers.ACTION_WHEN_HARMED.get().execute(p, (LivingEntity)(Object)this, source, amount));
        Services.POWER.getPowers((LivingEntity)(Object)this, ApugliPowers.DAMAGE_NEARBY_WHEN_HIT.get()).forEach(p -> ApugliPowers.DAMAGE_NEARBY_WHEN_HIT.get().execute(p, (LivingEntity)(Object)this, source, amount));
    }

    @Inject(method = "eat", at = @At("HEAD"))
    private void eatStackFood(Level world, ItemStack stack, CallbackInfoReturnable<ItemStack> cir) {
        if (Services.PLATFORM.getEntityFromItemStack(stack) instanceof LivingEntity living) {
            Optional<EdibleItemPower> power = Services.POWER.getPowers(living, ApugliPowers.EDIBLE_ITEM.get()).stream().filter(p -> p.doesApply(world, stack)).findFirst();
            power.ifPresent(p -> {
                world.playSound(null, this.getX(), this.getY(), this.getZ(), this.getEatingSound(stack), SoundSource.NEUTRAL, 1.0f, 1.0f + (world.random.nextFloat() - world.random.nextFloat()) * 0.4f);
                List<Pair<MobEffectInstance, Float>> list = p.getFoodComponent().getEffects();
                for (com.mojang.datafixers.util.Pair<MobEffectInstance, Float> pair : list) {
                    if (world.isClientSide || pair.getFirst() == null || !(world.random.nextFloat() < pair.getSecond()))
                        continue;
                    this.addEffect(new MobEffectInstance(pair.getFirst()));
                }
            });
        }
    }

    @Inject(method = "baseTick", at = @At("HEAD"))
    private void tick(CallbackInfo ci) {
        if (!this.isAlive()) return;
        if (Services.POWER.hasPower((LivingEntity) (Object) this, ApugliPowers.HOVER.get())) {
            this.setDeltaMovement(this.getDeltaMovement().multiply(1.0, 0.0, 1.0));
            this.fallDistance = 0.0F;
        }
    }

    @Inject(method = "travel", at = @At("HEAD"))
    private void travel(Vec3 movementInput, CallbackInfo ci) {
        if (this.isDeadOrDying() || this.getLevel().isClientSide) return;
        ApugliPowers.BUNNY_HOP.get().onTravel((LivingEntity)(Object)this, movementInput);
    }

}
