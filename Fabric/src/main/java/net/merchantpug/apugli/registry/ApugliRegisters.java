package net.merchantpug.apugli.registry;

import net.merchantpug.apugli.Apugli;
import net.merchantpug.apugli.registry.services.RegistrationProvider;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.apoli.registry.ApoliRegistries;

public class ApugliRegisters {
    
    @SuppressWarnings("rawtypes")
    public static final RegistrationProvider<PowerFactory> POWERS = RegistrationProvider.get(ApoliRegistries.POWER_FACTORY, Apugli.ID);
    
}