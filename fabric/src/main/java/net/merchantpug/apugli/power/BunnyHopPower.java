package net.merchantpug.apugli.power;

import net.merchantpug.apugli.power.factory.BunnyHopPowerFactory;
import com.google.auto.service.AutoService;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.calio.data.SerializableData;
import net.merchantpug.apugli.registry.power.ApugliPowers;
import net.minecraft.world.entity.LivingEntity;

@AutoService(BunnyHopPowerFactory.class)
public class BunnyHopPower extends AbstractResourcePower<BunnyHopPower.Instance> implements BunnyHopPowerFactory<BunnyHopPower.Instance> {
    
    public BunnyHopPower() {
        super("bunny_hop", BunnyHopPowerFactory.getSerializableData(),
            data -> (type, entity) -> new Instance(type, entity, data));
        allowCondition();
    }
    
    @Override
    public Class<Instance> getPowerClass() {
        return Instance.class;
    }
    
    public static class Instance extends AbstractResourcePower.Instance {

        private int framesOnGround = 0;

        public Instance(PowerType<?> type, LivingEntity entity, SerializableData.Instance data) {
            super(type, entity, data);
            this.setTicking();
        }

        @Override
        public void tick() {
            if (!entity.isAlive()) return;
            if (framesOnGround > 4) {
                ApugliPowers.BUNNY_HOP.get().reset(this, entity);
            }
            if (!ApugliPowers.BUNNY_HOP.get().canGainResource(entity)) {
                if (framesOnGround <= 4) {
                    framesOnGround += 1;
                }
            } else {
                this.framesOnGround = 0;
            }
        }
        
    }
    
}
