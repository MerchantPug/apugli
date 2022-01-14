package io.github.merchantpug.apugli.fabric;

import io.github.merchantpug.apugli.ApugliClient;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ApugliFabricClient implements ClientModInitializer {
    public void onInitializeClient() {
        ApugliClient.register();
    }
}
