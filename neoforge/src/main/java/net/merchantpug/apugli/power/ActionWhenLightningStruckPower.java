package net.merchantpug.apugli.power;

import com.google.auto.service.AutoService;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import net.merchantpug.apugli.power.configuration.FabricCooldownConfiguration;
import net.merchantpug.apugli.power.factory.ActionWhenLightningStruckPowerFactory;

@AutoService(ActionWhenLightningStruckPowerFactory.class)
public class ActionWhenLightningStruckPower extends AbstractCooldownPower implements ActionWhenLightningStruckPowerFactory<ConfiguredPower<FabricCooldownConfiguration, ?>> {

    public ActionWhenLightningStruckPower() {
        super(ActionWhenLightningStruckPowerFactory.getSerializableData().xmap(
            FabricCooldownConfiguration::new,
            FabricCooldownConfiguration::data
        ).codec());
    }
    
}
