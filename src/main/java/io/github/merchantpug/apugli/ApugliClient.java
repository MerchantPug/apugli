package io.github.merchantpug.apugli;

import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.networking.ModPackets;
import io.github.apace100.apoli.power.Active;
import io.github.merchantpug.apugli.mixin.client.ApoliClientAccessor;
import io.github.merchantpug.apugli.networking.ApugliPackets;
import io.github.merchantpug.apugli.networking.ApugliPacketsC2S;
import io.github.merchantpug.apugli.networking.ApugliPacketsS2C;
import io.github.merchantpug.apugli.registry.condition.ApugliEntityConditions;
import io.github.merchantpug.apugli.util.ApugliClassDataClient;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.Hash;
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
import java.util.function.Predicate;

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
						for(int i = 0; i < client.options.keysAll.length; i++) {
							ApoliClientAccessor.getIdToKeyBindingMap().put(client.options.keysAll[i].getTranslationKey(), client.options.keysAll[i]);
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
					if (!pressedKeys.equals(Apugli.currentlyUsedKeys)) {
						syncActiveKeys(pressedKeys, false);
					}
				} else {
					syncActiveKeys(pressedKeys, !hasClearedKeySync);
				}
			}
		});
	}

	@Environment(EnvType.CLIENT)
	private void syncActiveKeys(HashSet<Active.Key> keys, boolean nothingPressed) {
		if (nothingPressed) {
			PacketByteBuf buffer = new PacketByteBuf(Unpooled.buffer());
			buffer.writeInt(0);
			Apugli.currentlyUsedKeys.clear();
			ClientPlayNetworking.send(ApugliPackets.SYNC_ACTIVE_KEYS, buffer);
			hasClearedKeySync = true;
		} else if (keys.size() > 0) {
			PacketByteBuf buffer = new PacketByteBuf(Unpooled.buffer());
			buffer.writeInt(keys.size());
			for(Active.Key key : keys) {
				ApoliDataTypes.KEY.send(buffer, key);
			}
			Apugli.currentlyUsedKeys = keys;
			ClientPlayNetworking.send(ApugliPackets.SYNC_ACTIVE_KEYS, buffer);
		}
	}
}
