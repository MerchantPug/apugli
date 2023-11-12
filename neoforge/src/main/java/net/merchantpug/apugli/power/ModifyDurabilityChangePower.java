package net.merchantpug.apugli.power;

import com.google.auto.service.AutoService;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import net.merchantpug.apugli.power.configuration.FabricValueModifyingConfiguration;
import net.merchantpug.apugli.power.factory.ModifyDurabilityChangePowerFactory;

@AutoService(ModifyDurabilityChangePowerFactory.class)
public class ModifyDurabilityChangePower extends AbstractValueModifyingPower implements ModifyDurabilityChangePowerFactory<ConfiguredPower<FabricValueModifyingConfiguration, ?>>  {

    public ModifyDurabilityChangePower() {
        super(ModifyDurabilityChangePowerFactory.getSerializableData().xmap(
                FabricValueModifyingConfiguration::new,
                FabricValueModifyingConfiguration::data
        ).codec());
    }

}
