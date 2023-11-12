package net.merchantpug.apugli.power;

import com.google.auto.service.AutoService;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import net.merchantpug.apugli.power.configuration.FabricValueModifyingConfiguration;
import net.merchantpug.apugli.power.factory.ModifySoulSpeedPowerFactory;

@AutoService(ModifySoulSpeedPowerFactory.class)
public class ModifySoulSpeedPower extends AbstractValueModifyingPower implements ModifySoulSpeedPowerFactory<ConfiguredPower<FabricValueModifyingConfiguration, ?>> {

    public ModifySoulSpeedPower() {
        super(ModifySoulSpeedPowerFactory.getSerializableData().xmap(
                FabricValueModifyingConfiguration::new,
                FabricValueModifyingConfiguration::data
        ).codec());
    }

}
