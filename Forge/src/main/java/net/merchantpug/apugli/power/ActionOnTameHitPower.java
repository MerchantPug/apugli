package net.merchantpug.apugli.power;

import com.google.auto.service.AutoService;
import com.mojang.serialization.Codec;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import net.merchantpug.apugli.power.configuration.FabricCooldownConfiguration;
import net.merchantpug.apugli.power.factory.ActionOnTameHitPowerFactory;
import net.merchantpug.apugli.power.factory.ActionWhenTameHitPowerFactory;
import net.merchantpug.apugli.power.factory.TameHitActionPowerFactory;

@AutoService(ActionOnTameHitPowerFactory.class)
public class ActionOnTameHitPower extends AbstractCooldownPower implements ActionOnTameHitPowerFactory<ConfiguredPower<FabricCooldownConfiguration, ?>> {

    public ActionOnTameHitPower() {
        super(TameHitActionPowerFactory.getSerializableData().xmap(
            FabricCooldownConfiguration::new,
            FabricCooldownConfiguration::data
        ).codec());
    }
    
}
