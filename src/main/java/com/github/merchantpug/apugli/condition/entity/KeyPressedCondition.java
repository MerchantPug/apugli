/*
MIT License

Copyright (c) 2021 apace100

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */

package com.github.merchantpug.apugli.condition.entity;

import com.github.merchantpug.apugli.ApugliClient;
import com.github.merchantpug.apugli.mixin.client.ApoliClientAccessor;
import com.github.merchantpug.apugli.networking.ApugliPackets;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.Active;
import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.calio.data.SerializableData;
import com.github.merchantpug.apugli.Apugli;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;

import java.util.HashMap;
import java.util.HashSet;

public class KeyPressedCondition {
    public static boolean condition(SerializableData.Instance data, Entity entity) {
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

    public static ConditionFactory<Entity> getFactory() {
        return new ConditionFactory<>(Apugli.identifier("key_pressed"), new SerializableData()
                .add("key", ApoliDataTypes.KEY),
                KeyPressedCondition::condition
        );
    }
}
