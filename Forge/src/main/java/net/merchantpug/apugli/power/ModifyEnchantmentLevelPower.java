package net.merchantpug.apugli.power;

import com.google.auto.service.AutoService;
import com.mojang.serialization.Codec;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import net.merchantpug.apugli.power.configuration.FabricValueModifyingConfiguration;
import net.merchantpug.apugli.power.factory.ModifyEnchantmentLevelPowerFactory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.enchantment.Enchantment;

@AutoService(ModifyEnchantmentLevelPowerFactory.class)
public class ModifyEnchantmentLevelPower extends AbstractValueModifyingPower implements ModifyEnchantmentLevelPowerFactory<ConfiguredPower<FabricValueModifyingConfiguration, ?>> {

    public ModifyEnchantmentLevelPower() {
        super(ModifyEnchantmentLevelPowerFactory.getSerializableData().xmap(
                FabricValueModifyingConfiguration::new,
                FabricValueModifyingConfiguration::data
        ).codec());
    }

    @Override
    public boolean doesApply(ConfiguredPower<FabricValueModifyingConfiguration, ?> power, Entity entity, Enchantment enchantment) {
        return enchantment.equals(power.getConfiguration().data().get("enchantment"));
    }

}
