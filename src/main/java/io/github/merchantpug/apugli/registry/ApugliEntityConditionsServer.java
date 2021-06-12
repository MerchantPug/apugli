package io.github.merchantpug.apugli.registry;

import com.jamieswhiteshirt.reachentityattributes.ReachEntityAttributes;
import io.github.apace100.apoli.Apoli;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.apoli.registry.ApoliRegistries;
import io.github.apace100.calio.data.SerializableData;
import io.github.merchantpug.apugli.Apugli;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.RaycastContext;

import java.util.function.Predicate;

public class ApugliEntityConditionsServer {
    @SuppressWarnings("unchecked")
    public static void register() {
        register(new ConditionFactory<>(Apugli.identifier("block_looking_at"), new SerializableData()
                .add("block_condition", ApoliDataTypes.BLOCK_CONDITION),
                (data, entity) -> {
                    Predicate<CachedBlockPosition> blockCondition = ((ConditionFactory<CachedBlockPosition>.Instance)data.get("block_condition"));
                    if (entity instanceof ServerPlayerEntity) {
                        ServerPlayerInteractionManager interactionManager = ((ServerPlayerEntity) entity).interactionManager;
                        double baseReach;
                        if (interactionManager.isCreative()) {
                            baseReach = 5.0D;
                        } else {
                            baseReach = 4.5D;
                        }
                        double reach = ReachEntityAttributes.getReachDistance(entity, baseReach);
                        Vec3d vec3d = entity.getCameraPosVec(0.0F);
                        Vec3d vec3d2 = entity.getRotationVec(0.0F);
                        Vec3d vec3d3 = vec3d.add(vec3d2.x * reach, vec3d2.y * reach, vec3d2.z * reach);
                        BlockHitResult blockHitResult = entity.world.raycast(new RaycastContext(vec3d, vec3d3, RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, (Entity) entity));
                        if (blockHitResult != null && blockHitResult.getType() == HitResult.Type.BLOCK) {
                            return blockCondition.test(new CachedBlockPosition(entity.world, blockHitResult.getBlockPos(), true));
                        }
                    }
                    return false;
                }));
    }

    private static void register(ConditionFactory<LivingEntity> conditionFactory) {
        Registry.register(ApoliRegistries.ENTITY_CONDITION, conditionFactory.getSerializerId(), conditionFactory);
    }
}
