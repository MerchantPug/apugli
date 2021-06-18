package io.github.merchantpug.apugli.registry;

import com.jamieswhiteshirt.reachentityattributes.ReachEntityAttributes;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.apoli.registry.ApoliRegistries;
import io.github.apace100.apoli.util.Comparison;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.merchantpug.apugli.Apugli;
import io.github.merchantpug.apugli.util.ApugliDataTypes;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.RaycastContext;

import java.util.function.Predicate;

public class ApugliEntityConditions {
    public static void register() {
        register(new ConditionFactory<>(Apugli.identifier("nearby_entities"), new SerializableData()
                .add("entity_type", SerializableDataTypes.ENTITY_TYPE)
                .add("player_box_multiplier", SerializableDataTypes.FLOAT)
                .add("comparison", ApoliDataTypes.COMPARISON)
                .add("compare_to", SerializableDataTypes.INT),
                (data, entity) -> {
                    EntityType<?> entityType = (EntityType<?>) data.get("entity_type");
                    Float playerBoxMultiplier = (Float) data.get("player_box_multiplier");
                    int amount = entity.world.getOtherEntities(entity, entity.getBoundingBox().expand(playerBoxMultiplier), typeOfEntity -> typeOfEntity.getType() == entityType).size();
                    Comparison comparison = ((Comparison) data.get("comparison"));
                    int compareTo = data.getInt("compare_to");
                    return comparison.compare(amount, compareTo);
                }));
        register(new ConditionFactory<>(Apugli.identifier("entity_group"), new SerializableData()
                .add("group", ApugliDataTypes.APUGLI_ENTITY_GROUP),
                (data, entity) ->
                        entity.getGroup() == data.get("group")));
        register(new ConditionFactory<>(Apugli.identifier("can_have_effect"), new SerializableData()
                .add("effect", SerializableDataTypes.STATUS_EFFECT),
                (data, entity) -> {
                    StatusEffect effect = (StatusEffect)data.get("effect");
                    StatusEffectInstance instance = new StatusEffectInstance(effect);
                    return entity.canHaveStatusEffect(instance);
                }));
        register(new ConditionFactory<>(Apugli.identifier("block_looking_at"), new SerializableData()
                .add("block_condition", ApoliDataTypes.BLOCK_CONDITION),
                (data, entity) -> {
                    Predicate<CachedBlockPosition> blockCondition = (ConditionFactory<CachedBlockPosition>.Instance)data.get("block_condition");
                    if (entity instanceof PlayerEntity) {
                        double baseReach = 4.5D;
                        if (((PlayerEntity)entity).getAbilities().creativeMode) {
                            baseReach = 5.0D;
                        }
                        double reach = baseReach;
                        if (FabricLoader.getInstance().isModLoaded("reach-entity-attributes")) {
                            reach = ReachEntityAttributes.getReachDistance(entity, baseReach);
                        }
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
