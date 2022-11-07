package net.merchantpug.apugli;

import net.merchantpug.apugli.mixin.client.ApoliClientAccessor;
import net.merchantpug.apugli.networking.ApugliPackets;
import net.merchantpug.apugli.networking.ApugliPacketsS2C;
import net.merchantpug.apugli.util.ApugliClassDataClient;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.Active;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.texture.ResourceTexture;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;

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

		ClientTickEvents.START_CLIENT_TICK.register(tick -> {
			if (tick.player == null) return;
			ApugliClient.handleActiveKeys(tick.player);
		});
		ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
			Apugli.keysToCheck.clear();
			Apugli.currentlyUsedKeys.clear();
			ApugliClient.lastKeyBindingStates.clear();
			ApugliClient.hasClearedKeySync = false;
		});
	}

	public static void handleActiveKeys(PlayerEntity player) {
		HashSet<Active.Key> pressedKeys = new HashSet<>();
		HashMap<String, Boolean> currentKeyBindingStates = new HashMap<>();
		if (!Apugli.keysToCheck.containsKey(player.getUuid())) return;
		Apugli.keysToCheck.get(player.getUuid()).forEach(key -> {
			if(!ApoliClientAccessor.getInitializedKeyBindingMap()) {
				ApoliClientAccessor.setInitializedKeyBindingMap(true);
				MinecraftClient client = MinecraftClient.getInstance();
				for(int i = 0; i < client.options.allKeys.length; i++) {
					ApoliClientAccessor.getIdToKeyBindingMap().put(client.options.allKeys[i].getTranslationKey(), client.options.allKeys[i]);
				}
			}
			KeyBinding keyBinding = ApoliClientAccessor.getIdToKeyBindingMap().get(key.key);
			if(keyBinding != null) {
				if(!currentKeyBindingStates.containsKey(key.key)) {
					currentKeyBindingStates.put(key.key, keyBinding.isPressed());
				}
				if(!pressedKeys.contains(key) && currentKeyBindingStates.get(key.key) && (key.continuous || !ApugliClient.lastKeyBindingStates.getOrDefault(key.key, false))) {
					pressedKeys.add(key);
					ApugliClient.hasClearedKeySync = false;
				}
			}
		});
		ApugliClient.lastKeyBindingStates = currentKeyBindingStates;
		if (pressedKeys.size() > 0) {
			if (!Apugli.currentlyUsedKeys.getOrDefault(player.getUuid(), new HashSet<>()).equals(pressedKeys)) {
				syncActiveKeys(pressedKeys, false);
			}
		} else {
			syncActiveKeys(pressedKeys, !ApugliClient.hasClearedKeySync);
		}
	}

	@Environment(EnvType.CLIENT)
	private static void syncActiveKeys(HashSet<Active.Key> keys, boolean nothingPressed) {
		if (MinecraftClient.getInstance().player == null) return;
		PacketByteBuf buffer = new PacketByteBuf(Unpooled.buffer());
		if (nothingPressed) {
			buffer.writeInt(0);
			buffer.writeUuid(MinecraftClient.getInstance().player.getUuid());
			ClientPlayNetworking.send(ApugliPackets.SYNC_ACTIVE_KEYS_SERVER, buffer);
			ApugliClient.hasClearedKeySync = true;
		} else if (keys.size() > 0) {
			buffer.writeInt(keys.size());
			buffer.writeUuid(MinecraftClient.getInstance().player.getUuid());
			for(Active.Key key : keys) {
				ApoliDataTypes.KEY.send(buffer, key);
			}
			ClientPlayNetworking.send(ApugliPackets.SYNC_ACTIVE_KEYS_SERVER, buffer);
		}
	}
}
