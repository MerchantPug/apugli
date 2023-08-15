package net.merchantpug.apugli.power;

import com.google.auto.service.AutoService;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import net.merchantpug.apugli.power.configuration.FabricCooldownConfiguration;
import net.merchantpug.apugli.power.factory.DamageNearbyWhenHitPowerFactory;

@AutoService(DamageNearbyWhenHitPowerFactory.class)
public class DamageNearbyWhenHitPower extends AbstractCooldownPower implements DamageNearbyWhenHitPowerFactory<ConfiguredPower<FabricCooldownConfiguration, ?>> {

    public DamageNearbyWhenHitPower() {
        super(DamageNearbyWhenHitPowerFactory.getSerializableData().xmap(
            FabricCooldownConfiguration::new,
            FabricCooldownConfiguration::data
        ).codec());
    }
    
}
