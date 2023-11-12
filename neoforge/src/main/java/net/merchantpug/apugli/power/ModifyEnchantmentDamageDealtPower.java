package net.merchantpug.apugli.power;

import com.google.auto.service.AutoService;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import net.merchantpug.apugli.power.configuration.FabricValueModifyingConfiguration;
import net.merchantpug.apugli.power.factory.ModifyEnchantmentDamageDealtPowerFactory;
import net.merchantpug.apugli.power.factory.ModifyEnchantmentDamagePowerFactory;

@AutoService(ModifyEnchantmentDamageDealtPowerFactory.class)
public class ModifyEnchantmentDamageDealtPower extends AbstractValueModifyingPower implements ModifyEnchantmentDamageDealtPowerFactory<ConfiguredPower<FabricValueModifyingConfiguration, ?>> {

    public ModifyEnchantmentDamageDealtPower() {
        super(ModifyEnchantmentDamagePowerFactory.getSerializableData().xmap(
                FabricValueModifyingConfiguration::new,
                FabricValueModifyingConfiguration::data
        ).codec());
    }

}
