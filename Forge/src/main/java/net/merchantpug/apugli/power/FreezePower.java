package net.merchantpug.apugli.power;

import com.google.auto.service.AutoService;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import net.merchantpug.apugli.power.configuration.FabricValueModifyingConfiguration;
import net.merchantpug.apugli.power.factory.FreezePowerFactory;
import net.merchantpug.apugli.power.factory.ModifyDurabilityChangePowerFactory;

@AutoService(FreezePowerFactory.class)
public class FreezePower extends AbstractValueModifyingPower implements FreezePowerFactory<ConfiguredPower<FabricValueModifyingConfiguration, ?>>  {

    public FreezePower() {
        super(ModifyDurabilityChangePowerFactory.getSerializableData().xmap(
                FabricValueModifyingConfiguration::new,
                FabricValueModifyingConfiguration::data
        ).codec());
    }

}
