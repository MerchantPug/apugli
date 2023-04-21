package net.merchantpug.apugli.power;

import com.google.auto.service.AutoService;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import net.merchantpug.apugli.power.configuration.FabricCooldownConfiguration;
import net.merchantpug.apugli.power.factory.ActionOnHarmedPowerFactory;
import net.merchantpug.apugli.power.factory.ActionOnTameHitPowerFactory;
import net.merchantpug.apugli.power.factory.TameHitActionPowerFactory;

@AutoService(ActionOnHarmedPowerFactory.class)
public class ActionOnHarmedPower extends AbstractCooldownPower implements ActionOnHarmedPowerFactory<ConfiguredPower<FabricCooldownConfiguration, ?>> {

    public ActionOnHarmedPower() {
        super(TameHitActionPowerFactory.getSerializableData().xmap(
            FabricCooldownConfiguration::new,
            FabricCooldownConfiguration::data
        ).codec());
    }
    
}
