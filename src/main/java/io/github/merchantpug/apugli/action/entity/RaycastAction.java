package io.github.merchantpug.apugli.action.entity;

import com.jamieswhiteshirt.reachentityattributes.ReachEntityAttributes;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.factory.action.ActionFactory;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Triple;

import java.util.function.Consumer;
import java.util.function.Predicate;

@SuppressWarnings("unchecked")
public class RaycastAction {
    public static void action(SerializableData.Instance data, Entity entity) {
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
        if (blockHitResultType == HitResult.Type.MISS && entityHitResultType == HitResult.Type.MISS) return;

        if (blockHitResultType == HitResult.Type.BLOCK) {
            RaycastAction.onHitBlock(data, entity, blockHitResult);
        }

        if (entityHitResultType == HitResult.Type.ENTITY) {
            RaycastAction.onHitEntity(data, entity, entityHitResult);
        }
    }

    private static void fireSelfAction(SerializableData.Instance data, Entity entity) {
        if (!data.isPresent("self_action") || !entity.isAlive()) return;
        Consumer<Entity> selfAction = (Consumer<Entity>)data.get("self_action");

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

    private static void onHitEntity(SerializableData.Instance data, Entity entity, EntityHitResult result) {
        if (!data.isPresent("target_action")) return;
        Entity targetEntity = result.getEntity();

        boolean targetCondition = !data.isPresent("target_condition") || ((Predicate<Entity>)data.get("target_condition")).test(targetEntity);
        if(!targetCondition) return;

        Consumer<Entity> targetAction = (Consumer<Entity>)data.get("target_action");
        targetAction.accept(targetEntity);

        fireSelfAction(data, entity);
    }

    public static ActionFactory<Entity> getFactory() {
        return new ActionFactory<>(Apugli.identifier("raycast"),
                new SerializableData()
                        .add("distance", SerializableDataTypes.DOUBLE, null)
                        .add("block_action", ApoliDataTypes.BLOCK_ACTION, null)
                        .add("block_condition", ApoliDataTypes.BLOCK_CONDITION, null)
                        .add("target_action", ApoliDataTypes.ENTITY_ACTION, null)
                        .add("target_condition", ApoliDataTypes.ENTITY_CONDITION, null)
                        .add("self_action", ApoliDataTypes.ENTITY_ACTION, null),
                RaycastAction::action
        );
    }
}
