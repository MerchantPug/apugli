package net.merchantpug.apugli.mixin.xplatform.common;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.merchantpug.apugli.access.EntityAccess;
import net.merchantpug.apugli.mixin.xplatform.common.accessor.LivingEntityAccessor;
import net.merchantpug.apugli.platform.Services;
import net.merchantpug.apugli.power.CustomFootstepPower;
import net.merchantpug.apugli.power.HoverPower;
import net.merchantpug.apugli.power.StepHeightPower;
import net.merchantpug.apugli.registry.power.ApugliPowers;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(Entity.class)
public abstract class EntityMixin implements EntityAccess {
    @Shadow public abstract AABB getBoundingBox();

    @Shadow
    private Level level;

    @Shadow public abstract double getX();

    @Shadow public abstract double getY();

    @Shadow public abstract double getZ();

    @Shadow public abstract int getTicksFrozen();

    @Inject(method = "playStepSound", at = @At("HEAD"), cancellable = true)
    private void apugli$modifyStepSound(BlockPos pos, BlockState state, CallbackInfo ci) {
        if(!((Entity)(Object)this instanceof LivingEntity living)) return;
        List<CustomFootstepPower> powers = Services.POWER.getPowers(living, ApugliPowers.CUSTOM_FOOTSTEP.get());
        if(powers.isEmpty()) return;
        if(powers.stream().anyMatch(CustomFootstepPower::isMuted)) ci.cancel();
        powers.forEach(power -> power.playSound(living));
        ci.cancel();
    }


    @Unique
    private boolean apugli$moving;

    @Inject(method = "baseTick", at = @At(value = "HEAD", shift = At.Shift.BY, by = 2))
    private void apugli$setApugliMovingFalse(CallbackInfo ci) {
        apugli$moving = false;
    }

