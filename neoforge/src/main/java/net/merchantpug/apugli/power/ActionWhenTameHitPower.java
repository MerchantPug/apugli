package net.merchantpug.apugli.power;

import net.merchantpug.apugli.power.configuration.FabricCooldownConfiguration;
import net.merchantpug.apugli.power.factory.ActionWhenTameHitPowerFactory;
import net.merchantpug.apugli.power.factory.TameHitActionPowerFactory;
import com.google.auto.service.AutoService;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;

@AutoService(ActionWhenTameHitPowerFactory.class)
public class ActionWhenTameHitPower extends AbstractCooldownPower implements ActionWhenTameHitPowerFactory<ConfiguredPower<FabricCooldownConfiguration, ?>> {

    public ActionWhenTameHitPower() {
        super(TameHitActionPowerFactory.getSerializableData().xmap(
            FabricCooldownConfiguration::new,
            FabricCooldownConfiguration::data
        ).codec());
    }
    
}
