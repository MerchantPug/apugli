package net.merchantpug.apugli.power;

import com.google.auto.service.AutoService;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import net.merchantpug.apugli.power.configuration.FabricValueModifyingConfiguration;
import net.merchantpug.apugli.power.factory.ModifyEnchantmentDamagePowerFactory;
import net.merchantpug.apugli.power.factory.ModifyEnchantmentDamageTakenPowerFactory;

@AutoService(ModifyEnchantmentDamageTakenPowerFactory.class)
public class ModifyEnchantmentDamageTakenPower extends AbstractValueModifyingPower implements ModifyEnchantmentDamageTakenPowerFactory<ConfiguredPower<FabricValueModifyingConfiguration, ?>> {

    public ModifyEnchantmentDamageTakenPower() {
        super(ModifyEnchantmentDamagePowerFactory.getSerializableData().xmap(
                FabricValueModifyingConfiguration::new,
                FabricValueModifyingConfiguration::data
        ).codec());
    }

}
