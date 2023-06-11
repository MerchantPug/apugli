package net.merchantpug.apugli.power;

import net.merchantpug.apugli.power.configuration.FabricResourceConfiguration;
import net.merchantpug.apugli.power.factory.BunnyHopPowerFactory;
import com.google.auto.service.AutoService;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import net.merchantpug.apugli.registry.power.ApugliPowers;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

@AutoService(BunnyHopPowerFactory.class)
public class BunnyHopPower extends AbstractResourcePower implements BunnyHopPowerFactory<ConfiguredPower<FabricResourceConfiguration, ?>> {

    private int framesOnGround = 0;

    public BunnyHopPower() {
        super(BunnyHopPowerFactory.getSerializableData().xmap(
            FabricResourceConfiguration::new,
            FabricResourceConfiguration::data
        ).codec());
        this.ticking();
    }

    @Override
    public void tick(ConfiguredPower<FabricResourceConfiguration, ?> configuration, Entity entity) {
        if (!(entity instanceof LivingEntity living) || !entity.isAlive()) return;
        if (framesOnGround > 4) {
            ApugliPowers.BUNNY_HOP.get().reset(configuration, living);
        }
        if (!canGainResource(living)) {
            if (framesOnGround <= 4) {
                framesOnGround += 1;
            }
        } else {
            this.framesOnGround = 0;
        }
    }
    
}
