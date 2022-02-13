package io.github.merchantpug.apugli.action.entity;

import com.jamieswhiteshirt.reachentityattributes.ReachEntityAttributes;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataType;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.merchantpug.apugli.Apugli;
import io.github.merchantpug.apugli.util.ApugliDataTypes;
import io.github.merchantpug.apugli.util.RaycastUtils;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.particle.*;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Pair;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.*;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Triple;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

@SuppressWarnings("unchecked")
public class RaycastAction {
    public static void action(SerializableData.Instance data, Entity entity) {
        double baseReach = (entity instanceof PlayerEntity && ((PlayerEntity)entity).getAbilities().creativeMode) ? 5.0D : 4.5D;
        double reach = (entity instanceof LivingEntity && FabricLoader.getInstance().isModLoaded("reach-entity-attributes")) ? ReachEntityAttributes.getReachDistance((LivingEntity)entity, baseReach) : baseReach;
        double distance = data.isPresent("distance") ? data.getDouble("distance") : reach;
        Vec3d eyePosition = entity.getCameraPosVec(0);
        Vec3d lookVector = entity.getRotationVec(0).multiply(distance);
        Vec3d traceEnd = eyePosition.add(lookVector);

        RaycastContext context = new RaycastContext(eyePosition, traceEnd, RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, entity);
        BlockHitResult blockHitResult = entity.world.raycast(context);

        double baseEntityAttackRange = (entity instanceof PlayerEntity && ((PlayerEntity)entity).getAbilities().creativeMode) ? 6.0D : 3.0D;
        double entityAttackRange = (entity instanceof LivingEntity &&  FabricLoader.getInstance().isModLoaded("reach-entity-attributes")) ? ReachEntityAttributes.getAttackRange((LivingEntity)entity, baseEntityAttackRange) : baseEntityAttackRange;
        double entityDistance = data.isPresent("distance") ? data.getDouble("distance") : entityAttackRange;
        Vec3d entityLookVector = entity.getRotationVec(0).multiply(entityDistance);
        Vec3d entityTraceEnd = eyePosition.add(entityLookVector);
        Box entityBox = entity.getBoundingBox().stretch(lookVector).expand(1.0D);

        double blockHitResultSquaredDistance = blockHitResult != null ? blockHitResult.getBlockPos().getSquaredDistance(eyePosition.x, eyePosition.y, eyePosition.z, true) : entityDistance * entityDistance;
        double entityReach = Math.min(blockHitResultSquaredDistance, entityDistance * entityDistance);
        EntityHitResult entityHitResult = ProjectileUtil.raycast(entity, eyePosition, entityTraceEnd, entityBox, (traceEntity) -> !traceEntity.isSpectator() && traceEntity.collides(), entityReach);

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
            ((ServerWorld)entity.world).spawnParticles(particleEffect, entity.getEyePos().getX() + d * entity.getRotationVec(0).getX(), entity.getEyePos().getY() + d * entity.getRotationVec(0).getY(), entity.getEyePos().getZ() + d * entity.getRotationVec(0).getZ(), 1, 0, 0, 0, 0);
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
        if (!data.isPresent("target_action") && !data.isPresent("bientity_action")) return;
        Entity targetEntity = result.getEntity();
        Pair<Entity, Entity> pair = new Pair<>(entity, targetEntity);

        boolean targetCondition = (!data.isPresent("target_condition") || ((Predicate<Entity>)data.get("target_condition")).test(targetEntity)) && (!data.isPresent("bientity_condition") || ((Predicate<Pair<Entity, Entity>>)data.get("bientity_condition")).test(pair));
        if(!targetCondition) return;

        Consumer<Entity> targetAction = (Consumer<Entity>)data.get("target_action");
        if (targetAction != null) targetAction.accept(targetEntity);
        Consumer<Pair<Entity, Entity>> biEntityAction = (Consumer<Pair<Entity, Entity>>)data.get("bientity_action");
        if (biEntityAction != null) biEntityAction.accept(pair);

        if (calledThroughPierce) return;
        fireSelfAction(data, entity);
    }

    public static ActionFactory<Entity> getFactory() {
        return new ActionFactory<>(Apugli.identifier("raycast"),
                new SerializableData()
                        .add("distance", SerializableDataTypes.DOUBLE, null)
                        .add("pierce", SerializableDataTypes.BOOLEAN, false)
                        .add("particle", SerializableDataTypes.PARTICLE_EFFECT_OR_TYPE, null)
                        .add("spacing", SerializableDataTypes.DOUBLE, 0.5)
                        .add("block_action", ApoliDataTypes.BLOCK_ACTION, null)
                        .add("block_condition", ApoliDataTypes.BLOCK_CONDITION, null)
                        .add("bientity_action", ApoliDataTypes.BIENTITY_ACTION, null)
                        .add("bientity_condition", ApoliDataTypes.BIENTITY_CONDITION, null)
                        .add("target_action", ApoliDataTypes.ENTITY_ACTION, null)
                        .add("target_condition", ApoliDataTypes.ENTITY_CONDITION, null)
                        .add("self_action", ApoliDataTypes.ENTITY_ACTION, null),
                RaycastAction::action
        );
    }
}
