package com.github.merchantpug.apugli.condition.entity;

import com.github.merchantpug.apugli.Apugli;
import com.github.merchantpug.apugli.ApugliClient;
import com.github.merchantpug.apugli.mixin.client.OriginsClientAccessor;
import com.github.merchantpug.apugli.networking.ApugliPackets;
import io.github.apace100.origins.power.Active;
import io.github.apace100.origins.power.factory.condition.ConditionFactory;
import io.github.apace100.origins.util.SerializableData;
import io.github.apace100.origins.util.SerializableDataType;
import io.netty.buffer.Unpooled;
import me.shedaniel.architectury.networking.NetworkManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;

import java.util.HashMap;
import java.util.HashSet;

public class KeyPressedCondition {
    public static boolean condition(SerializableData.Instance data, LivingEntity entity) {
        if (!Apugli.keysToCheck.containsKey(entity.getUuid())) {
            Apugli.keysToCheck.put(entity.getUuid(), new HashSet<>());
        }
        if (Apugli.keysToCheck.get(entity.getUuid()).stream().noneMatch(key -> key.equals(data.get("key")))) {
            Apugli.keysToCheck.get(entity.getUuid()).add(data.get("key"));
        }
        if (entity instanceof PlayerEntity) {
            if (entity.world.isClient) {
                handleActiveKeys((PlayerEntity)entity);
            }
            if (Apugli.currentlyUsedKeys.containsKey(entity.getUuid())) {
                return Apugli.currentlyUsedKeys.get(entity.getUuid()).stream().anyMatch(key -> key.equals(data.get("key")));
            }
        }
        return false;
    }

    @Environment(EnvType.CLIENT)
    private static void handleActiveKeys(PlayerEntity player) {
        if(player != null) {
            HashSet<Active.Key> pressedKeys = new HashSet<>();
            HashMap<String, Boolean> currentKeyBindingStates = new HashMap<>();
            if (!Apugli.keysToCheck.containsKey(player.getUuid())) return;
            Apugli.keysToCheck.get(player.getUuid()).forEach(key -> {
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
    }

    @Environment(EnvType.CLIENT)
    private static void syncActiveKeys(HashSet<Active.Key> keys, boolean nothingPressed) {
        if (MinecraftClient.getInstance().player == null) return;
        PacketByteBuf buffer = new PacketByteBuf(Unpooled.buffer());
        if (nothingPressed) {
            buffer.writeInt(0);
            buffer.writeUuid(MinecraftClient.getInstance().player.getUuid());
            NetworkManager.sendToServer(ApugliPackets.SYNC_ACTIVE_KEYS_SERVER, buffer);
            ApugliClient.hasClearedKeySync = true;
        } else if (keys.size() > 0) {
            buffer.writeInt(keys.size());
            buffer.writeUuid(MinecraftClient.getInstance().player.getUuid());
            for(Active.Key key : keys) {
                SerializableDataType.KEY.send(buffer, key);
            }
            NetworkManager.sendToServer(ApugliPackets.SYNC_ACTIVE_KEYS_SERVER, buffer);
        }
    }

    public static ConditionFactory<LivingEntity> getFactory() {
        return new ConditionFactory<>(Apugli.identifier("key_pressed"), new SerializableData()
                .add("key", SerializableDataType.KEY),
                KeyPressedCondition::condition
        );
    }
}
