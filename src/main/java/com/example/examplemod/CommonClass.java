package com.github.merchantpug.apugli;

import com.github.merchantpug.apugli.platform.Services;
import com.github.merchantpug.apugli.registry.ExamplePowerFactories;

public class CommonClass {

    // This method serves as an initialization hook for the mod. The vanilla
    // game has no mechanism to load tooltip listeners so this must be
    // invoked from a mod loader specific project like Forge or Fabric.
    public static void init() {
        Constants.LOG.info("Hello from Common init on {}! we are currently in a {} environment!", Services.PLATFORM.getPlatformName(), Services.PLATFORM.isDevelopmentEnvironment() ? "development" : "production");
        ExamplePowerFactories.init();
    }
}