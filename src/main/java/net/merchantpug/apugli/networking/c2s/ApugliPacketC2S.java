package net.merchantpug.apugli.networking.c2s;

import net.merchantpug.apugli.networking.ApugliPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

public interface ApugliPacketC2S extends ApugliPacket {
    void handle(MinecraftServer server, ServerPlayerEntity player);
}
