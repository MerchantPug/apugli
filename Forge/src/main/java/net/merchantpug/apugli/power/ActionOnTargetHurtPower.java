package net.merchantpug.apugli.power;

import com.google.auto.service.AutoService;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import net.merchantpug.apugli.power.configuration.FabricCooldownConfiguration;
import net.merchantpug.apugli.power.factory.ActionOnTargetHurtPowerFactory;
import net.merchantpug.apugli.power.factory.TargetHurtActionPowerFactory;

@AutoService(ActionOnTargetHurtPowerFactory.class)
public class ActionOnTargetHurtPower extends AbstractCooldownPower implements ActionOnTargetHurtPowerFactory<ConfiguredPower<FabricCooldownConfiguration, ?>> {

    public ActionOnTargetHurtPower() {
        super(TargetHurtActionPowerFactory.getSerializableData().xmap(
                FabricCooldownConfiguration::new,
                FabricCooldownConfiguration::data
        ).codec());
    }

}