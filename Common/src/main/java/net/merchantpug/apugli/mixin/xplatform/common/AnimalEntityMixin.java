package net.merchantpug.apugli.mixin.xplatform.common;

import net.merchantpug.apugli.platform.Services;
import net.merchantpug.apugli.power.PreventBreedingPower;
import net.merchantpug.apugli.power.factory.SpecialPowerFactory;
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
    @Unique private Animal apugli$otherAnimalEntity;
    @Unique private ServerPlayer apugli$serverPlayerEntity;

    protected AnimalEntityMixin(EntityType<? extends AgeableMob> entityType, Level world) {
        super(entityType, world);
    }

    @Inject(method = "mobInteract", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;getItemInHand(Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/item/ItemStack;", shift = At.Shift.BY, by = 2), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    private void preventMobBreeding(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir, ItemStack itemStack) {
        List<PreventBreedingPower> preventBreedingPowerList = Services.POWER.getPowers(player, ApugliPowers.PREVENT_BREEDING.get()).stream().filter(power -> power.doesApply(this)).collect(Collectors.toList());
        if(preventBreedingPowerList.isEmpty()) return;
        if(this.isFood(itemStack)) {
            int i = this.getAge();
            if(i == 0 && this.canFallInLove()) {
                if(preventBreedingPowerList.stream().anyMatch(PreventBreedingPower::hasAction)) {
                    preventBreedingPowerList.forEach(power -> power.executeAction(this));
                    this.inLove = (int)Services.PLATFORM.applyModifiers(player, ApugliPowers.MODIFY_BREEDING_COOLDOWN.get(), 600);
                    cir.setReturnValue(InteractionResult.SUCCESS);
                } else {
                    cir.setReturnValue(InteractionResult.FAIL);
                }
            }
        }
    }

    @Inject(method = "spawnChildFromBreeding", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;awardStat(Lnet/minecraft/resources/ResourceLocation;)V"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void captureBreedLocals(ServerLevel world, Animal other, CallbackInfo ci, AgeableMob passiveEntity, ServerPlayer serverPlayerEntity) {
        this.apugli$otherAnimalEntity = other;
        this.apugli$serverPlayerEntity = serverPlayerEntity;
    }

    @ModifyArg(method = "spawnChildFromBreeding", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/Animal;setAge(I)V", ordinal = 0))
    private int modifyThisAnimalBreed(int age) {
        return (int)Services.PLATFORM.applyModifiers(apugli$serverPlayerEntity, ApugliPowers.MODIFY_BREEDING_COOLDOWN.get(), age, p -> ApugliPowers.MODIFY_BREEDING_COOLDOWN.get().doesApply(p, apugli$serverPlayerEntity, this));
    }

    @ModifyArg(method = "spawnChildFromBreeding", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/Animal;setAge(I)V", ordinal = 1))
    private int modifyOtherAnimalBreed(int age) {
        return (int)Services.PLATFORM.applyModifiers(apugli$serverPlayerEntity, ApugliPowers.MODIFY_BREEDING_COOLDOWN.get(), age, p -> ApugliPowers.MODIFY_BREEDING_COOLDOWN.get().doesApply(p, apugli$serverPlayerEntity, apugli$otherAnimalEntity));
    }

}
