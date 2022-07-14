package io.github.merchantpug.apugli.condition.entity;

import dev.architectury.injectables.annotations.ExpectPlatform;
import io.github.apace100.origins.power.factory.condition.ConditionFactory;
import io.github.apace100.origins.util.SerializableData;
import io.github.apace100.origins.util.SerializableDataType;
import io.github.merchantpug.apugli.Apugli;
import io.github.merchantpug.apugli.util.RaycastUtils;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

import java.util.function.Predicate;

@SuppressWarnings("unchecked")
public class RaycastCondition {
    public static boolean condition(SerializableData.Instance data, LivingEntity entity) {
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

        if (blockHitResultType == HitResult.Type.MISS && entityHitResultType == HitResult.Type.MISS) return false;

        if (blockHitResultType == HitResult.Type.BLOCK) return RaycastCondition.onHitBlock(data, entity, blockHitResult);

        if (entityHitResultType == HitResult.Type.ENTITY) return RaycastCondition.onHitEntity(data, entityHitResult);
        return false;
    }

    private static boolean onHitBlock(SerializableData.Instance data, Entity entity, BlockHitResult result) {
        CachedBlockPosition blockPosition = new CachedBlockPosition(entity.world, result.getBlockPos(), true);
        return data.isPresent("block_condition") && ((Predicate<CachedBlockPosition>)data.get("block_condition")).test(blockPosition);
    }

    private static boolean onHitEntity(SerializableData.Instance data, EntityHitResult result) {
        Entity targetEntity = result.getEntity();
        return (!data.isPresent("target_condition") || ((Predicate<Entity>)data.get("target_condition")).test(targetEntity));
    }

    @ExpectPlatform
    private static double getReach(Entity entity, double baseReach) {
        throw new AssertionError();
    }

    @ExpectPlatform
    private static double getAttackRange(Entity entity, double baseAttackRange) {
        throw new AssertionError();
    }

    public static ConditionFactory<LivingEntity> getFactory() {
        return new ConditionFactory<>(Apugli.identifier("raycast"),
                new SerializableData()
                        .add("distance", SerializableDataType.DOUBLE, null)
                        .add("block_condition", SerializableDataType.BLOCK_CONDITION, null)
                        .add("target_condition", SerializableDataType.ENTITY_CONDITION, null),
                RaycastCondition::condition
        );
    }
}
