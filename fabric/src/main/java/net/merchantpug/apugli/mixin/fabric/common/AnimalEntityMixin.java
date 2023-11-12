package net.merchantpug.apugli.mixin.fabric.common;

import net.merchantpug.apugli.platform.Services;
import net.merchantpug.apugli.power.PreventBreedingPower;
import net.merchantpug.apugli.registry.power.ApugliPowers;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;
import java.util.stream.Collectors;

@Mixin(Animal.class)
public abstract class AnimalEntityMixin extends AgeableMob {
    @Shadow public abstract boolean isFood(ItemStack stack);

    @Shadow public abstract boolean canFallInLove();

    @Shadow private int inLove;

    @Shadow public abstract @Nullable ServerPlayer getLoveCause();

    @Unique private Animal apugli$otherAnimalEntity;

    protected AnimalEntityMixin(EntityType<? extends AgeableMob> entityType, Level world) {
        super(entityType, world);
    }

    @Inject(method = "mobInteract", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;getItemInHand(Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/item/ItemStack;", shift = At.Shift.BY, by = 2), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    private void apugli$preventMobBreeding(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir, ItemStack itemStack) {
        List<PreventBreedingPower> preventBreedingPowerList = Services.POWER.getPowers(player, ApugliPowers.PREVENT_BREEDING.get()).stream().filter(power -> power.doesApply(this)).collect(Collectors.toList());
        if(!preventBreedingPowerList.isEmpty() && this.isFood(itemStack)) {
            int i = this.getAge();
            if(i == 0 && this.canFallInLove()) {
                if(preventBreedingPowerList.stream().anyMatch(PreventBreedingPower::hasAction)) {
                    preventBreedingPowerList.forEach(power -> power.executeAction(this));
                    this.inLove = (int)Services.PLATFORM.applyModifiers(player, ApugliPowers.MODIFY_BREEDING_COOLDOWN.get(), 6000);
                    cir.setReturnValue(InteractionResult.SUCCESS);
                } else {
                    cir.setReturnValue(InteractionResult.FAIL);
                }
            }
        }
    }

    @Inject(method = "finalizeSpawnChildFromBreeding", at = @At(value = "HEAD"))
    private void apugli$captureBreedLocals(ServerLevel serverLevel, Animal animal, AgeableMob ageableMob, CallbackInfo ci) {
        this.apugli$otherAnimalEntity = animal;
    }

    @ModifyArg(method = "finalizeSpawnChildFromBreeding", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/Animal;setAge(I)V", ordinal = 0))
    private int apugli$modifyThisAnimalBreed(int age) {
        if (this.getLoveCause() != null) {
            return (int)Services.PLATFORM.applyModifiers(this.getLoveCause(), ApugliPowers.MODIFY_BREEDING_COOLDOWN.get(), age, p -> ApugliPowers.MODIFY_BREEDING_COOLDOWN.get().doesApply(p, this.getLoveCause(), this));
        }
        return age;
    }

    @ModifyArg(method = "finalizeSpawnChildFromBreeding", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/Animal;setAge(I)V", ordinal = 1))
    private int apugli$modifyOtherAnimalBreed(int age) {
        if (this.getLoveCause() != null) {
            int returnValue = (int) Services.PLATFORM.applyModifiers(this.getLoveCause(), ApugliPowers.MODIFY_BREEDING_COOLDOWN.get(), age, p -> ApugliPowers.MODIFY_BREEDING_COOLDOWN.get().doesApply(p, this.getLoveCause(), apugli$otherAnimalEntity));
            this.apugli$otherAnimalEntity = null;
            return returnValue;
        }
        this.apugli$otherAnimalEntity = null;
        return age;
    }

}
