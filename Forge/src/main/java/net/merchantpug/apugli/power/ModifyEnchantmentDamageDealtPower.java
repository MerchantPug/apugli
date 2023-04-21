package net.merchantpug.apugli.power;

import com.google.auto.service.AutoService;
import com.mojang.serialization.Codec;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import net.merchantpug.apugli.power.configuration.FabricValueModifyingConfiguration;
import net.merchantpug.apugli.power.factory.ModifyBreedingCooldownPowerFactory;
import net.merchantpug.apugli.power.factory.ModifyEnchantmentDamageDealtPowerFactory;

@AutoService(ModifyEnchantmentDamageDealtPowerFactory.class)
public class ModifyEnchantmentDamageDealtPower extends AbstractValueModifyingPower implements ModifyEnchantmentDamageDealtPowerFactory<ConfiguredPower<FabricValueModifyingConfiguration, ?>> {

    public ModifyEnchantmentDamageDealtPower() {
        super(ModifyBreedingCooldownPowerFactory.getSerializableData().xmap(
                FabricValueModifyingConfiguration::new,
                FabricValueModifyingConfiguration::data
        ).codec());
    }

}
