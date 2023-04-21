package net.merchantpug.apugli.power;

import com.google.auto.service.AutoService;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import net.merchantpug.apugli.power.configuration.FabricCooldownConfiguration;
import net.merchantpug.apugli.power.factory.ActionOnHarmedPowerFactory;
import net.merchantpug.apugli.power.factory.ActionWhenHarmedPowerFactory;
import net.merchantpug.apugli.power.factory.TameHitActionPowerFactory;

@AutoService(ActionWhenHarmedPowerFactory.class)
public class ActionWhenHarmedPower extends AbstractCooldownPower implements ActionWhenHarmedPowerFactory<ConfiguredPower<FabricCooldownConfiguration, ?>> {

    public ActionWhenHarmedPower() {
        super(TameHitActionPowerFactory.getSerializableData().xmap(
            FabricCooldownConfiguration::new,
            FabricCooldownConfiguration::data
        ).codec());
    }
    
}
