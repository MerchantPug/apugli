package com.github.merchantpug.apugli.power;

import com.github.merchantpug.apugli.power.factory.SimplePowerFactory;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.calio.data.SerializableData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class CustomFootstepPower extends CustomSoundPower {
    
    public CustomFootstepPower(PowerType<?> type, LivingEntity living, SerializableData.Instance data) {
        super(type, living, data);
    }
    
    @Override
    protected void playSound(Entity entity, SoundEvent soundEvent, float volume, float pitch) {
        entity.level.playSound(null,
            entity.getX(), entity.getY(), entity.getZ(),
            soundEvent, entity.getSoundSource(),
            volume, pitch
        );
    }
    
    public static class Factory extends SimplePowerFactory<CustomFootstepPower> {
        
        public Factory() {
            super("custom_footstep",
                CustomSoundPower.getSerializableData(),
                data -> (type, entity) -> new CustomFootstepPower(type, entity, data)
            );
            allowCondition();
        }
        
        @Override
        public Class<CustomFootstepPower> getPowerClass() {
            return CustomFootstepPower.class;
        }
        
    }
    
}