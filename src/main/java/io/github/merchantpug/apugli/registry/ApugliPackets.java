package io.github.merchantpug.apugli.registry;

import io.github.merchantpug.apugli.networking.packet.EatGrassPacket;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public class ApugliPackets {

    public static void register() {
        ServerPlayNetworking.registerGlobalReceiver(EatGrassPacket.ID, EatGrassPacket::handle);
    }
}
