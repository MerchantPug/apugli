package com.github.merchantpug.apugli.power.factory;

import com.github.merchantpug.apugli.platform.Services;
import io.github.apace100.calio.data.SerializableData;

/**
 * An impl of {@link IPowerFactory} which needs to be load through {@link SerializableData} and directly uses {@link SerializableData.Instance} to achieve Xplatform compatibility.
 * @param <P> Same with {@link IPowerFactory}.
 */
public interface SpecialPowerFactory<P> extends IPowerFactory<P> {
    
    SerializableData.Instance getDataFromPower(P power);
    
    default Class<P> getPowerClass() {
        switch(Services.PLATFORM.getPlatformName()) {
            case "Forge" -> throw new RuntimeException("Unexpected call to IPowerFactory#getPowerClass() on platform Forge");
            case "Fabric" -> throw new RuntimeException(String.format("%s doesn't implement IPowerFactory#getPowerClass()", this.getClass()));
            default -> throw new RuntimeException("Unexpected Platform");
        }
    }
    
}
