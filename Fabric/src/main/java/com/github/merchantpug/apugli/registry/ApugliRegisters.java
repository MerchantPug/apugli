package com.github.merchantpug.apugli.registry;

import com.github.merchantpug.apugli.Apugli;
import com.github.merchantpug.apugli.registry.services.RegistrationProvider;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.apoli.registry.ApoliRegistries;

public class ApugliRegisters {
    
    @SuppressWarnings("rawtypes")
    public static final RegistrationProvider<PowerFactory> POWERS = RegistrationProvider.get(ApoliRegistries.POWER_FACTORY, Apugli.ID);
    
}