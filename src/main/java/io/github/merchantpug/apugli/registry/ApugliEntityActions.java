package io.github.merchantpug.apugli.registry;

import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.apoli.registry.ApoliRegistries;
import io.github.apace100.calio.data.SerializableData;
import io.github.merchantpug.apugli.Apugli;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.mob.ZombieVillagerEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.WorldEvents;

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
    }

    private static void register(ActionFactory<Entity> actionFactory) {
        Registry.register(ApoliRegistries.ENTITY_ACTION, actionFactory.getSerializerId(), actionFactory);
    }
}
