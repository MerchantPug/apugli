package net.merchantpug.apugli.power;

import com.google.auto.service.AutoService;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import net.merchantpug.apugli.power.configuration.FabricCooldownConfiguration;
import net.merchantpug.apugli.power.factory.ActionWhenProjectileHitPowerFactory;
import net.merchantpug.apugli.power.factory.ActionWhenTameHitPowerFactory;
import net.merchantpug.apugli.power.factory.ProjectileHitActionPowerFactory;
import net.merchantpug.apugli.power.factory.TameHitActionPowerFactory;

@AutoService(ActionWhenProjectileHitPowerFactory.class)
public class ActionWhenProjectileHitPower extends AbstractCooldownPower implements ActionWhenProjectileHitPowerFactory<ConfiguredPower<FabricCooldownConfiguration, ?>> {

    public ActionWhenProjectileHitPower() {
        super(ProjectileHitActionPowerFactory.getSerializableData().xmap(
            FabricCooldownConfiguration::new,
            FabricCooldownConfiguration::data
        ).codec());
    }
    
}
