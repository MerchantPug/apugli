package net.merchantpug.apugli.power;

import net.merchantpug.apugli.power.configuration.FabricResourceConfiguration;
import net.merchantpug.apugli.power.factory.BunnyHopPowerFactory;
import com.google.auto.service.AutoService;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;

@AutoService(BunnyHopPowerFactory.class)
public class BunnyHopPower extends AbstractResourcePower implements BunnyHopPowerFactory<ConfiguredPower<FabricResourceConfiguration, ?>> {

    public BunnyHopPower() {
        super(BunnyHopPowerFactory.getSerializableData().xmap(
            FabricResourceConfiguration::new,
            FabricResourceConfiguration::data
        ).codec());
    }
    
}
