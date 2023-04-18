package com.github.merchantpug.apugli;

import com.github.merchantpug.apugli.platform.Services;
import com.github.merchantpug.apugli.registry.power.ApugliPowers;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Apugli {
    public static final String ID = "apugli";
    public static final String NAME = "Apugli";
    public static final Logger LOG = LoggerFactory.getLogger(NAME);
    
    public static void init() {
        LOG.info("Hello from Common init on {}! we are currently in a {} environment!", Services.PLATFORM.getPlatformName(), Services.PLATFORM.isDevelopmentEnvironment() ? "development" : "production");
        ApugliPowers.registerAll();
    }
    
    public static ResourceLocation asResource(String name) {
        return new ResourceLocation(ID, name);
    }
    
}