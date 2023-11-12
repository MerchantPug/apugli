package net.merchantpug.apugli.platform;

import net.merchantpug.apugli.Apugli;
import net.merchantpug.apugli.platform.services.IPreLoadHelper;

import java.util.ServiceLoader;

public class PreLoadService {
    public static final IPreLoadHelper INSTANCE = load(IPreLoadHelper.class);

    public static <T> T load(Class<T> clazz) {

        final T loadedService = ServiceLoader.load(clazz)
                .findFirst()
                .orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
        Apugli.LOG.debug("Loaded {} for service {}", loadedService, clazz);
        return loadedService;
    }

}
