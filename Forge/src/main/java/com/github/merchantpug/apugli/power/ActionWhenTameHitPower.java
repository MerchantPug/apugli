package com.github.merchantpug.apugli.power;

import com.github.merchantpug.apugli.power.configuration.FabricCooldownConfiguration;
import com.github.merchantpug.apugli.power.factory.ActionWhenTameHitPowerFactory;
import com.google.auto.service.AutoService;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;

@AutoService(ActionWhenTameHitPowerFactory.class)
public class ActionWhenTameHitPower extends AbstractCooldownPower implements ActionWhenTameHitPowerFactory<ConfiguredPower<FabricCooldownConfiguration, ?>> {
    
    public ActionWhenTameHitPower() {
        super(ActionWhenTameHitPowerFactory.getSerializableData().xmap(
            FabricCooldownConfiguration::new,
            FabricCooldownConfiguration::data
        ).codec());
    }
    
}
