package net.merchantpug.apugli.mixin.forge.common;

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
import net.minecraftforge.event.entity.living.BabyEntitySpawnEvent;
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
    @Unique private Animal apugli$otherAnimalEntity;
    @Unique private ServerPlayer apugli$serverPlayerEntity;

    protected AnimalEntityMixin(EntityType<? extends AgeableMob> entityType, Level world) {
        super(entityType, world);
    }
    @Inject(method = "spawnChildFromBreeding", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;awardStat(Lnet/minecraft/resources/ResourceLocation;)V"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void captureBreedLocals(ServerLevel pLevel, Animal pMate, CallbackInfo ci, AgeableMob ageablemob, BabyEntitySpawnEvent event, boolean cancelled, ServerPlayer serverplayer) {
        this.apugli$otherAnimalEntity = pMate;
        this.apugli$serverPlayerEntity = serverplayer;
    }

    @ModifyArg(method = "spawnChildFromBreeding", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/Animal;setAge(I)V", ordinal = 0))
    private int modifyThisAnimalBreed(int age) {
        return (int)Services.PLATFORM.applyModifiers(apugli$serverPlayerEntity, ApugliPowers.MODIFY_BREEDING_COOLDOWN.get(), age, p -> ApugliPowers.MODIFY_BREEDING_COOLDOWN.get().doesApply(p, apugli$serverPlayerEntity, this));
    }

    @ModifyArg(method = "spawnChildFromBreeding", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/Animal;setAge(I)V", ordinal = 1))
    private int modifyOtherAnimalBreed(int age) {
        int returnValue = (int)Services.PLATFORM.applyModifiers(apugli$serverPlayerEntity, ApugliPowers.MODIFY_BREEDING_COOLDOWN.get(), age, p -> ApugliPowers.MODIFY_BREEDING_COOLDOWN.get().doesApply(p, apugli$serverPlayerEntity, apugli$otherAnimalEntity));
        this.apugli$serverPlayerEntity = null;
        this.apugli$otherAnimalEntity = null;
        return returnValue;
    }

}
