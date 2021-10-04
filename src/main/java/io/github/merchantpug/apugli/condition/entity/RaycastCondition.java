package io.github.merchantpug.apugli.condition.entity;

import com.jamieswhiteshirt.reachentityattributes.ReachEntityAttributes;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.merchantpug.apugli.Apugli;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

import java.util.function.Predicate;

@SuppressWarnings("unchecked")
public class RaycastCondition {
    public static boolean condition(SerializableData.Instance data, Entity entity) {
        double baseReach = (entity instanceof PlayerEntity && ((PlayerEntity) entity).getAbilities().creativeMode) ? 5.0D : 4.5D;
        double reach = FabricLoader.getInstance().isModLoaded("reach-entity-attributes") ? ReachEntityAttributes.getReachDistance((LivingEntity)entity, baseReach) : baseReach;
        double distance = data.isPresent("distance") ? data.getDouble("distance") : reach;
        Vec3d eyePosition = entity.getCameraPosVec(0);
        Vec3d lookVector = entity.getRotationVec(0);
        Vec3d traceEnd = eyePosition.add(lookVector.x * distance, lookVector.y * distance, lookVector.z * distance);
        Box box = entity.getBoundingBox().stretch(lookVector).expand(1.0D);

        RaycastContext context = new RaycastContext(eyePosition, traceEnd, RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.ANY, entity);
        BlockHitResult blockHitResult = entity.world.raycast(context);

        double entityReach = blockHitResult != null ? blockHitResult.getBlockPos().getSquaredDistance(eyePosition.x, eyePosition.y, eyePosition.z, true) : distance * distance;
        EntityHitResult entityHitResult = ProjectileUtil.raycast(entity, eyePosition, traceEnd, box, (traceEntity) -> !traceEntity.isSpectator() && traceEntity.collides(), entityReach);

        HitResult.Type blockHitResultType = blockHitResult.getType();
        HitResult.Type entityHitResultType = (entityHitResult != null) ? entityHitResult.getType() : null;
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
        return data.isPresent("target_condition") && ((Predicate<Entity>)data.get("target_condition")).test(targetEntity);
    }

    public static ConditionFactory<Entity> getFactory() {
        return new ConditionFactory<>(Apugli.identifier("raycast"),
                new SerializableData()
                        .add("distance", SerializableDataTypes.DOUBLE, null)
                        .add("block_action", ApoliDataTypes.BLOCK_ACTION, null)
                        .add("block_condition", ApoliDataTypes.BLOCK_CONDITION, null)
                        .add("target_action", ApoliDataTypes.ENTITY_ACTION, null)
                        .add("target_condition", ApoliDataTypes.ENTITY_CONDITION, null)
                        .add("self_action", ApoliDataTypes.ENTITY_ACTION, null),
                RaycastCondition::condition
        );
    }
}
