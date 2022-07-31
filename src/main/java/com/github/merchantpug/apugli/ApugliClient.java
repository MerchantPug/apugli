package com.github.merchantpug.apugli;

import com.github.merchantpug.apugli.networking.ApugliPacketsS2C;
import com.github.merchantpug.apugli.util.ApugliClassDataClient;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.ResourceTexture;

import java.util.*;

@Environment(EnvType.CLIENT)
public class ApugliClient implements ClientModInitializer {
	public static HashMap<String, Boolean> lastKeyBindingStates = new HashMap<>();
	public static boolean hasClearedKeySync = false;

	public static boolean isServerRunningApugli = false;

	@Override
	public void onInitializeClient() {
		ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
			client.getTextureManager().registerTexture(Apugli.identifier("empty_player_texture"), new ResourceTexture(Apugli.identifier("textures/empty_player_texture.png")));
		});

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
