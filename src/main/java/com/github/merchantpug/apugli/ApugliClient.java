package com.github.merchantpug.apugli;

import com.github.merchantpug.apugli.networking.ApugliPacketsS2C;
import com.github.merchantpug.apugli.util.ApugliClassDataClient;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;

import java.util.*;

@Environment(EnvType.CLIENT)
public class ApugliClient implements ClientModInitializer {
	public static HashMap<String, Boolean> lastKeyBindingStates = new HashMap<>();
	public static boolean hasClearedKeySync = false;

	public static boolean isServerRunningApugli = false;

	@Override
	public void onInitializeClient() {
		ApugliPacketsS2C.register();
		ApugliClassDataClient.registerAll();

		ClientPlayConnectionEvents.DISCONNECT.register(((handler, client) -> {
			Apugli.keysToCheck.clear();
			Apugli.currentlyUsedKeys.clear();
			ApugliClient.lastKeyBindingStates.clear();
			ApugliClient.hasClearedKeySync = false;
		}));
	}
}
