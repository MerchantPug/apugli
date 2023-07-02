package net.merchantpug.apugli.mixin.xplatform.common;

import net.merchantpug.apugli.access.EntityAccess;
import net.merchantpug.apugli.platform.Services;
import net.merchantpug.apugli.power.CustomFootstepPower;
import net.merchantpug.apugli.power.HoverPower;
import net.merchantpug.apugli.power.StepHeightPower;
import net.merchantpug.apugli.registry.power.ApugliPowers;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(Entity.class)
public abstract class EntityMixin implements EntityAccess {
    @Shadow public abstract AABB getBoundingBox();

    @Shadow public Level level;

    @Shadow public abstract void setPos(double x, double y, double z);

    @Shadow public abstract double getX();

    @Shadow public abstract double getY();

    @Shadow public abstract double getZ();

    @Inject(method = "playStepSound", at = @At("HEAD"), cancellable = true)
    private void modifyStepSound(BlockPos pos, BlockState state, CallbackInfo ci) {
        if(!((Entity)(Object)this instanceof LivingEntity living) || state.getMaterial().isLiquid()) return;
        List<CustomFootstepPower> powers = Services.POWER.getPowers(living, ApugliPowers.CUSTOM_FOOTSTEP.get());
        if(powers.isEmpty()) return;
        if(powers.stream().anyMatch(CustomFootstepPower::isMuted)) ci.cancel();
        powers.forEach(power -> power.playSound(living));
        ci.cancel();
    }


    @Unique
    private boolean apugli$moving;

    @Inject(method = "baseTick", at = @At(value = "HEAD", shift = At.Shift.BY, by = 2))
    private void setApugliMovingFalse(CallbackInfo ci) {
        apugli$moving = false;
    }

    @Inject(method = "move", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;setPos(DDD)V"))
    private void setApugliMovingTrue(MoverType movementType, Vec3 movement, CallbackInfo ci) {
        if (movement != Vec3.ZERO) {
            apugli$moving = true;
        }
    }

    public boolean apugli$isMoving() {
        return apugli$moving;
    }

    @Inject(method = "move", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;maybeBackOffFromEdge(Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/entity/MoverType;)Lnet/minecraft/world/phys/Vec3;"))
    private void handleHoverCorrection(MoverType movementType, Vec3 movement, CallbackInfo ci) {
        if ((Entity)(Object)this instanceof LivingEntity thisAsLiving) {
            float lowerCorrectionRange = StepHeightPower.getLowerCorrectionRange(thisAsLiving)
                    .orElse(HoverPower.getCorrectionRange(thisAsLiving).orElse(0.0F));
            float upperCorrectionRange = StepHeightPower.getUpperCorrectionRange(thisAsLiving)
                    .orElse(HoverPower.getCorrectionRange(thisAsLiving).orElse(0.0F));
            if (lowerCorrectionRange > 0.0F || upperCorrectionRange > 0.0F) {
                AABB box = this.getBoundingBox().expandTowards(movement);
                List<VoxelShape> collisionList = new ArrayList<>();
                this.level.getBlockCollisions(null, box).forEach(collisionList::add);
                Vec3 vec3d4 = movement.lengthSqr() == 0.0 ? movement : Entity.collideBoundingBox(thisAsLiving, movement, box, this.level, collisionList);
                if (movement.x != vec3d4.x || movement.z != vec3d4.z) {
                    if (lowerCorrectionRange > 0.0F) {
                        Vec3 vec3d = Entity.collideBoundingBox(thisAsLiving, new Vec3(movement.x, lowerCorrectionRange, movement.z), box, this.level, collisionList);
                        Vec3 vec3d2 = Entity.collideBoundingBox(thisAsLiving, new Vec3(0.0, lowerCorrectionRange, 0.0), box.expandTowards(movement.x, 0.0, movement.z), this.level, collisionList);
                        Vec3 vec3d3 = Entity.collideBoundingBox(thisAsLiving, new Vec3(movement.x, 0.0, movement.z), box, this.level, collisionList).add(vec3d2);
                        if (vec3d3.horizontalDistanceSqr() > vec3d.horizontalDistanceSqr()) {
                            vec3d = vec3d3;
                        }

                        if (vec3d.horizontalDistanceSqr() > vec3d4.horizontalDistanceSqr()) {
                            this.setPos(this.getX(), this.getY() + Entity.collideBoundingBox(thisAsLiving, new Vec3(0.0, vec3d.y + Entity.collideBoundingBox(thisAsLiving, new Vec3(0.0, -vec3d.y + movement.y, 0.0), box.move(vec3d), this.level, collisionList).y, 0.0), box, this.level, collisionList).y(), this.getZ());
                            return;
                        }
                    }

                    Vec3 vec3d5 = Entity.collideBoundingBox(thisAsLiving, new Vec3(movement.x, -upperCorrectionRange, movement.z), box, this.level, collisionList);
                    Vec3 vec3d6 = Entity.collideBoundingBox(thisAsLiving, new Vec3(0.0, -upperCorrectionRange, 0.0), box.expandTowards(movement.x, 0.0, movement.z), this.level, collisionList);
                    Vec3 vec3d7 = Entity.collideBoundingBox(thisAsLiving, new Vec3(movement.x, 0.0, movement.z), box, this.level, collisionList).add(vec3d6);

                    if (vec3d7.horizontalDistanceSqr() > vec3d5.horizontalDistanceSqr()) {
                        vec3d5 = vec3d7;
                    }

                    if (vec3d5.horizontalDistanceSqr() > vec3d4.horizontalDistanceSqr()) {
                        this.setPos(this.getX(), this.getY() + Entity.collideBoundingBox(thisAsLiving, new Vec3(0.0, vec3d5.y + Entity.collideBoundingBox(thisAsLiving, new Vec3(0.0, -vec3d5.y + movement.y, 0.0), box.move(vec3d5), this.level, collisionList).y, 0.0), box, this.level, collisionList).y(), this.getZ());
                    }
                }
            }
        }
    }

}
