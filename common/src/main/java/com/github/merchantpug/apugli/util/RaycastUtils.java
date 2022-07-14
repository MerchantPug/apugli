package com.github.merchantpug.apugli.util;

import net.minecraft.entity.Entity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class RaycastUtils {
    @Nullable
    public static EntityHitResult raycast(Entity entity, Vec3d vec3d, Vec3d vec3d2, Box box, Predicate<Entity> predicate, double d) {
        World world = entity.world;
        double e = d;
        Entity entity2 = null;
        Vec3d vec3d3 = null;
        Iterator var12 = world.getOtherEntities(entity, box, predicate).iterator();

        while(true) {
            while(var12.hasNext()) {
                Entity entity3 = (Entity)var12.next();
                Box box2 = entity3.getBoundingBox().expand((double)entity3.getTargetingMargin());
                Optional<Vec3d> optional = box2.raycast(vec3d, vec3d2);
                if (box2.contains(vec3d)) {
                    if (e >= 0.0D) {
                        entity2 = entity3;
                        vec3d3 = (Vec3d)optional.orElse(vec3d);
                        e = 0.0D;
                    }
                } else if (optional.isPresent()) {
                    Vec3d vec3d4 = (Vec3d)optional.get();
                    double f = vec3d.squaredDistanceTo(vec3d4);
                    if (f < e || e == 0.0D) {
                        if (entity3.getRootVehicle() == entity.getRootVehicle()) {
                            if (e == 0.0D) {
                                entity2 = entity3;
                                vec3d3 = vec3d4;
                            }
                        } else {
                            entity2 = entity3;
                            vec3d3 = vec3d4;
                            e = f;
                        }
                    }
                }
            }

            if (entity2 == null) {
                return null;
            }

            return new EntityHitResult(entity2, vec3d3);
        }
    }

    public static List<EntityHitResult> raycastMultiple(Entity entity, Vec3d vec3d, Vec3d vec3d2, Box box, Predicate<Entity> predicate, double d) {
        List<EntityHitResult> list = new ArrayList<>();
        World world = entity.world;
        double e = d;
        Entity entity2;
        Vec3d vec3d3;
        Iterator var12 = world.getOtherEntities(entity, box, predicate).iterator();

        while(true) {
            while(var12.hasNext()) {
                Entity entity3 = (Entity) var12.next();
                Box box2 = entity3.getBoundingBox().expand(entity3.getTargetingMargin());
                Optional<Vec3d> optional = box2.raycast(vec3d, vec3d2);
                if (box2.contains(vec3d)) {
                    if (e >= 0.0D) {
                        entity2 = entity3;
                        vec3d3 = optional.orElse(vec3d);
                        list.add(new EntityHitResult(entity2, vec3d3));
                    }
                } else if (optional.isPresent()) {
                    Vec3d vec3d4 = optional.get();
                    double f = vec3d.squaredDistanceTo(vec3d4);
                    if (f < e || e == 0.0D) {
                        if (entity3.getRootVehicle() == entity.getRootVehicle()) {
                            if (e == 0.0D) {
                                entity2 = entity3;
                                vec3d3 = vec3d4;
                                list.add(new EntityHitResult(entity2, vec3d3));
                            }
                        } else {
                            entity2 = entity3;
                            vec3d3 = vec3d4;
                            e = f;
                            list.add(new EntityHitResult(entity2, vec3d3));
                        }
                    }
                }
            }
            return list;
        }
    }
}
