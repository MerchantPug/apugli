package com.github.merchantpug.apugli.fabric;

import com.github.merchantpug.apugli.Apugli;
import com.github.merchantpug.apugli.ApugliClient;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;

@Environment(EnvType.CLIENT)
public class ApugliFabricClient implements ClientModInitializer {
    public void onInitializeClient() {
        ApugliClient.register();

        ClientPlayConnectionEvents.DISCONNECT.register(((handler, client) -> {
            Apugli.keysToCheck.clear();
            Apugli.currentlyUsedKeys.clear();
            ApugliClient.lastKeyBindingStates.clear();
            ApugliClient.hasClearedKeySync = false;
        }));
    }
}
