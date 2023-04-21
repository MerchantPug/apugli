package net.merchantpug.apugli.power;

import com.google.auto.service.AutoService;
import com.mojang.serialization.Codec;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import net.merchantpug.apugli.power.configuration.FabricValueModifyingConfiguration;
import net.merchantpug.apugli.power.factory.ModifyBreedingCooldownPowerFactory;
import net.merchantpug.apugli.power.factory.ModifyEnchantmentLevelPowerFactory;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.function.Predicate;

@AutoService(ModifyBreedingCooldownPowerFactory.class)
public class ModifyBreedingCooldownPower extends AbstractValueModifyingPower implements ModifyBreedingCooldownPowerFactory<ConfiguredPower<FabricValueModifyingConfiguration, ?>> {

    public ModifyBreedingCooldownPower() {
        super(ModifyBreedingCooldownPowerFactory.getSerializableData().xmap(
                FabricValueModifyingConfiguration::new,
                FabricValueModifyingConfiguration::data
        ).codec());
    }

}
