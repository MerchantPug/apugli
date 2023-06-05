package net.merchantpug.apugli.client;

import io.github.apace100.apoli.power.Active;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientLoginConnectionEvents;
import net.merchantpug.apugli.component.ApugliEntityComponents;
import net.merchantpug.apugli.component.KeyPressComponent;
import net.merchantpug.apugli.condition.factory.entity.CachedBlockInRadiusCondition;
import net.merchantpug.apugli.mixin.fabric.client.accessor.ApoliClientAccessor;
import net.merchantpug.apugli.networking.ApugliPackets;
import net.merchantpug.apugli.networking.c2s.UpdateKeysPressedPacket;
import net.merchantpug.apugli.util.ApugliClassDataClient;
import net.merchantpug.apugli.client.util.TextureUtilClient;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

@Environment(EnvType.CLIENT)
public class ApugliClientFabric implements ClientModInitializer {
	private static HashMap<String, Boolean> lastKeyBindingStates = new HashMap<>();
	public static boolean isServerRunningApugli = false;

	@Override
	public void onInitializeClient() {
		ApugliPackets.registerS2C();
		ApugliClassDataClient.registerAll();

		ClientTickEvents.START_CLIENT_TICK.register(tick -> ApugliClientFabric.handleActiveKeys());

		ClientLoginConnectionEvents.DISCONNECT.register((handler, client) -> TextureUtilClient.clear());

		ClientLoginConnectionEvents.DISCONNECT.register((handler, client) -> {
			CachedBlockInRadiusCondition.clearCache();
		});

		ClientChunkEvents.CHUNK_UNLOAD.register((world, chunk) -> CachedBlockInRadiusCondition.invalidateChunk(chunk));
	}

	public static void handleActiveKeys() {
		Player player = Minecraft.getInstance().player;
		if (player == null) return;
		Set<Active.Key> addedKeys = new HashSet<>();
		Set<Active.Key> removedKeys = new HashSet<>();
		HashMap<String, Boolean> currentKeyBindingStates = new HashMap<>();
		KeyPressComponent component = ApugliEntityComponents.KEY_PRESS_COMPONENT.get(player);
		if (!component.getKeysToCheck().isEmpty()) {
			if (!ApoliClientAccessor.getInitializedKeyBindingMap()) {
				ApoliClientAccessor.setInitializedKeyBindingMap(true);
				Minecraft client = Minecraft.getInstance();
				for (int i = 0; i < client.options.keyMappings.length; i++) {
					ApoliClientAccessor.getIdToKeyBindingMap().put(client.options.keyMappings[i].getName(), client.options.keyMappings[i]);
				}
			}
			component.getKeysToCheck().forEach(key -> {
				KeyMapping keyBinding = ApoliClientAccessor.getIdToKeyBindingMap().get(key.key);
				if (keyBinding != null) {
					if (!currentKeyBindingStates.containsKey(key.key)) {
						currentKeyBindingStates.put(key.key, keyBinding.isDown());
					}
					if (currentKeyBindingStates.get(key.key) && (key.continuous || !lastKeyBindingStates.getOrDefault(key.key, false))) {
						component.addKey(key);
						if (!lastKeyBindingStates.getOrDefault(key.key, false)) {
							addedKeys.add(key);
						}
					} else if ((!currentKeyBindingStates.get(key.key) || !key.continuous) && lastKeyBindingStates.getOrDefault(key.key, false)) {
						component.removeKey(key);
						removedKeys.add(key);
					}
				}
			});
			ApugliPackets.sendC2S(new UpdateKeysPressedPacket(addedKeys, removedKeys));
		}
		lastKeyBindingStates = currentKeyBindingStates;
	}
}