package io.github.merchantpug.apugli;

import io.github.merchantpug.apugli.networking.ApugliPacketsS2C;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class ApugliClient {
    @Environment(EnvType.CLIENT)
    public static void register() {
        ApugliPacketsS2C.register();
    }
}
