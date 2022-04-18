package io.github.merchantpug.apugli.networking;

import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.Active;
import io.github.merchantpug.apugli.Apugli;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.HashSet;
import java.util.List;

public class ApugliPacketsC2S {
    public static void register() {
        ServerPlayNetworking.registerGlobalReceiver(ApugliPackets.SYNC_ACTIVE_KEYS_SERVER, ApugliPacketsC2S::onSyncActiveKeys);
    }

    private static void onSyncActiveKeys(MinecraftServer minecraftServer, ServerPlayerEntity playerEntity, ServerPlayNetworkHandler serverPlayNetworkHandler, PacketByteBuf packetByteBuf, PacketSender packetSender) {
        int count = packetByteBuf.readInt();
        int playerId = packetByteBuf.readInt();
        Active.Key[] activeKeys = new Active.Key[count];
        for(int i = 0; i < count; i++) {
            activeKeys[i] = ApoliDataTypes.KEY.receive(packetByteBuf);
        }
        minecraftServer.execute(() -> {
            Entity entity = playerEntity.getWorld().getEntityById(playerId);
            if (!(entity instanceof PlayerEntity playerEntity2)) {
                Apugli.LOGGER.warn("Tried modifying non PlayerEntity's keys pressed.");
                return;
            }
            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            buf.writeInt(activeKeys.length);
            buf.writeInt(entity.getId());
            for(Active.Key key : activeKeys) {
                ApoliDataTypes.KEY.send(buf, key);
            }
            minecraftServer.getPlayerManager().getPlayerList().forEach(player -> {
                ServerPlayNetworking.send(player, ApugliPackets.SYNC_ACTIVE_KEYS_CLIENT, buf);
            });
            if (activeKeys.length == 0) {
                Apugli.currentlyUsedKeys.remove(playerEntity2);
            } else {
                Apugli.currentlyUsedKeys.put(playerEntity2, new HashSet<>(List.of(activeKeys)));
            }
        });
    }
}
