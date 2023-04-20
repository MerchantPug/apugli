package net.merchantpug.apugli.action.factory.entity;

import net.merchantpug.apugli.action.factory.IActionFactory;
import io.github.apace100.calio.data.SerializableData;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.LevelEvent;

public class ZombifyVillagerAction implements IActionFactory<Entity> {
    
    public void execute(SerializableData.Instance data, Entity entity) {
        if(!(entity instanceof Villager villagerEntity)) return;
        ZombieVillager zombieVillagerEntity = villagerEntity.convertTo(EntityType.ZOMBIE_VILLAGER, false);
        if(zombieVillagerEntity != null) {
            zombieVillagerEntity.finalizeSpawn((ServerLevelAccessor) zombieVillagerEntity.level, zombieVillagerEntity.level.getCurrentDifficultyAt(zombieVillagerEntity.blockPosition()), MobSpawnType.CONVERSION, new Zombie.ZombieGroupData(false, true), null);
            zombieVillagerEntity.setVillagerData(villagerEntity.getVillagerData());
            zombieVillagerEntity.setGossips(villagerEntity.getGossips().store(NbtOps.INSTANCE).getValue());
            zombieVillagerEntity.setTradeOffers(villagerEntity.getOffers().createTag());
            zombieVillagerEntity.setVillagerXp(villagerEntity.getVillagerXp());
        }
        if(villagerEntity.getLastHurtByMob() != null) villagerEntity.hurt(DamageSource.mobAttack(villagerEntity.getLastHurtByMob()), Float.MAX_VALUE);
        else villagerEntity.hurt(DamageSource.GENERIC, Float.MAX_VALUE);
        entity.level.levelEvent(null, LevelEvent.SOUND_ZOMBIE_INFECTED, entity.blockPosition(), 0);
    }

}
