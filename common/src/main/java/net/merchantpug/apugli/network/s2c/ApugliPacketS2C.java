package net.merchantpug.apugli.network.s2c;

import net.merchantpug.apugli.network.ApugliPacket;

public interface ApugliPacketS2C extends ApugliPacket {
    void handle();
}
