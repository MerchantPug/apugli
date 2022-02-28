package io.github.merchantpug.apugli.mixin;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.merchantpug.apugli.power.CustomFootstepPower;
import io.github.merchantpug.apugli.util.MobBehaviorUtil;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(Entity.class)
public class EntityMixin {
    @Inject(method = "playStepSound", at = @At("HEAD"), cancellable = true)
    private void modifyStepSound(BlockPos pos, BlockState state, CallbackInfo ci) {
        if (state.getMaterial().isLiquid()) return;
        List<CustomFootstepPower> powers = PowerHolderComponent.getPowers((Entity)(Object)this, CustomFootstepPower.class);
        if (powers.isEmpty()) return;
        if (powers.stream().anyMatch(CustomFootstepPower::isMuted)) ci.cancel();
        powers.forEach(power -> power.playFootstep((Entity)(Object)this));
        ci.cancel();
    }

    @Inject(method = "discard", at = @At("HEAD"))
    private void removeFromMobMap(CallbackInfo ci) {
        if (!((Entity)(Object)this instanceof MobEntity) || !MobBehaviorUtil.mobEntityMap.contains((MobEntity)(Object)this)) return;
        MobBehaviorUtil.mobEntityMap.remove((MobEntity)(Object)this);
    }
}
