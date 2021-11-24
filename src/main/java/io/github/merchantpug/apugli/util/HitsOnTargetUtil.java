package io.github.merchantpug.apugli.util;

import io.github.merchantpug.apugli.access.LivingEntityAccess;
import io.github.merchantpug.apugli.networking.ApugliPackets;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

public class HitsOnTargetUtil {
    public static void sendPacket(LivingEntity target, LivingEntity attacker, HitsOnTargetUtil.PacketType type, int setAmount) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeInt(target.getId());
        buf.writeInt(attacker.getId());
        buf.writeByte(type.ordinal());
        if (type == PacketType.SET) {
            buf.writeInt(setAmount);
        }

        for (ServerPlayerEntity player : PlayerLookup.tracking(target)) {
            ServerPlayNetworking.send(player, ApugliPackets.SYNC_HITS_ON_TARGET, buf);
        }
        if (!(target instanceof ServerPlayerEntity)) return;
        ServerPlayNetworking.send((ServerPlayerEntity)target, ApugliPackets.SYNC_HITS_ON_TARGET, buf);
    }

    public enum PacketType {
        SET, REMOVE;
    }
}
