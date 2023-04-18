package com.github.merchantpug.apugli.power;

import com.github.merchantpug.apugli.power.configuration.FabricResourceConfiguration;
import com.github.merchantpug.apugli.power.factory.BunnyHopPowerFactory;
import com.google.auto.service.AutoService;
import com.mojang.serialization.Codec;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;

@AutoService(BunnyHopPowerFactory.class)
public class BunnyHopPower extends AbstractResourcePower implements BunnyHopPowerFactory<ConfiguredPower<FabricResourceConfiguration, ?>> {
    
    public BunnyHopPower(Codec<FabricResourceConfiguration> codec) {
        super(BunnyHopPowerFactory.getSerializableData().xmap(
            FabricResourceConfiguration::new,
            FabricResourceConfiguration::data
        ).codec());
    }
    
}
