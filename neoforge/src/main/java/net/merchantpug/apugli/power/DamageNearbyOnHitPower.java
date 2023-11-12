package net.merchantpug.apugli.power;

import com.google.auto.service.AutoService;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import net.merchantpug.apugli.power.configuration.FabricCooldownConfiguration;
import net.merchantpug.apugli.power.factory.DamageNearbyOnHitPowerFactory;

@AutoService(DamageNearbyOnHitPowerFactory.class)
public class DamageNearbyOnHitPower extends AbstractCooldownPower implements DamageNearbyOnHitPowerFactory<ConfiguredPower<FabricCooldownConfiguration, ?>> {

    public DamageNearbyOnHitPower() {
        super(DamageNearbyOnHitPowerFactory.getSerializableData().xmap(
            FabricCooldownConfiguration::new,
            FabricCooldownConfiguration::data
        ).codec());
    }
    
}
