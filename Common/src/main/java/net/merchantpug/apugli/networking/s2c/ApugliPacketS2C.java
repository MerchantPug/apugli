package net.merchantpug.apugli.networking.s2c;

import net.merchantpug.apugli.networking.ApugliPacket;

public interface ApugliPacketS2C extends ApugliPacket {
    void handle();
}
