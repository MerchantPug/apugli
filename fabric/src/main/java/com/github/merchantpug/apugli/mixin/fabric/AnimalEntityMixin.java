package com.github.merchantpug.apugli.mixin.fabric;

import com.github.merchantpug.apugli.powers.ModifyBreedingCooldownPower;
import io.github.apace100.origins.component.OriginComponent;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(AnimalEntity.class)
public abstract class AnimalEntityMixin extends PassiveEntity {
    @Unique private AnimalEntity otherAnimalEntity;
    @Unique private ServerPlayerEntity serverPlayerEntity;

    protected AnimalEntityMixin(EntityType<? extends PassiveEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "breed", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;incrementStat(Lnet/minecraft/util/Identifier;)V"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void captureBreedLocals(ServerWorld world, AnimalEntity other, CallbackInfo ci, PassiveEntity passiveEntity, ServerPlayerEntity serverPlayerEntity) {
        this.otherAnimalEntity = other;
        this.serverPlayerEntity = serverPlayerEntity;
    }

    @ModifyArg(method = "breed", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/passive/AnimalEntity;setBreedingAge(I)V", ordinal = 0))
    private int modifyThisAnimalBreed(int age) {
        return (int)OriginComponent.modify(serverPlayerEntity, ModifyBreedingCooldownPower.class, age, p -> p.doesApply(this));
    }

    @ModifyArg(method = "breed", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/passive/AnimalEntity;setBreedingAge(I)V", ordinal = 1))
    private int modifyOtherAnimalBreed(int age) {
        return (int)OriginComponent.modify(serverPlayerEntity, ModifyBreedingCooldownPower.class, age, p -> p.doesApply(otherAnimalEntity));
    }
}
