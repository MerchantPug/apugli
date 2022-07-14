package com.github.merchantpug.apugli.networking;

import io.github.apace100.origins.power.Active;
import io.github.apace100.origins.util.SerializableDataType;
import com.github.merchantpug.apugli.Apugli;
import io.netty.buffer.Unpooled;
import me.shedaniel.architectury.networking.NetworkManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.world.ServerWorld;

import java.util.Arrays;
import java.util.HashSet;
import java.util.UUID;

public class ApugliPacketsC2S {
    public static void register() {
        NetworkManager.registerReceiver(NetworkManager.c2s(), ApugliPackets.SYNC_ACTIVE_KEYS_SERVER, ApugliPacketsC2S::onSyncActiveKeys);
    }

    private static void onSyncActiveKeys(PacketByteBuf packetByteBuf, NetworkManager.PacketContext context) {
        int count = packetByteBuf.readInt();
        UUID playerId = packetByteBuf.readUuid();
        Active.Key[] activeKeys = new Active.Key[count];
        for(int i = 0; i < count; i++) {
            activeKeys[i] = SerializableDataType.KEY.receive(packetByteBuf);
        }
        context.queue(() -> {
            Entity entity = context.getPlayer().world.getPlayerByUuid(playerId);
            if (!(entity instanceof PlayerEntity)) {
                Apugli.LOGGER.warn("Tried modifying non PlayerEntity's keys pressed.");
                return;
            }

            PlayerEntity playerEntity2 = (PlayerEntity)entity;
            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            buf.writeInt(activeKeys.length);
            buf.writeInt(entity.getEntityId());
            for(Active.Key key : activeKeys) {
                SerializableDataType.KEY.send(buf, key);
            }

            NetworkManager.sendToPlayers(((ServerWorld)context.getPlayer().world).getPlayers(), ApugliPackets.SYNC_ACTIVE_KEYS_CLIENT, buf);

            if (activeKeys.length == 0) {
                Apugli.currentlyUsedKeys.remove(playerEntity2.getUuid());
            } else {
                Apugli.currentlyUsedKeys.put(playerEntity2.getUuid(), new HashSet<>(Arrays.asList(activeKeys)));
            }
        });
    }
}