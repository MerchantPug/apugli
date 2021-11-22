package io.github.merchantpug.apugli.mixin;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.merchantpug.apugli.power.PreventBeeAngerPower;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(BeehiveBlock.class)
public class BeehiveBlockMixin {
    @ModifyArg(method = "angerNearbyBees", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/passive/BeeEntity;setTarget(Lnet/minecraft/entity/LivingEntity;)V"))
    private LivingEntity dontAngerBees(LivingEntity entity) {
        if (PowerHolderComponent.hasPower(entity, PreventBeeAngerPower.class)) {
            return entity = null;
        }
        return entity;
    }
}
