package net.merchantpug.apugli.power;

import net.merchantpug.apugli.power.configuration.FabricCooldownConfiguration;
import net.merchantpug.apugli.power.factory.ActionOnTargetDeathPowerFactory;
import com.google.auto.service.AutoService;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;

@AutoService(ActionOnTargetDeathPowerFactory.class)
public class ActionOnTargetDeathPower extends AbstractCooldownPower implements ActionOnTargetDeathPowerFactory<ConfiguredPower<FabricCooldownConfiguration, ?>> {

    public ActionOnTargetDeathPower() {
        super(ActionOnTargetDeathPowerFactory.getSerializableData().xmap(
            FabricCooldownConfiguration::new,
            FabricCooldownConfiguration::data
        ).codec());
    }
    
}
