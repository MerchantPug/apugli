package io.github.merchantpug.apugli.action.entity;

import dev.architectury.injectables.annotations.ExpectPlatform;
import io.github.apace100.origins.power.factory.action.ActionFactory;
import io.github.apace100.origins.util.SerializableData;
import io.github.apace100.origins.util.SerializableDataType;
import io.github.merchantpug.apugli.Apugli;
import io.github.merchantpug.apugli.util.BackportedDataTypes;
import io.github.merchantpug.apugli.util.RaycastUtils;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Pair;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Triple;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

@SuppressWarnings("unchecked")
public class RaycastAction {
    public static void action(SerializableData.Instance data, Entity entity) {
        double baseReach = (entity instanceof PlayerEntity && ((PlayerEntity)entity).abilities.creativeMode) ? 5.0D : 4.5D;
        double reach = getReach(entity, baseReach);
        double distance = data.isPresent("distance") ? data.getDouble("distance") : reach;
        Vec3d eyePosition = entity.getCameraPosVec(0);
        Vec3d lookVector = entity.getRotationVec(0).multiply(distance);
        Vec3d traceEnd = eyePosition.add(lookVector);

        RaycastContext context = new RaycastContext(eyePosition, traceEnd, RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, entity);
        BlockHitResult blockHitResult = entity.world.raycast(context);

        double baseEntityAttackRange = (entity instanceof PlayerEntity && ((PlayerEntity)entity).abilities.creativeMode) ? 6.0D : 3.0D;
        double entityAttackRange = getAttackRange(entity, baseEntityAttackRange);
        double entityDistance = data.isPresent("distance") ? data.getDouble("distance") : entityAttackRange;
        Vec3d entityLookVector = entity.getRotationVec(0).multiply(entityDistance);
        Vec3d entityTraceEnd = eyePosition.add(entityLookVector);
        Box entityBox = entity.getBoundingBox().stretch(lookVector).expand(1.0D);

        double blockHitResultSquaredDistance = blockHitResult != null ? blockHitResult.getBlockPos().getSquaredDistance(eyePosition.x, eyePosition.y, eyePosition.z, true) : entityDistance * entityDistance;
        double entityReach = Math.min(blockHitResultSquaredDistance, entityDistance * entityDistance);
        EntityHitResult entityHitResult = RaycastUtils.raycast(entity, eyePosition, entityTraceEnd, entityBox, (traceEntity) -> !traceEntity.isSpectator() && traceEntity.collides(), entityReach);

        HitResult.Type blockHitResultType = blockHitResult.getType();
        HitResult.Type entityHitResultType = entityHitResult != null ? entityHitResult.getType() : null;

        double squaredParticleDistance = entityHitResult != null && !data.getBoolean("pierce") ? entityHitResult.getPos().squaredDistanceTo(eyePosition.x, eyePosition.y, eyePosition.z) : entityReach;
        createParticlesAtHitPos(data, entity, Math.sqrt(squaredParticleDistance));

        if (data.getBoolean("pierce")) {
            List<EntityHitResult> list = RaycastUtils.raycastMultiple(entity, eyePosition, traceEnd, entityBox, (traceEntity) -> !traceEntity.isSpectator() && traceEntity.collides(), entityReach);
            RaycastAction.handlePierce(data, entity, list);
            return;
        }

        if (blockHitResultType == HitResult.Type.MISS && entityHitResultType == HitResult.Type.MISS) return;

        if (blockHitResultType == HitResult.Type.BLOCK) {
            RaycastAction.onHitBlock(data, entity, blockHitResult);
        }

        if (entityHitResultType == HitResult.Type.ENTITY) {
            RaycastAction.onHitEntity(data, entity, entityHitResult, false);
        }
    }

    private static void handlePierce(SerializableData.Instance data, Entity entity, List<EntityHitResult> list) {
        if (list.isEmpty()) return;
        list.forEach(targetEntity -> onHitEntity(data, entity, targetEntity, true));
        fireSelfAction(data, entity);
    }

    private static void createParticlesAtHitPos(SerializableData.Instance data, Entity entity, double entityReach) {
        if (!data.isPresent("particle") || entity.world.isClient()) return;
        ParticleEffect particleEffect = (ParticleEffect)data.get("particle");

        for (double d = data.getDouble("spacing"); d < entityReach; d += data.getDouble("spacing")) {
            ((ServerWorld)entity.world).spawnParticles(particleEffect, entity.getX() + d * entity.getRotationVec(0).getX(), entity.getEyeY() + d * entity.getRotationVec(0).getY(), entity.getZ() + d * entity.getRotationVec(0).getZ(), 1, 0, 0, 0, 0);
        }
    }

    private static void fireSelfAction(SerializableData.Instance data, Entity entity) {
        if (!data.isPresent("self_action") || !entity.isAlive()) return;
        Consumer<Entity> selfAction = (ActionFactory<Entity>.Instance)data.get("self_action");

        selfAction.accept(entity);
    }

    private static void onHitBlock(SerializableData.Instance data, Entity entity, BlockHitResult result) {
        if (!data.isPresent("block_action")) return;
        CachedBlockPosition blockPosition = new CachedBlockPosition(entity.world, result.getBlockPos(), true);

        boolean blockCondition = !data.isPresent("block_condition") || ((Predicate<CachedBlockPosition>)data.get("block_condition")).test(blockPosition);
        if (!blockCondition) return;

        Consumer<Triple<World, BlockPos, Direction>> blockAction = (Consumer<Triple<World, BlockPos, Direction>>)data.get("block_action");
        blockAction.accept(Triple.of(entity.world, result.getBlockPos(), result.getSide()));

        fireSelfAction(data, entity);
    }

    private static void onHitEntity(SerializableData.Instance data, Entity entity, EntityHitResult result, boolean calledThroughPierce) {
        if (!data.isPresent("target_action")) return;
        Entity targetEntity = result.getEntity();
        Pair<Entity, Entity> pair = new Pair<>(entity, targetEntity);

        boolean targetCondition = (!data.isPresent("target_condition") || ((Predicate<Entity>)data.get("target_condition")).test(targetEntity));
        if(!targetCondition) return;

        Consumer<Entity> targetAction = (Consumer<Entity>)data.get("target_action");
        if (targetAction != null) targetAction.accept(targetEntity);

        if (calledThroughPierce) return;
        fireSelfAction(data, entity);
    }

    @ExpectPlatform
    private static double getReach(Entity entity, double baseReach) {
        throw new AssertionError();
    }

    @ExpectPlatform
    private static double getAttackRange(Entity entity, double baseAttackRange) {
        throw new AssertionError();
    }


    public static ActionFactory<Entity> getFactory() {
        return new ActionFactory<>(Apugli.identifier("raycast"),
                new SerializableData()
                        .add("distance", SerializableDataType.DOUBLE, null)
                        .add("pierce", SerializableDataType.BOOLEAN, false)
                        .add("particle", BackportedDataTypes.PARTICLE_EFFECT_OR_TYPE, null)
                        .add("spacing", SerializableDataType.DOUBLE, 0.5)
                        .add("block_action", SerializableDataType.BLOCK_ACTION, null)
                        .add("block_condition", SerializableDataType.BLOCK_CONDITION, null)
                        .add("target_action", SerializableDataType.ENTITY_ACTION, null)
                        .add("target_condition", SerializableDataType.ENTITY_CONDITION, null)
                        .add("self_action", SerializableDataType.ENTITY_ACTION, null),
                RaycastAction::action
        );
    }
}
