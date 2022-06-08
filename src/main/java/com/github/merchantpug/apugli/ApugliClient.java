package com.github.merchantpug.apugli;

import com.github.merchantpug.apugli.networking.ApugliPackets;
import com.github.merchantpug.apugli.networking.ApugliPacketsS2C;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.Active;
import com.github.merchantpug.apugli.mixin.client.ApoliClientAccessor;
import com.github.merchantpug.apugli.util.ApugliClassDataClient;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.network.PacketByteBuf;

import java.util.*;

@Environment(EnvType.CLIENT)
public class ApugliClient implements ClientModInitializer {
	public static HashSet<Active.Key> keysToCheck = new HashSet<>();
	private static HashMap<String, Boolean> lastKeyBindingStates = new HashMap<>();
	private static boolean hasClearedKeySync = false;

	@Override
	public void onInitializeClient() {
		ApugliPacketsS2C.register();
		ApugliClassDataClient.registerAll();

		ClientPlayConnectionEvents.DISCONNECT.register(((handler, client) -> keysToCheck.clear()));

		ClientTickEvents.START_CLIENT_TICK.register(tick -> {
			if(tick.player != null) {
				HashSet<Active.Key> pressedKeys = new HashSet<>();
				HashMap<String, Boolean> currentKeyBindingStates = new HashMap<>();
				keysToCheck.forEach(key -> {
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
						if(!pressedKeys.contains(key) && currentKeyBindingStates.get(key.key) && (key.continuous || !lastKeyBindingStates.getOrDefault(key.key, false))) {
							pressedKeys.add(key);
							hasClearedKeySync = false;
						}
					}
				});
				lastKeyBindingStates = currentKeyBindingStates;
				if (pressedKeys.size() > 0) {
					if (Apugli.currentlyUsedKeys.get(MinecraftClient.getInstance().player) != pressedKeys) {
						syncActiveKeys(pressedKeys, false);
					}
				} else {
					syncActiveKeys(pressedKeys, !hasClearedKeySync);
				}
			}
		});
	}

	@Environment(EnvType.CLIENT)
	private static void syncActiveKeys(HashSet<Active.Key> keys, boolean nothingPressed) {
		if (MinecraftClient.getInstance().player == null) return;
		PacketByteBuf buffer = new PacketByteBuf(Unpooled.buffer());
		if (nothingPressed) {
			buffer.writeInt(0);
			buffer.writeInt(MinecraftClient.getInstance().player.getId());
			ClientPlayNetworking.send(ApugliPackets.SYNC_ACTIVE_KEYS_SERVER, buffer);
			hasClearedKeySync = true;
		} else if (keys.size() > 0) {
			buffer.writeInt(keys.size());
			buffer.writeInt(MinecraftClient.getInstance().player.getId());
			for(Active.Key key : keys) {
				ApoliDataTypes.KEY.send(buffer, key);
			}
			ClientPlayNetworking.send(ApugliPackets.SYNC_ACTIVE_KEYS_SERVER, buffer);
		}
	}
}
