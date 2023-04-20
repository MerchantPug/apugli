package net.merchantpug.apugli.power;

import net.merchantpug.apugli.power.factory.SimplePowerFactory;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.calio.data.SerializableData;
import net.minecraft.world.entity.LivingEntity;

public class AerialAffinityPower extends Power {
    
    public AerialAffinityPower(PowerType<?> type, LivingEntity entity) {
        super(type, entity);
    }
    
    public static class Factory extends SimplePowerFactory<AerialAffinityPower> {
        
        public Factory() {
            super("aerial_affinity", new SerializableData(), data -> AerialAffinityPower::new);
            allowCondition();
        }
    
        @Override
        public Class<AerialAffinityPower> getPowerClass() {
            return AerialAffinityPower.class;
        }
        
    }
    
}
