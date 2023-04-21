package net.merchantpug.apugli.mixin.xplatform.common;

import net.merchantpug.apugli.platform.Services;
import net.merchantpug.apugli.power.CustomFootstepPower;
import net.merchantpug.apugli.registry.power.ApugliPowers;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(Entity.class)
public class EntityMixin {
    @Inject(method = "playStepSound", at = @At("HEAD"), cancellable = true)
    private void modifyStepSound(BlockPos pos, BlockState state, CallbackInfo ci) {
        if(!((Entity)(Object)this instanceof LivingEntity living) || state.getMaterial().isLiquid()) return;
        List<CustomFootstepPower> powers = Services.POWER.getPowers(living, ApugliPowers.CUSTOM_FOOTSTEP.get());
        if(powers.isEmpty()) return;
        if(powers.stream().anyMatch(CustomFootstepPower::isMuted)) ci.cancel();
        powers.forEach(power -> power.playSound(living));
        ci.cancel();
    }
}