    @Inject(method = "move", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;setPos(DDD)V"))
    private void apugli$setApugliMovingTrue(MoverType movementType, Vec3 movement, CallbackInfo ci) {
        if (movement != Vec3.ZERO) {
            apugli$moving = true;
        }
    }

    public boolean apugli$isMoving() {
        return apugli$moving;
    }

    @Inject(method = "collide", at = @At(value = "HEAD"), cancellable = true)
    private void apugli$handleHoverCorrection(Vec3 movement, CallbackInfoReturnable<Vec3> cir) {
        if ((Entity)(Object)this instanceof LivingEntity thisAsLiving) {
            double lowerCorrectionRange = StepHeightPower.getLowerCorrectionRange(thisAsLiving)
                    .orElse(HoverPower.getCorrectionRange(thisAsLiving).orElse(0.0));
            double upperCorrectionRange = StepHeightPower.getUpperCorrectionRange(thisAsLiving)
                    .orElse(HoverPower.getCorrectionRange(thisAsLiving).orElse(0.0));
            if (lowerCorrectionRange > 0.0F || upperCorrectionRange > 0.0F) {
                AABB box = this.getBoundingBox();
                List<VoxelShape> collisionList = this.level.getEntityCollisions(thisAsLiving, box.expandTowards(movement));
                Vec3 vec3d4 = movement.lengthSqr() == 0.0 ? movement : Entity.collideBoundingBox(thisAsLiving, movement, box, this.level, collisionList);
                if ((movement.x != vec3d4.x || movement.z != vec3d4.z)) {
                    boolean handledLowerRange = false;
                    double correction = 0.0;
                    while (correction <= lowerCorrectionRange) {
                        Vec3 vec3d = Entity.collideBoundingBox(thisAsLiving, new Vec3(movement.x, correction, movement.z), box, this.level, collisionList);
                        Vec3 vec3d2 = Entity.collideBoundingBox(thisAsLiving, new Vec3(0.0, correction, 0.0), box.expandTowards(movement.x, 0.0, movement.z), this.level, collisionList);
                        Vec3 vec3d3 = Entity.collideBoundingBox(thisAsLiving, new Vec3(movement.x, 0.0, movement.z), box, this.level, collisionList).add(vec3d2);

                        if (vec3d3.horizontalDistanceSqr() > vec3d.horizontalDistanceSqr()) {
                            vec3d = vec3d3;
                        }

                        if (vec3d.horizontalDistanceSqr() > vec3d4.horizontalDistanceSqr()) {
                            cir.setReturnValue(vec3d.add(Entity.collideBoundingBox(thisAsLiving, new Vec3(0.0, -vec3d.y() + Mth.abs((float) movement.y()), 0.0), box.move(vec3d), this.level, collisionList)));
                            if (Services.POWER.getPowers(thisAsLiving, ApugliPowers.STEP_HEIGHT.get()).stream().anyMatch(power -> power.canCorrectLowerHeight() && power.shouldAllowJumpAfter())) {
                                ((LivingEntityAccessor) thisAsLiving).apugli$setNoJumpDelay(0);
                            }
                            handledLowerRange = true;
                            break;
                        }
                        double checkAddition = lowerCorrectionRange % 1.0;
                        if (checkAddition == 0.0) {
                            checkAddition = 1.0;
                        }
                        correction += checkAddition;
                    }

                    if (handledLowerRange) return;

                    correction = 0.0;
                    while (correction <= upperCorrectionRange) {
                        Vec3 vec3d = Entity.collideBoundingBox(thisAsLiving, new Vec3(movement.x, -correction, movement.z), box, this.level, collisionList);
                        Vec3 vec3d2 = Entity.collideBoundingBox(thisAsLiving, new Vec3(0.0, -correction, 0.0), box.expandTowards(movement.x, 0.0, movement.z), this.level, collisionList);
                        Vec3 vec3d3 = Entity.collideBoundingBox(thisAsLiving, new Vec3(movement.x, 0.0, movement.z), box, this.level, collisionList).add(vec3d2);

                        if (vec3d3.horizontalDistanceSqr() > vec3d.horizontalDistanceSqr()) {
                            vec3d = vec3d3;
                        }

                        if (vec3d.horizontalDistanceSqr() > vec3d4.horizontalDistanceSqr()) {
                            cir.setReturnValue(vec3d.add(Entity.collideBoundingBox(thisAsLiving, new Vec3(0.0, -vec3d.y() + Mth.abs((float) movement.y()), 0.0), box.move(vec3d), this.level, collisionList)));
                            break;
                        }
                        double checkAddition = lowerCorrectionRange % 1.0;
                        if (checkAddition == 0.0) {
                            checkAddition = 1.0;
                        }
                        correction += checkAddition;
                    }
                }
            }
        }
    }

    @Unique
    private int apugli$modifiedFreezeTicks;

    @ModifyReturnValue(method = "getTicksRequiredToFreeze", at = @At("RETURN"))
    private int apugli$modifyTicksRequiredToFreeze(int original) {
        if ((Entity)(Object)this instanceof LivingEntity living) {
            if (Services.POWER.hasPower(living, ApugliPowers.FREEZE.get())) {
                int newValue = (int) Services.PLATFORM.applyModifiers(living, ApugliPowers.FREEZE.get(), original);
                apugli$modifiedFreezeTicks = newValue;
                return newValue;
            } else if (apugli$modifiedFreezeTicks > 0) {
                if (this.getTicksFrozen() > 0)
                    return apugli$modifiedFreezeTicks;
                else
                    apugli$modifiedFreezeTicks = 0;
            }
        }
        return original;
    }


    @ModifyReturnValue(method = "canFreeze", at = @At(value = "RETURN"))
    private boolean apugli$allowFreezing(boolean original) {
        if ((Entity)(Object)this instanceof LivingEntity living && Services.POWER.hasPower(living, ApugliPowers.FREEZE.get())) {
            return true;
        }
        return original;
    }

}
