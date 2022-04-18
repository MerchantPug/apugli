package io.github.merchantpug.apugli.networking;

import io.github.apace100.origins.power.Active;
import io.github.apace100.origins.util.SerializableDataType;
import io.github.merchantpug.apugli.Apugli;
import me.shedaniel.architectury.networking.NetworkManager;
import net.minecraft.network.PacketByteBuf;

import java.util.Arrays;
import java.util.HashSet;

public class ApugliPacketsC2S {
    public static void register() {
        NetworkManager.registerReceiver(NetworkManager.c2s(), ApugliPackets.SYNC_ACTIVE_KEYS, ApugliPacketsC2S::onSyncActiveKeys);
    }

    private static void onSyncActiveKeys(PacketByteBuf packetByteBuf, NetworkManager.PacketContext context) {
        int count = packetByteBuf.readInt();
        Active.Key[] activeKeys = new Active.Key[count];
        for(int i = 0; i < count; i++) {
            activeKeys[i] = SerializableDataType.KEY.receive(packetByteBuf);
        }
        context.queue(() -> {
            if (activeKeys.length == 0) {
                Apugli.currentlyUsedKeys.clear();
            } else {
                Apugli.currentlyUsedKeys = new HashSet<>(Arrays.asList(activeKeys));
            }
        });
    }
}