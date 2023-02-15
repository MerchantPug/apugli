package net.merchantpug.apugli.mixin;

import net.merchantpug.apugli.access.EntityAccess;
import net.merchantpug.apugli.power.CustomFootstepPower;
import io.github.apace100.apoli.component.PowerHolderComponent;
import net.merchantpug.apugli.power.HoverPower;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Mixin(Entity.class)
public abstract class EntityMixin implements EntityAccess {
    @Shadow public abstract Box getBoundingBox();

    @Shadow public World world;

    @Shadow public abstract void setPosition(double x, double y, double z);

    @Shadow public abstract double getX();

    @Shadow public abstract double getZ();

    @Shadow public abstract double getY();

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

    @Inject(method = "move", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiler/Profiler;pop()V", ordinal = 0))
    private void handleHoverCorrection(MovementType movementType, Vec3d movement, CallbackInfo ci) {
        if ((Entity)(Object)this instanceof LivingEntity thisAsLiving) {
            Optional<Float> correctionHeightOptional = PowerHolderComponent.getPowers(thisAsLiving, HoverPower.class).stream().filter(HoverPower::canCorrectHeight).map(HoverPower::getCorrectionRange).max(Float::compare);
            if (correctionHeightOptional.isPresent()) {
                float correctionHeight = correctionHeightOptional.get();
                Box box = this.getBoundingBox().stretch(movement);
                List<VoxelShape> collisionList = new ArrayList<>();
                world.getBlockCollisions(null, box).forEach(collisionList::add);
                Vec3d vec3d4 = movement.lengthSquared() == 0.0 ? movement : Entity.adjustMovementForCollisions(thisAsLiving, movement, box, this.world, collisionList);
                if (correctionHeight > 0.0F && (movement.x != vec3d4.x || movement.z != vec3d4.z)) {
                    Vec3d vec3d = Entity.adjustMovementForCollisions(thisAsLiving, new Vec3d(movement.x, correctionHeight, movement.z), box, this.world, collisionList);
                    Vec3d vec3d2 = Entity.adjustMovementForCollisions(thisAsLiving, new Vec3d(0.0, correctionHeight, 0.0), box.stretch(movement.x, 0.0, movement.z), this.world, collisionList);
                    Vec3d vec3d3 = Entity.adjustMovementForCollisions(thisAsLiving, new Vec3d(movement.x, 0.0, movement.z), box, this.world, collisionList).add(vec3d2);
                    if (vec3d3.horizontalLengthSquared() > vec3d.horizontalLengthSquared()) {
                        vec3d = vec3d3;
                    }

                    Vec3d vec3d5 = Entity.adjustMovementForCollisions(thisAsLiving, new Vec3d(movement.x, -correctionHeight, movement.z), box, this.world, collisionList);
                    Vec3d vec3d6 = Entity.adjustMovementForCollisions(thisAsLiving, new Vec3d(0.0, -correctionHeight, 0.0), box.stretch(movement.x, 0.0, movement.z), this.world, collisionList);
                    Vec3d vec3d7 = Entity.adjustMovementForCollisions(thisAsLiving, new Vec3d(movement.x, 0.0, movement.z), box, this.world, collisionList).add(vec3d6);

                    if (vec3d7.horizontalLengthSquared() > vec3d5.horizontalLengthSquared()) {
                        vec3d = vec3d3;
                    }

                    if (vec3d.horizontalLengthSquared() > vec3d4.horizontalLengthSquared()) {
                        this.setPosition(this.getX(), this.getY() + Entity.adjustMovementForCollisions(thisAsLiving, new Vec3d(0.0, vec3d.y + Entity.adjustMovementForCollisions(thisAsLiving, new Vec3d(0.0, -vec3d.y + movement.y, 0.0), box.offset(vec3d), this.world, collisionList).y, 0.0), box, this.world, collisionList).getY(), this.getZ());
                    } else if (vec3d5.horizontalLengthSquared() > vec3d4.horizontalLengthSquared()) {
                        this.setPosition(this.getX(), this.getY() + Entity.adjustMovementForCollisions(thisAsLiving, new Vec3d(0.0, vec3d5.y + Entity.adjustMovementForCollisions(thisAsLiving, new Vec3d(0.0, -vec3d5.y + movement.y, 0.0), box.offset(vec3d5), this.world, collisionList).y, 0.0), box, this.world, collisionList).getY(), this.getZ());
                    }
                }
            }
        }
    }

    public boolean apugli$isMoving() {
        return apugli$moving;
    }
}
