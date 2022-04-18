package io.github.merchantpug.apugli;

import io.github.apace100.origins.power.Active;
import io.github.apace100.origins.util.SerializableDataType;
import io.github.merchantpug.apugli.mixin.client.OriginsClientAccessor;
import io.github.merchantpug.apugli.networking.ApugliPackets;
import io.github.merchantpug.apugli.networking.ApugliPacketsS2C;
import io.github.merchantpug.apugli.registry.KeybindRegistry;
import io.github.merchantpug.apugli.util.ApugliClientConfig;
import io.netty.buffer.Unpooled;
import me.shedaniel.architectury.event.events.client.ClientTickEvent;
import me.shedaniel.architectury.networking.NetworkManager;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.network.PacketByteBuf;

import java.util.HashMap;
import java.util.HashSet;

public class ApugliClient {
    public static ApugliClientConfig config;
    public static HashSet<Active.Key> keysToCheck = new HashSet<>();
    private static HashMap<String, Boolean> lastKeyBindingStates = new HashMap<>();
    private static boolean hasClearedKeySync = false;

    @Environment(EnvType.CLIENT)
    public static void register() {
        ApugliPacketsS2C.register();

        AutoConfig.register(ApugliClientConfig.class, JanksonConfigSerializer::new);
        ApugliClient.config = AutoConfig.getConfigHolder(ApugliClientConfig.class).getConfig();

        KeybindRegistry.register();

        ClientTickEvent.CLIENT_PRE.register(tick -> {
            if(tick.player != null) {
                HashSet<Active.Key> pressedKeys = new HashSet<>();
                HashMap<String, Boolean> currentKeyBindingStates = new HashMap<>();
                keysToCheck.forEach(key -> {
                    if(!OriginsClientAccessor.getInitializedKeyBindingMap()) {
                        OriginsClientAccessor.setInitializedKeyBindingMap(true);
                        MinecraftClient client = MinecraftClient.getInstance();
                        for(int i = 0; i < client.options.keysAll.length; i++) {
                            OriginsClientAccessor.getIdToKeyBindingMap().put(client.options.keysAll[i].getTranslationKey(), client.options.keysAll[i]);
                        }
                    }
                    KeyBinding keyBinding = OriginsClientAccessor.getIdToKeyBindingMap().get(key.key);
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
    private static void syncActiveKeys(HashSet<Active.Key> keys, boolean nothingPressed) {
        if (MinecraftClient.getInstance().player == null) return;
        PacketByteBuf buffer = new PacketByteBuf(Unpooled.buffer());
        if (nothingPressed) {
            buffer.writeInt(0);
            buffer.writeInt(MinecraftClient.getInstance().player.getEntityId());
            NetworkManager.sendToServer(ApugliPackets.SYNC_ACTIVE_KEYS_SERVER, buffer);
            hasClearedKeySync = true;
        } else if (keys.size() > 0) {
            buffer.writeInt(keys.size());
            buffer.writeInt(MinecraftClient.getInstance().player.getEntityId());
            for(Active.Key key : keys) {
                SerializableDataType.KEY.send(buffer, key);
            }

            NetworkManager.sendToServer(ApugliPackets.SYNC_ACTIVE_KEYS_SERVER, buffer);
        }
    }
}
