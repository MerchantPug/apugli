package the.great.migration.merchantpug.apugli;

import com.github.merchantpug.apugli.mixin.xplatforn.client.accessor.ApoliClientAccessor;
import com.github.merchantpug.apugli.util.ApugliClassDataClient;
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
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import the.great.migration.merchantpug.apugli.networking.ApugliPackets;
import the.great.migration.merchantpug.apugli.networking.ApugliPacketsS2C;

import java.util.HashMap;
import java.util.HashSet;

@Environment(EnvType.CLIENT)
public class ApugliClient implements ClientModInitializer {
	public static HashMap<String, Boolean> lastKeyBindingStates = new HashMap<>();
	public static boolean hasClearedKeySync = false;

	public static boolean isServerRunningApugli = false;

	@Override
	public void onInitializeClient() {
		ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
			client.getTextureManager().register(Apugli.identifier("empty_player_texture"), new SimpleTexture(Apugli.identifier("textures/empty_player_texture.png")));
		});

		ApugliPacketsS2C.register();
		ApugliClassDataClient.registerAll();

		ClientTickEvents.START_CLIENT_TICK.register(tick -> {
			if (tick.player == null) return;
			ApugliClient.handleActiveKeys(tick.player);
		});
		ClientPlayConnectionEvents.DISCONNECT.register(((handler, client) -> {
			Apugli.keysToCheck.clear();
			Apugli.currentlyUsedKeys.clear();
			ApugliClient.lastKeyBindingStates.clear();
			ApugliClient.hasClearedKeySync = false;
		}));
	}

	public static void handleActiveKeys(Player player) {
		HashSet<Active.Key> pressedKeys = new HashSet<>();
		HashMap<String, Boolean> currentKeyBindingStates = new HashMap<>();
		if (!Apugli.keysToCheck.containsKey(player.getUUID())) return;
		Apugli.keysToCheck.get(player.getUUID()).forEach(key -> {
			if(!ApoliClientAccessor.getInitializedKeyBindingMap()) {
				ApoliClientAccessor.setInitializedKeyBindingMap(true);
				Minecraft client = Minecraft.getInstance();
				for(int i = 0; i < client.options.keyMappings.length; i++) {
					ApoliClientAccessor.getIdToKeyBindingMap().put(client.options.keyMappings[i].getName(), client.options.keyMappings[i]);
				}
			}
			KeyMapping keyBinding = ApoliClientAccessor.getIdToKeyBindingMap().get(key.key);
			if(keyBinding != null) {
				if(!currentKeyBindingStates.containsKey(key.key)) {
					currentKeyBindingStates.put(key.key, keyBinding.isDown());
				}
				if(!pressedKeys.contains(key) && currentKeyBindingStates.get(key.key) && (key.continuous || !ApugliClient.lastKeyBindingStates.getOrDefault(key.key, false))) {
					pressedKeys.add(key);
					ApugliClient.hasClearedKeySync = false;
				}
			}
		});
		ApugliClient.lastKeyBindingStates = currentKeyBindingStates;
		if (pressedKeys.size() > 0) {
			if (!Apugli.currentlyUsedKeys.getOrDefault(player.getUUID(), new HashSet<>()).equals(pressedKeys)) {
				syncActiveKeys(pressedKeys, false);
			}
		} else {
			syncActiveKeys(pressedKeys, !ApugliClient.hasClearedKeySync);
		}
	}

	@Environment(EnvType.CLIENT)
	private static void syncActiveKeys(HashSet<Active.Key> keys, boolean nothingPressed) {
		if (Minecraft.getInstance().player == null) return;
		FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
		if (nothingPressed) {
			buffer.writeInt(0);
			buffer.writeUUID(Minecraft.getInstance().player.getUUID());
			ClientPlayNetworking.send(ApugliPackets.SYNC_ACTIVE_KEYS_SERVER, buffer);
			ApugliClient.hasClearedKeySync = true;
		} else if(keys.size() > 0) {
			buffer.writeInt(keys.size());
			buffer.writeUUID(Minecraft.getInstance().player.getUUID());
			for(Active.Key key : keys) {
				ApoliDataTypes.KEY.send(buffer, key);
			}
			ClientPlayNetworking.send(ApugliPackets.SYNC_ACTIVE_KEYS_SERVER, buffer);
		}
	}
}
