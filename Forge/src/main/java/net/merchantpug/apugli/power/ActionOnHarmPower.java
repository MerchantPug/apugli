package net.merchantpug.apugli.power;

import com.google.auto.service.AutoService;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import net.merchantpug.apugli.power.configuration.FabricCooldownConfiguration;
import net.merchantpug.apugli.power.factory.ActionOnHarmPowerFactory;
import net.merchantpug.apugli.power.factory.HarmActionPowerFactory;
import net.merchantpug.apugli.power.factory.TameHitActionPowerFactory;

@AutoService(ActionOnHarmPowerFactory.class)
public class ActionOnHarmPower extends AbstractCooldownPower implements ActionOnHarmPowerFactory<ConfiguredPower<FabricCooldownConfiguration, ?>> {

    public ActionOnHarmPower() {
        super(HarmActionPowerFactory.getSerializableData().xmap(
            FabricCooldownConfiguration::new,
            FabricCooldownConfiguration::data
        ).codec());
    }
    
}
