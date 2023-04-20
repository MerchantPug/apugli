package net.merchantpug.apugli.power.factory;

import net.merchantpug.apugli.platform.Services;

/**
 * A common abstraction of {@link io.github.apace100.apoli.power.factory.PowerFactory} and {@link io.github.edwinmindcraft.apoli.api.power.factory.PowerFactory}. <br>
 * Can be registered and used in the Common project, impls(except for {@link SimplePowerFactory}) should be created in Fabric/Forge projects and use {@link Services} to load. <br>
 * @param <P> The Power type, only used in {@link IPowerFactory#getPowerClass()} for Fabric's end to get powers. <br>
 *           For Forge impls, use {@link io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower}
 */
public interface IPowerFactory<P> {
    
    Class<P> getPowerClass();
    
}
