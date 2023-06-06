package net.merchantpug.apugli.util;

import com.mojang.math.Vector3f;
import io.github.apace100.apoli.util.Space;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class RaycastUtil {

    public static BlockHitResult raycastBlock(Entity actor, double dis) {
        return raycastBlock(actor, dis, null, Space.WORLD);
    }

    public static BlockHitResult raycastBlock(Entity actor, double dis, Vec3 direction, Space space) {
        Vec3 rayStart = actor.getEyePosition(0);
        Vec3 rayDir;
        if (direction == null) {
            rayDir = actor.getViewVector(0).scale(dis);
        } else {
            Vector3f vector3f = new Vector3f(direction.normalize());
            space.toGlobal(vector3f, actor);
            rayDir = new Vec3(vector3f.x(), vector3f.y(), vector3f.z()).scale(dis);
        }
        Vec3 rayEnd = rayStart.add(rayDir);
        ClipContext context = new ClipContext(rayStart, rayEnd, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, actor);
        return actor.level.clip(context);
    }

    public static EntityHitResult raycastEntity(BlockHitResult blockHitResult, Entity actor, double dis) {
        return raycastEntity(blockHitResult, actor, dis, null, Space.WORLD, null);
    }

    public static EntityHitResult raycastEntity(BlockHitResult blockHitResult, Entity actor, double dis, Vec3 direction, Space space, Predicate<Tuple<Entity, Entity>> predicate) {
        Vec3 rayStart = actor.getEyePosition(0);
        Vec3 rayDir;
        if (direction == null) {
            rayDir = actor.getViewVector(0).scale(dis);
        } else {
            Vector3f vector3f = new Vector3f(direction.normalize());
            space.toGlobal(vector3f, actor);
            rayDir = new Vec3(vector3f.x(), vector3f.y(), vector3f.z()).scale(dis);
        }
        Vec3 rayEnd = rayStart.add(rayDir);
        AABB entityBox = actor.getBoundingBox().expandTowards(rayDir).inflate(1.0D);
        double blockHitDisSqr = blockHitResult != null
            ? blockHitResult.getBlockPos().distToLowCornerSqr(rayStart.x, rayStart.y, rayStart.z)
            : dis * dis;
        double disSqr = Math.min(blockHitDisSqr, dis * dis);
        return ProjectileUtil.getEntityHitResult(actor, rayStart, rayEnd, entityBox,
            traceEntity -> !traceEntity.isSpectator() && traceEntity.isPickable() && (predicate == null || predicate.test(new Tuple<>(actor, traceEntity))), disSqr);
    }
    
    public static List<EntityHitResult> raycastEntities(Entity actor, Predicate<Entity> predicate, double dis, Vec3 direction, Space space) {
        Vec3 rayStart = actor.getEyePosition(0);
        Vec3 rayDir;
        if (direction == null) {
            rayDir = actor.getViewVector(0).scale(dis);
        } else {
            Vector3f vector3f = new Vector3f(direction.normalize());
            space.toGlobal(vector3f, actor);
            rayDir = new Vec3(vector3f.x(), vector3f.y(), vector3f.z()).scale(dis);
        }
        Vec3 rayEnd = rayStart.add(rayDir);
        AABB entityBox = actor.getBoundingBox().expandTowards(rayDir).inflate(1.0D);
        List<EntityHitResult> results = new ArrayList<>();
        Level level = actor.level;
        for(Entity target : level.getEntities(actor, entityBox, predicate)) {
            AABB targetBB = target.getBoundingBox().inflate(target.getPickRadius());
            Optional<Vec3> intersection = targetBB.clip(rayStart, rayEnd);
            if(targetBB.contains(rayStart)) {
                if(dis >= 0.0D) {
                    results.add(new EntityHitResult(target, intersection.orElse(rayStart)));
                }
            } else if(intersection.isPresent()) {
                double targetDis = rayStart.distanceToSqr(intersection.get());
                if(targetDis < dis || dis == 0.0D) {
                    if(target.getRootVehicle() == actor.getRootVehicle()) {
                        if(dis == 0.0D) {
                            results.add(new EntityHitResult(target, intersection.get()));
                        }
                    } else {
                        dis = targetDis;
                        results.add(new EntityHitResult(target, intersection.get()));
                    }
                }
            }
        }
        return results;
    }
    
}
