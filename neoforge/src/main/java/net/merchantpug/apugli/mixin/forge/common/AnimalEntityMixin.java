package net.merchantpug.apugli.mixin.forge.common;

import net.merchantpug.apugli.platform.Services;
import net.merchantpug.apugli.registry.power.ApugliPowers;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Animal.class)
public abstract class AnimalEntityMixin extends AgeableMob {
    @Shadow @Nullable public abstract ServerPlayer getLoveCause();

    @Unique private Animal apugli$otherAnimalEntity;

    protected AnimalEntityMixin(EntityType<? extends AgeableMob> entityType, Level world) {
        super(entityType, world);
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
