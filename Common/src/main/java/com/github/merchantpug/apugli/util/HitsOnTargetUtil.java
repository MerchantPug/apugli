package com.github.merchantpug.apugli.util;

import the.great.migration.merchantpug.apugli.networking.ApugliPackets;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

public class HitsOnTargetUtil {
    public static void sendPacket(LivingEntity target, @Nullable Entity attacker, HitsOnTargetUtil.PacketType type, int setAmount, int timer) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeByte(type.ordinal());
        buf.writeInt(target.getId());
        buf.writeBoolean(attacker != null);
        if(attacker != null) {
            buf.writeInt(attacker.getId());
        }
        if(type == PacketType.SET) {
            buf.writeInt(setAmount);
            buf.writeInt(timer);
        }

        for(ServerPlayer player : PlayerLookup.tracking(target)) {
            ServerPlayNetworking.send(player, ApugliPackets.SYNC_HITS_ON_TARGET, buf);
        }
        if(!(target instanceof ServerPlayer)) return;
        ServerPlayNetworking.send((ServerPlayer)target, ApugliPackets.SYNC_HITS_ON_TARGET, buf);
    }

    public enum PacketType {
        SET, REMOVE, CLEAR
    }
}
