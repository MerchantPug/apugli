package net.merchantpug.apugli.power.factory;

import io.github.apace100.calio.data.SerializableData;

@Deprecated
public interface AerialAffinityPowerFactory<P> extends SpecialPowerFactory<P> {

    static SerializableData getSerializableData() {
        return new SerializableData();
    }
    
}
