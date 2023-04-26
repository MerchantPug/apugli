package net.merchantpug.apugli.power;

import com.google.auto.service.AutoService;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import net.merchantpug.apugli.power.configuration.FabricCooldownConfiguration;
import net.merchantpug.apugli.power.factory.ActionOnProjectileHitPowerFactory;
import net.merchantpug.apugli.power.factory.ActionWhenProjectileHitPowerFactory;
import net.merchantpug.apugli.power.factory.ProjectileHitActionPowerFactory;

@AutoService(ActionOnProjectileHitPowerFactory.class)
public class ActionOnProjectileHitPower extends AbstractCooldownPower implements ActionOnProjectileHitPowerFactory<ConfiguredPower<FabricCooldownConfiguration, ?>> {

    public ActionOnProjectileHitPower() {
        super(ProjectileHitActionPowerFactory.getSerializableData().xmap(
            FabricCooldownConfiguration::new,
            FabricCooldownConfiguration::data
        ).codec());
    }
    
}
