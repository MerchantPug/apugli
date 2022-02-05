package io.github.merchantpug.apugli.mixin;

import io.github.apace100.origins.component.OriginComponent;
import io.github.merchantpug.apugli.powers.PreventBeeAngerPower;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(BeehiveBlock.class)
public class BeehiveBlockMixin {
    @ModifyArg(method = "angerNearbyBees", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/passive/BeeEntity;setTarget(Lnet/minecraft/entity/LivingEntity;)V"))
    private LivingEntity dontAngerBees(LivingEntity entity) {
        if (OriginComponent.hasPower(entity, PreventBeeAngerPower.class)) {
            return entity = null;
        }
        return entity;
    }
}