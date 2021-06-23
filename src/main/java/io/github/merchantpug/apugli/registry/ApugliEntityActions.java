package io.github.merchantpug.apugli.registry;

import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.apoli.registry.ApoliRegistries;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.merchantpug.apugli.Apugli;
import io.github.merchantpug.apugli.util.ApugliDataTypes;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.mob.ZombieVillagerEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.WorldEvents;
import net.minecraft.world.explosion.Explosion;

import java.util.Collection;

public class ApugliEntityActions {
    public static void register() {
        register(new ActionFactory<>(Apugli.identifier("zombify_villager"), new SerializableData(),
            (data, entity) -> {
                if (entity instanceof VillagerEntity) {
                    VillagerEntity villagerEntity = (VillagerEntity)entity;
                    ZombieVillagerEntity zombieVillagerEntity = villagerEntity.convertTo(EntityType.ZOMBIE_VILLAGER, false);
                    if (zombieVillagerEntity != null) {
                        zombieVillagerEntity.initialize((ServerWorldAccess)zombieVillagerEntity.world, zombieVillagerEntity.world.getLocalDifficulty(zombieVillagerEntity.getBlockPos()), SpawnReason.CONVERSION, new ZombieEntity.ZombieData(false, true), (NbtCompound)null);
                        zombieVillagerEntity.setVillagerData(villagerEntity.getVillagerData());
                        zombieVillagerEntity.setGossipData(villagerEntity.getGossip().serialize(NbtOps.INSTANCE).getValue());
                        zombieVillagerEntity.setOfferData(villagerEntity.getOffers().toNbt());
                        zombieVillagerEntity.setXp(villagerEntity.getExperience());
                    }
                    villagerEntity.damage(ApugliDamageSources.zombification(((VillagerEntity)entity).getAttacker()), Float.MAX_VALUE);
                    entity.world.syncWorldEvent(null, WorldEvents.ZOMBIE_INFECTS_VILLAGER, entity.getBlockPos(), 0);
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
                    if (entity instanceof LivingEntity) {
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
                            DamageSource s = (DamageSource)data.get("source");
                            float a = data.getFloat("amount");
                            if (s != null && a != 0.0F) {
                                entity.damage(s, a);
                            }
                        }
                    }));
    }

    private static void register(ActionFactory<Entity> actionFactory) {
        Registry.register(ApoliRegistries.ENTITY_ACTION, actionFactory.getSerializerId(), actionFactory);
    }
}
