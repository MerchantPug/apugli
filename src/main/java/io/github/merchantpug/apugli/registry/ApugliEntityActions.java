package io.github.merchantpug.apugli.registry;

import com.jamieswhiteshirt.reachentityattributes.ReachEntityAttributes;
import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.CooldownPower;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.VariableIntPower;
import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.apoli.registry.ApoliRegistries;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.merchantpug.apugli.Apugli;
import io.github.merchantpug.apugli.util.ApugliDataTypes;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.mob.ZombieVillagerEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtOps;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.*;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
import net.minecraft.world.explosion.Explosion;
import org.apache.commons.lang3.tuple.Triple;

import java.util.Collection;
import java.util.function.Predicate;

public class ApugliEntityActions {
    @SuppressWarnings("unchecked")
    public static void register() {
        register(new ActionFactory<>(Apugli.identifier("zombify_villager"), new SerializableData(),
            (data, entity) -> {
                if (entity instanceof VillagerEntity villagerEntity) {
                    ZombieVillagerEntity zombieVillagerEntity = villagerEntity.convertTo(EntityType.ZOMBIE_VILLAGER, false);
                    if (zombieVillagerEntity != null) {
                        zombieVillagerEntity.initialize((ServerWorldAccess)zombieVillagerEntity.world, zombieVillagerEntity.world.getLocalDifficulty(zombieVillagerEntity.getBlockPos()), SpawnReason.CONVERSION, new ZombieEntity.ZombieData(false, true), null);
                        zombieVillagerEntity.setVillagerData(villagerEntity.getVillagerData());
                        zombieVillagerEntity.setGossipData(villagerEntity.getGossip().serialize(NbtOps.INSTANCE).getValue());
                        zombieVillagerEntity.setOfferData(villagerEntity.getOffers().toNbt());
                        zombieVillagerEntity.setXp(villagerEntity.getExperience());
                    }
                    villagerEntity.damage(ApugliDamageSources.zombification(((VillagerEntity)entity).getAttacker()), Float.MAX_VALUE);
                    entity.world.syncWorldEvent(null, WorldEvents.ZOMBIE_INFECTS_VILLAGER, entity.getBlockPos(), 0);
                }
            }));
        register(new ActionFactory<>(Apugli.identifier("raycast"), new SerializableData()
                .add("block_action", ApoliDataTypes.BLOCK_ACTION, null)
                .add("block_condition", ApoliDataTypes.BLOCK_CONDITION, null)
                .add("target_action", ApoliDataTypes.ENTITY_ACTION, null)
                .add("target_condition", ApoliDataTypes.ENTITY_CONDITION, null)
                .add("self_action", ApoliDataTypes.ENTITY_ACTION, null),
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
                        if (entityHitResult != null && entityHitResult.getType() == HitResult.Type.ENTITY) {
                            if (data.isPresent("target_action")) {
                                Predicate<LivingEntity> entityCondition = (ConditionFactory<LivingEntity>.Instance) data.get("target_condition");
                                boolean targetCondition = entityCondition == null || entityCondition.test((LivingEntity) entityHitResult.getEntity());
                                if (targetCondition) {
                                    ((ActionFactory<Entity>.Instance) data.get("target_action")).accept(entityHitResult.getEntity());
                                    if (data.isPresent("self_action")) {
                                        ((ActionFactory<Entity>.Instance) data.get("target_action")).accept(entity);
                                    }
                                }
                            }
                        } else if (blockHitResult != null && blockHitResult.getType() == HitResult.Type.BLOCK) {
                            if (data.isPresent("block_action")) {
                                Predicate<CachedBlockPosition> blockCondition = (ConditionFactory<CachedBlockPosition>.Instance) data.get("block_condition");
                                boolean targetCondition = blockCondition == null || blockCondition.test(new CachedBlockPosition(entity.world, blockHitResult.getBlockPos(), true));
                                if (targetCondition) {
                                    ((ActionFactory<Triple<World, BlockPos, Direction>>.Instance) data.get("block_action")).accept(
                                            Triple.of(entity.world, blockHitResult.getBlockPos(), Direction.UP));
                                    if (data.isPresent("self_action")) {
                                        ((ActionFactory<Entity>.Instance) data.get("self_action")).accept(entity);
                                    }
                                }
                            }
                        }
                    }
                }));
        register(new ActionFactory<>(Apugli.identifier("explode"), new SerializableData()
                .add("radius", SerializableDataTypes.FLOAT)
                .add("behavior", ApugliDataTypes.EXPLOSION_BEHAVIOR)
                .add("use_charged", SerializableDataTypes.BOOLEAN, false)
                .add("spawn_effect_cloud", SerializableDataTypes.BOOLEAN, false)
                .add("source", SerializableDataTypes.DAMAGE_SOURCE, null)
                .add("amount", SerializableDataTypes.FLOAT, 0.0F),
                (data, entity) -> {
                    if (entity instanceof LivingEntity && !entity.world.isClient()) {
                        boolean useCharged = data.getBoolean("use_charged");
                        boolean tmoCharged;
                        boolean cursedCharged;
                        float f = 1.0F;
                        if (useCharged) {
                            if (FabricLoader.getInstance().isModLoaded("toomanyorigins")) {
                                tmoCharged = ((LivingEntity)entity).hasStatusEffect(Registry.STATUS_EFFECT.get(new Identifier("toomanyorigins", "charged")));
                            } else tmoCharged = false;
                            if (FabricLoader.getInstance().isModLoaded("cursedorigins")) {
                                cursedCharged = ((LivingEntity)entity).hasStatusEffect(Registry.STATUS_EFFECT.get(new Identifier("cursedorigins", "charged")));
                            } else cursedCharged = false;

                            if (tmoCharged) {
                                ((LivingEntity)entity).removeStatusEffect(Registry.STATUS_EFFECT.get(new Identifier("toomanyorigins", "charged")));
                                f = 2.0F;
                            }
                            if (cursedCharged) {
                                ((LivingEntity)entity).removeStatusEffect(Registry.STATUS_EFFECT.get(new Identifier("cursedorigins", "charged")));
                                f = 2.0F;
                            }
                        }

                        entity.world.createExplosion(entity, DamageSource.explosion((LivingEntity)entity), null, entity.getX(), entity.getY(), entity.getZ(), data.getFloat("radius") * f, false, (Explosion.DestructionType)data.get("behavior"));

                        Collection<StatusEffectInstance> collection = ((LivingEntity)entity).getStatusEffects();
                        if (!collection.isEmpty() && data.getBoolean("spawn_effect_cloud")) {
                            AreaEffectCloudEntity areaEffectCloudEntity = new AreaEffectCloudEntity(entity.world, entity.getX(), entity.getY(), entity.getZ());
                            areaEffectCloudEntity.setRadius(2.5F);
                            areaEffectCloudEntity.setRadiusOnUse(-0.5F);
                            areaEffectCloudEntity.setWaitTime(10);
                            areaEffectCloudEntity.setDuration(areaEffectCloudEntity.getDuration() / 2);
                            areaEffectCloudEntity.setRadiusGrowth(-areaEffectCloudEntity.getRadius() / (float)areaEffectCloudEntity.getDuration());

                            for (StatusEffectInstance statusEffectInstance : collection) {
                                areaEffectCloudEntity.addEffect(new StatusEffectInstance(statusEffectInstance));
                            }
                            entity.world.spawnEntity(areaEffectCloudEntity);
                        }
                        DamageSource source = (DamageSource)data.get("source");
                        float amount = data.getFloat("amount");
                        if (source != null && amount != 0.0F) {
                            entity.damage(source, amount);
                        }
                    }
                }));
        register(new ActionFactory<>(Apugli.identifier("swing_hand"), new SerializableData()
                .add("hand", ApugliDataTypes.HAND),
        (data, entity) -> {
            if (entity instanceof PlayerEntity && !entity.world.isClient) {
                ((PlayerEntity) entity).swingHand((Hand)data.get("hand"), true);
            }
        }));
        register(new ActionFactory<>(Apugli.identifier("drop_item"), new SerializableData()
                .add("equipment_slot", SerializableDataTypes.EQUIPMENT_SLOT)
                .add("item_condition", ApoliDataTypes.ITEM_CONDITION),
                (data, entity) -> {
                    if (entity instanceof LivingEntity) {
                        ConditionFactory<ItemStack>.Instance condition = (ConditionFactory<ItemStack>.Instance)data.get("item_condition");
                        ItemStack equippedItem = ((LivingEntity)entity).getEquippedStack((EquipmentSlot)data.get("equipment_slot"));
                        if(!equippedItem.isEmpty() && condition.test(equippedItem)) {
                            entity.dropStack(equippedItem, entity.getEyeHeight(entity.getPose()));
                            entity.equipStack((EquipmentSlot)data.get("equipment_slot"), ItemStack.EMPTY);
                        }
                    }
                }));
    }

    private static void register(ActionFactory<Entity> actionFactory) {
        Registry.register(ApoliRegistries.ENTITY_ACTION, actionFactory.getSerializerId(), actionFactory);
    }
}
