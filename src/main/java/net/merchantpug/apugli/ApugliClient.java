package net.merchantpug.apugli;

import net.merchantpug.apugli.component.ApugliEntityComponents;
import net.merchantpug.apugli.component.KeyPressComponent;
import net.merchantpug.apugli.mixin.client.ApoliClientAccessor;
import net.merchantpug.apugli.networking.ApugliPackets;
import net.merchantpug.apugli.networking.c2s.UpdateKeysPressedPacket;
import net.merchantpug.apugli.util.ApugliClassDataClient;
import io.github.apace100.apoli.power.Active;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.entity.player.PlayerEntity;

import java.util.*;

@Environment(EnvType.CLIENT)
public class ApugliClient implements ClientModInitializer {
	private static HashMap<String, Boolean> lastKeyBindingStates = new HashMap<>();
	public static boolean isServerRunningApugli = false;

	@Override
	public void onInitializeClient() {
		ApugliPackets.registerS2C();
		ApugliClassDataClient.registerAll();

		ClientTickEvents.START_CLIENT_TICK.register(tick -> {
			if (tick.player == null) return;
			ApugliClient.handleActiveKeys(tick.player);
		});
	}

	public static void handleActiveKeys(PlayerEntity player) {
		Set<Active.Key> addedKeys = new HashSet<>();
		Set<Active.Key> removedKeys = new HashSet<>();
		HashMap<String, Boolean> currentKeyBindingStates = new HashMap<>();
		KeyPressComponent component = ApugliEntityComponents.KEY_PRESS_COMPONENT.get(player);
		if (!component.getKeysToCheck().isEmpty()) {
			if (!ApoliClientAccessor.getInitializedKeyBindingMap()) {
				ApoliClientAccessor.setInitializedKeyBindingMap(true);
				MinecraftClient client = MinecraftClient.getInstance();
				for (int i = 0; i < client.options.allKeys.length; i++) {
					ApoliClientAccessor.getIdToKeyBindingMap().put(client.options.allKeys[i].getTranslationKey(), client.options.allKeys[i]);
				}
			}
			component.getKeysToCheck().forEach(key -> {
				KeyBinding keyBinding = ApoliClientAccessor.getIdToKeyBindingMap().get(key.key);
				if (keyBinding != null) {
					if (!currentKeyBindingStates.containsKey(key.key)) {
						currentKeyBindingStates.put(key.key, keyBinding.isPressed());
					}
					if (currentKeyBindingStates.get(key.key) && (key.continuous || !lastKeyBindingStates.getOrDefault(key.key, false))) {
						ApugliEntityComponents.KEY_PRESS_COMPONENT.get(player).addKey(key);
						if (!lastKeyBindingStates.getOrDefault(key.key, false)) {
							addedKeys.add(key);
						}
					} else if ((!currentKeyBindingStates.get(key.key) || !key.continuous) && lastKeyBindingStates.getOrDefault(key.key, false)) {
						ApugliEntityComponents.KEY_PRESS_COMPONENT.get(player).removeKey(key);
						removedKeys.add(key);
					}
				}
			});
			ApugliPackets.sendC2SPacket(new UpdateKeysPressedPacket(addedKeys, removedKeys));
		}
		lastKeyBindingStates = currentKeyBindingStates;
	}
}
