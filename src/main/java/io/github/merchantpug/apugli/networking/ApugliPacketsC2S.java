package io.github.merchantpug.apugli.networking;

import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.Active;
import io.github.merchantpug.apugli.Apugli;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Arrays;
import java.util.HashSet;

public class ApugliPacketsC2S {
    public static void register() {
        ServerPlayNetworking.registerGlobalReceiver(ApugliPackets.SYNC_ACTIVE_KEYS, ApugliPacketsC2S::onSyncActiveKeys);
    }

    private static void onSyncActiveKeys(MinecraftServer minecraftServer, ServerPlayerEntity playerEntity, ServerPlayNetworkHandler serverPlayNetworkHandler, PacketByteBuf packetByteBuf, PacketSender packetSender) {
        int count = packetByteBuf.readInt();
        Active.Key[] activeKeys = new Active.Key[count];
        for(int i = 0; i < count; i++) {
            activeKeys[i] = ApoliDataTypes.KEY.receive(packetByteBuf);
        }
        minecraftServer.execute(() -> {
            if (activeKeys.length == 0) {
                Apugli.currentlyUsedKeys.clear();
            } else {
                Apugli.currentlyUsedKeys = new HashSet<>(Arrays.asList(activeKeys));
            }
        });
    }
}
