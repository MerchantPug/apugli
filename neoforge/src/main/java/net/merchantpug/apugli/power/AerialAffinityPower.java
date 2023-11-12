package net.merchantpug.apugli.power;

import com.google.auto.service.AutoService;
import io.github.apace100.calio.data.SerializableData;
import io.github.edwinmindcraft.apoli.api.configuration.NoConfiguration;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import io.github.edwinmindcraft.apoli.common.power.DummyPower;
import net.merchantpug.apugli.power.factory.AerialAffinityPowerFactory;

@Deprecated
@AutoService(AerialAffinityPowerFactory.class)
public class AerialAffinityPower extends DummyPower implements AerialAffinityPowerFactory<ConfiguredPower<NoConfiguration, ?>> {

    @Override
    public SerializableData.Instance getDataFromPower(ConfiguredPower<NoConfiguration, ?> power) {
        return AerialAffinityPowerFactory.getSerializableData().new Instance();
    }

}
