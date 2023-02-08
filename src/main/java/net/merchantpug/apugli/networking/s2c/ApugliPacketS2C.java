package net.merchantpug.apugli.networking.s2c;

import net.merchantpug.apugli.networking.ApugliPacket;
import net.minecraft.client.MinecraftClient;

public interface ApugliPacketS2C extends ApugliPacket {
    void handle(MinecraftClient client);
}
