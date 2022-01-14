package io.github.merchantpug.apugli;

import io.github.merchantpug.apugli.registry.PowerFactories;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Apugli {
    public static final String MOD_ID = "apugli";
    public static final Logger LOGGER = LogManager.getLogger(Apugli.class);
    
    public static void init() {
        PowerFactories.register();
    }

    public static Identifier identifier(String path) {
        return new Identifier(MOD_ID, path);
    }
}
