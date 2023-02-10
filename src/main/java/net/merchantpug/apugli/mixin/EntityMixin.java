package net.merchantpug.apugli.mixin;

import net.merchantpug.apugli.access.EntityAccess;
import net.merchantpug.apugli.power.CustomFootstepPower;
import io.github.apace100.apoli.component.PowerHolderComponent;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MovementType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(Entity.class)
@Implements(@Interface(iface = EntityAccess.class, prefix = "apugli$"))
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

    @Unique
    private boolean apugli$moving;

    @Inject(method = "baseTick", at = @At(value = "HEAD", shift = At.Shift.BY, by = 2))
    private void setApugliMovingFalse(CallbackInfo ci) {
        apugli$moving = false;
    }

    @Inject(method = "move", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;setPosition(DDD)V"))
    private void setApugliMovingTrue(MovementType movementType, Vec3d movement, CallbackInfo ci) {
        if (movement != Vec3d.ZERO) {
            apugli$moving = true;
        }
    }

    public boolean apugli$isMoving() {
        return apugli$moving;
    }
}
