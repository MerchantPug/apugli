package net.merchantpug.apugli.power;

import com.google.auto.service.AutoService;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import net.merchantpug.apugli.power.configuration.FabricCooldownConfiguration;
import net.merchantpug.apugli.power.factory.ActionOnAttackerHurtPowerFactory;
import net.merchantpug.apugli.power.factory.TargetHurtActionPowerFactory;

@AutoService(ActionOnAttackerHurtPowerFactory.class)
public class ActionOnAttackerHurtPower extends AbstractCooldownPower implements ActionOnAttackerHurtPowerFactory<ConfiguredPower<FabricCooldownConfiguration, ?>> {

    public ActionOnAttackerHurtPower() {
        super(TargetHurtActionPowerFactory.getSerializableData().xmap(
                FabricCooldownConfiguration::new,
                FabricCooldownConfiguration::data
        ).codec());
    }

}
