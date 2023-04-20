package net.merchantpug.apugli.mixin.xplatform.common;

import io.github.apace100.apoli.component.PowerHolderComponent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import the.great.migration.merchantpug.apugli.power.CustomFootstepPower;

import java.util.List;

@Mixin(Entity.class)
public class EntityMixin {
    @Inject(method = "playStepSound", at = @At("HEAD"), cancellable = true)
    private void modifyStepSound(BlockPos pos, BlockState state, CallbackInfo ci) {
        if(state.getMaterial().isLiquid()) return;
        List<CustomFootstepPower> powers = PowerHolderComponent.getPowers((Entity)(Object)this, CustomFootstepPower.class);
        if(powers.isEmpty()) return;
        if(powers.stream().anyMatch(CustomFootstepPower::isMuted)) ci.cancel();
        powers.forEach(power -> power.playFootstep((Entity)(Object)this));
        ci.cancel();
    }
}
