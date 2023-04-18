<<<<<<<< HEAD:src/main/java/net/merchantpug/apugli/mixin/AnimalEntityMixin.java
package net.merchantpug.apugli.mixin;

import net.merchantpug.apugli.power.ModifyBreedingCooldownPower;
import net.merchantpug.apugli.power.PreventBreedingPower;
========
package com.github.merchantpug.apugli.mixin.xplatforn.common;

>>>>>>>> pr/25:Common/src/main/java/com/github/merchantpug/apugli/mixin/xplatforn/common/AnimalEntityMixin.java
import io.github.apace100.apoli.component.PowerHolderComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import the.great.migration.merchantpug.apugli.power.ModifyBreedingCooldownPower;
import the.great.migration.merchantpug.apugli.power.PreventBreedingPower;

import java.util.List;
import java.util.stream.Collectors;

@Mixin(Animal.class)
public abstract class AnimalEntityMixin extends AgeableMob {
    @Shadow public abstract boolean isBreedingItem(ItemStack stack);

    @Shadow public abstract boolean canEat();

    @Shadow private int loveTicks;
<<<<<<<< HEAD:src/main/java/net/merchantpug/apugli/mixin/AnimalEntityMixin.java
    @Unique private AnimalEntity apugli$otherAnimalEntity;
    @Unique private ServerPlayerEntity apugli$serverPlayerEntity;
========
    @Unique private Animal otherAnimalEntity;
    @Unique private ServerPlayer serverPlayerEntity;
>>>>>>>> pr/25:Common/src/main/java/com/github/merchantpug/apugli/mixin/xplatforn/common/AnimalEntityMixin.java

    protected AnimalEntityMixin(EntityType<? extends AgeableMob> entityType, Level world) {
        super(entityType, world);
    }

    @Inject(method = "interactMob", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;getStackInHand(Lnet/minecraft/util/Hand;)Lnet/minecraft/item/ItemStack;", shift = At.Shift.BY, by = 2), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    private void preventMobBreeding(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir, ItemStack itemStack) {
        List<PreventBreedingPower> preventBreedingPowerList = PowerHolderComponent.getPowers(player, PreventBreedingPower.class).stream().filter(power -> power.doesApply(this)).collect(Collectors.toList());
        if(preventBreedingPowerList.isEmpty()) return;
        if(this.isBreedingItem(itemStack)) {
            int i = this.getAge();
            if(i == 0 && this.canEat()) {
                if(preventBreedingPowerList.stream().anyMatch(PreventBreedingPower::hasAction)) {
                    preventBreedingPowerList.forEach(power -> power.executeAction(this));
                    this.loveTicks = (int)PowerHolderComponent.modify(player, ModifyBreedingCooldownPower.class, 600);
                    cir.setReturnValue(InteractionResult.SUCCESS);
                } else {
                    cir.setReturnValue(InteractionResult.FAIL);
                }
            }
        }
    }

    @Inject(method = "breed", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;incrementStat(Lnet/minecraft/util/Identifier;)V"), locals = LocalCapture.CAPTURE_FAILHARD)
<<<<<<<< HEAD:src/main/java/net/merchantpug/apugli/mixin/AnimalEntityMixin.java
    private void captureBreedLocals(ServerWorld world, AnimalEntity other, CallbackInfo ci, PassiveEntity passiveEntity, ServerPlayerEntity serverPlayerEntity) {
        this.apugli$otherAnimalEntity = other;
        this.apugli$serverPlayerEntity = serverPlayerEntity;
========
    private void captureBreedLocals(ServerLevel world, Animal other, CallbackInfo ci, AgeableMob passiveEntity, ServerPlayer serverPlayerEntity) {
        this.otherAnimalEntity = other;
        this.serverPlayerEntity = serverPlayerEntity;
>>>>>>>> pr/25:Common/src/main/java/com/github/merchantpug/apugli/mixin/xplatforn/common/AnimalEntityMixin.java
    }

    @ModifyArg(method = "breed", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/passive/AnimalEntity;setBreedingAge(I)V", ordinal = 0))
    private int modifyThisAnimalBreed(int age) {
        return (int)PowerHolderComponent.modify(apugli$serverPlayerEntity, ModifyBreedingCooldownPower.class, age, p -> p.doesApply((Entity)(Object)this));
    }

    @ModifyArg(method = "breed", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/passive/AnimalEntity;setBreedingAge(I)V", ordinal = 1))
    private int modifyOtherAnimalBreed(int age) {
        return (int)PowerHolderComponent.modify(apugli$serverPlayerEntity, ModifyBreedingCooldownPower.class, age, p -> p.doesApply(apugli$otherAnimalEntity));
    }
}
