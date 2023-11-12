package net.merchantpug.apugli.platform;

import net.merchantpug.apugli.Apugli;
import net.merchantpug.apugli.platform.services.*;

import java.util.ServiceLoader;

public class Services {
    
    public static final IActionHelper ACTION = load(IActionHelper.class);
    public static final IConditionHelper CONDITION = load(IConditionHelper.class);
    public static final IConfigHelper CONFIG = load(IConfigHelper.class);
    public static final IPowerHelper<?> POWER = load(IPowerHelper.class);
    public static final IPlatformHelper PLATFORM = load(IPlatformHelper.class);
    
    public static <T> T load(Class<T> clazz) {

        final T loadedService = ServiceLoader.load(clazz)
                .findFirst()
                .orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
        Apugli.LOG.debug("Loaded {} for service {}", loadedService, clazz);
        return loadedService;
    }
    
}
