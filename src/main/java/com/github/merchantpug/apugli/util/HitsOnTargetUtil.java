package com.github.merchantpug.apugli.util;

import com.github.merchantpug.apugli.networking.ApugliPackets;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

public class HitsOnTargetUtil {
    public static void sendPacket(LivingEntity target, @Nullable LivingEntity attacker, HitsOnTargetUtil.PacketType type, int addAmount) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeByte(type.ordinal());
        buf.writeInt(target.getId());
        if (attacker != null) {
            buf.writeInt(attacker.getId());
        }
        if (type == PacketType.ADD) {
            buf.writeInt(addAmount);
        }

        for (ServerPlayerEntity player : PlayerLookup.tracking(target)) {
            ServerPlayNetworking.send(player, ApugliPackets.SYNC_HITS_ON_TARGET, buf);
        }
        if (!(target instanceof ServerPlayerEntity)) return;
        ServerPlayNetworking.send((ServerPlayerEntity)target, ApugliPackets.SYNC_HITS_ON_TARGET, buf);
    }

    public enum PacketType {
        ADD, REMOVE, CLEAR
    }
}
