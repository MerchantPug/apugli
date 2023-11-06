package net.merchantpug.apugli.client;

import io.github.apace100.apoli.power.Active;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientLoginConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.merchantpug.apugli.client.renderer.CustomProjectileRenderer;
import net.merchantpug.apugli.client.util.TextureUtilClient;
import net.merchantpug.apugli.component.ApugliEntityComponents;
import net.merchantpug.apugli.component.KeyPressComponent;
import net.merchantpug.apugli.condition.factory.entity.CachedBlockInRadiusCondition;
import net.merchantpug.apugli.mixin.fabric.client.accessor.ApoliClientAccessor;
import net.merchantpug.apugli.network.ApugliPackets;
import net.merchantpug.apugli.network.c2s.UpdateKeysPressedPacket;
import net.merchantpug.apugli.registry.ApugliEntityTypes;
import net.merchantpug.apugli.util.ApugliClassDataClient;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

@Environment(EnvType.CLIENT)
public class ApugliClientFabric implements ClientModInitializer {
	private static HashMap<String, Boolean> lastKeyBindingStates = new HashMap<>();

	@Override
	public void onInitializeClient() {
		ApugliPackets.registerS2C();
		ApugliClassDataClient.registerAll();

		ClientTickEvents.START_CLIENT_TICK.register(ApugliClientFabric::handleActiveKeys);

        EntityRendererRegistry.register(ApugliEntityTypes.CUSTOM_AREA_EFFECT_CLOUD.get(), NoopRenderer::new);
		EntityRendererRegistry.register(ApugliEntityTypes.CUSTOM_PROJECTILE.get(), CustomProjectileRenderer::new);

		ClientLoginConnectionEvents.DISCONNECT.register((handler, client) -> {
			TextureUtilClient.clear();
			CachedBlockInRadiusCondition.clearCache();
		});
	}

	public static void handleActiveKeys(Minecraft minecraft) {
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