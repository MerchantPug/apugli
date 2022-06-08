package com.github.merchantpug.apugli.action.entity;

import com.github.merchantpug.apugli.Apugli;
import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.calio.data.SerializableData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.mob.ZombieVillagerEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.WorldEvents;

public class ZombifyVillagerAction {
    public static void action(SerializableData.Instance data, Entity entity) {
        if (!(entity instanceof VillagerEntity villagerEntity)) return;
        ZombieVillagerEntity zombieVillagerEntity = villagerEntity.convertTo(EntityType.ZOMBIE_VILLAGER, false);
        if (zombieVillagerEntity != null) {
            zombieVillagerEntity.initialize((ServerWorldAccess) zombieVillagerEntity.world, zombieVillagerEntity.world.getLocalDifficulty(zombieVillagerEntity.getBlockPos()), SpawnReason.CONVERSION, new ZombieEntity.ZombieData(false, true), null);
            zombieVillagerEntity.setVillagerData(villagerEntity.getVillagerData());
            zombieVillagerEntity.setGossipData(villagerEntity.getGossip().serialize(NbtOps.INSTANCE).getValue());
            zombieVillagerEntity.setOfferData(villagerEntity.getOffers().toNbt());
            zombieVillagerEntity.setXp(villagerEntity.getExperience());
        }
        if (villagerEntity.getAttacker() != null) villagerEntity.damage(DamageSource.mob(villagerEntity.getAttacker()), Float.MAX_VALUE);
        else villagerEntity.damage(DamageSource.GENERIC, Float.MAX_VALUE);
        entity.world.syncWorldEvent(null, WorldEvents.ZOMBIE_INFECTS_VILLAGER, entity.getBlockPos(), 0);
    }

    public static ActionFactory<Entity> getFactory() {
        return new ActionFactory<>(Apugli.identifier("zombify_villager"),
                new SerializableData(),
                ZombifyVillagerAction::action
        );
    }
}
