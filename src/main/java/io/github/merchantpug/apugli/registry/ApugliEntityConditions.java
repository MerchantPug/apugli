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
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.*;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.gen.feature.StructureFeature;

import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ApugliEntityConditions {

    @SuppressWarnings("unchecked")
    public static void register() {
        register(new ConditionFactory<>(Apugli.identifier("entity_in_radius"), new SerializableData()
                .add("condition", ApoliDataTypes.ENTITY_CONDITION)
                .add("radius", SerializableDataTypes.DOUBLE)
                .add("compare_to", SerializableDataTypes.INT, 1)
                .add("comparison", ApoliDataTypes.COMPARISON, Comparison.GREATER_THAN_OR_EQUAL),
                (data, entity) -> {
                    Predicate<LivingEntity> entityCondition = ((ConditionFactory<LivingEntity>.Instance)data.get("condition"));
                    int stopAt = -1;
                    Comparison comparison = ((Comparison)data.get("comparison"));
                    int compareTo = data.getInt("compare_to");
                    switch (comparison) {
                        case EQUAL, LESS_THAN_OR_EQUAL, GREATER_THAN -> stopAt = compareTo + 1;
                        case LESS_THAN, GREATER_THAN_OR_EQUAL -> stopAt = compareTo;
                    }
                    int count = 0;
                    for (Entity target : entity.world.getOtherEntities(entity, entity.getBoundingBox().expand(data.getDouble("radius")))) {
                        if (target instanceof LivingEntity) {
                            if (entityCondition.test((LivingEntity)target)) {
                                count++;
                                if (count == stopAt) {
                                    break;
                                }
                            }
                        }
                    }
                    return comparison.compare(count, compareTo);
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
        register(new ConditionFactory<>(Apugli.identifier("looking_at"), new SerializableData()
                .add("block_condition", ApoliDataTypes.BLOCK_CONDITION, null)
                .add("condition", ApoliDataTypes.ENTITY_CONDITION, null),
                (data, entity) -> {
                    if (entity instanceof LivingEntity && !entity.world.isClient()) {
                        double baseReach = 4.5D;
                        if (entity instanceof PlayerEntity) {
                            if (((PlayerEntity) entity).getAbilities().creativeMode) {
                                baseReach = 5.0D;
                            }
                        }
                        double reach;
                        if (FabricLoader.getInstance().isModLoaded("reach-entity-attributes")) {
                            reach = ReachEntityAttributes.getReachDistance((LivingEntity) entity, baseReach);
                        } else {
                            reach = baseReach;
                        }
                        Vec3d vec3d = entity.getCameraPosVec(0.0F);
                        Vec3d vec3d2 = entity.getRotationVec(0.0F);
                        Vec3d vec3d3 = vec3d.add(vec3d2.x * reach, vec3d2.y * reach, vec3d2.z * reach);
                        Box box = entity.getBoundingBox().stretch(vec3d2).expand(1.0D);
                        double d = reach * reach;
                        Predicate<Entity> predicate = (entityx) -> !entityx.isSpectator() && entityx.collides();
                        EntityHitResult entityHitResult = ProjectileUtil.raycast(entity, vec3d, vec3d3, box, predicate, d);
                        BlockHitResult blockHitResult = entity.world.raycast(new RaycastContext(vec3d, vec3d3, RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, entity));
                        if (entityHitResult != null && entityHitResult.getEntity() instanceof LivingEntity) {
                            if (data.isPresent("condition")) {
                                Predicate<LivingEntity> entityCondition = (ConditionFactory<LivingEntity>.Instance) data.get("target_condition");
                                return entityCondition.test((LivingEntity) entityHitResult.getEntity());
                            }
                            return false;
                        } else if (entityHitResult != null && !(entityHitResult.getEntity() instanceof LivingEntity)) {
                            return false;
                        } else if (blockHitResult != null) {
                            if (data.isPresent("block_condition")) {
                                Predicate<CachedBlockPosition> blockCondition = (ConditionFactory<CachedBlockPosition>.Instance) data.get("block_condition");
                                return blockCondition.test(new CachedBlockPosition(entity.world, blockHitResult.getBlockPos(), true));
                            }
                        }
                    }
                    return false;
                }));
        register(new ConditionFactory<>(Apugli.identifier("structure"), new SerializableData()
                .add("structure", SerializableDataTypes.IDENTIFIER),
                (data, entity) -> {
                    if (entity.getEntityWorld() instanceof ServerWorld) {
                        StructureFeature<?> structure = Registry.STRUCTURE_FEATURE.get(data.getId("structure"));
                        BlockPos structurePos = ((ServerWorld)entity.getEntityWorld()).locateStructure(structure, entity.getBlockPos(), 12, false);
                        if (structurePos != null) {
                            ChunkPos structureChunkPos = new ChunkPos(structurePos.getX() >> 4, structurePos.getZ() >> 4);
                            StructureStart<?> structureStart = ((ServerWorld)entity.getEntityWorld()).getStructureAccessor().getStructureStart(ChunkSectionPos.from(structureChunkPos, 0), structure, entity.world.getChunk(structurePos));
                            List<StructurePiece> structurePieces = structureStart.getChildren().stream().toList();
                            for (StructurePiece structurePiece : structurePieces) {
                                BlockBox box = structurePiece.getBoundingBox();
                                if (entity.getBoundingBox().intersects(Box.from(box))) {
                                    return true;
                                }
                            }
                        }
                    }
                    return false;
                }));
    }

    private static void register(ConditionFactory<LivingEntity> conditionFactory) {
        Registry.register(ApoliRegistries.ENTITY_CONDITION, conditionFactory.getSerializerId(), conditionFactory);
    }
}
