package io.github.merchantpug.apugli.registry;

import io.github.merchantpug.apugli.networking.packet.EatGrassPacket;
import io.github.merchantpug.apugli.networking.packet.LightUpBlockPacket;
import io.github.merchantpug.apugli.networking.packet.RocketJumpPacket;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public class ApugliPackets {

    public static void register() {
        ServerPlayNetworking.registerGlobalReceiver(EatGrassPacket.ID, EatGrassPacket::handle);
        ServerPlayNetworking.registerGlobalReceiver(LightUpBlockPacket.ID, LightUpBlockPacket::handle);
        ServerPlayNetworking.registerGlobalReceiver(RocketJumpPacket.ID, RocketJumpPacket::handle);
    }
}
