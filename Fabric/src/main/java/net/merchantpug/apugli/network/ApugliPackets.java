/*
MIT License

Copyright (c) 2021 apace100

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */

package net.merchantpug.apugli.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.merchantpug.apugli.network.c2s.ApugliPacketC2S;
import net.merchantpug.apugli.network.c2s.ExecuteBiEntityActionServerPacket;
import net.merchantpug.apugli.network.c2s.ExecuteEntityActionServerPacket;
import net.merchantpug.apugli.network.c2s.UpdateKeysPressedPacket;
import net.merchantpug.apugli.network.s2c.*;
import net.merchantpug.apugli.network.s2c.integration.pehkui.ClearScaleModifierCachePacket;
import net.merchantpug.apugli.network.s2c.integration.pehkui.SyncScalePacket;
import net.merchantpug.apugli.network.s2c.integration.pehkui.UpdateScaleDataPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.apache.logging.log4j.util.TriConsumer;

import java.util.function.Consumer;
import java.util.function.Function;

public class ApugliPackets {

    public static void registerS2C() {
        ClientPlayConnectionEvents.INIT.register((clientPlayNetworkHandler, minecraftClient) -> {
            ClientPlayNetworking.registerReceiver(SendParticlesPacket.ID, createS2CHandler(SendParticlesPacket::decode, SendParticlesPacket::handle));
            ClientPlayNetworking.registerReceiver(SyncHitsOnTargetLessenedPacket.ID, createS2CHandler(SyncHitsOnTargetLessenedPacket::decode, SyncHitsOnTargetLessenedPacket::handle));
            ClientPlayNetworking.registerReceiver(SyncKeysLessenedPacket.ID, createS2CHandler(SyncKeysLessenedPacket::decode, SyncKeysLessenedPacket::handle));
            ClientPlayNetworking.registerReceiver(AddKeyToCheckPacket.ID, createS2CHandler(AddKeyToCheckPacket::decode, AddKeyToCheckPacket::handle));
            ClientPlayNetworking.registerReceiver(SyncExplosionPacket.ID, createS2CHandler(SyncExplosionPacket::decode, SyncExplosionPacket::handle));
            ClientPlayNetworking.registerReceiver(UpdateUrlTexturesPacket.ID, createS2CHandler(UpdateUrlTexturesPacket::decode, UpdateUrlTexturesPacket::handle));
            ClientPlayNetworking.registerReceiver(ExecuteEntityActionClientPacket.ID, createS2CHandler(ExecuteEntityActionClientPacket::decode, ExecuteEntityActionClientPacket::handle));
            ClientPlayNetworking.registerReceiver(ExecuteBiEntityActionClientPacket.ID, createS2CHandler(ExecuteBiEntityActionClientPacket::decode, ExecuteBiEntityActionClientPacket::handle));
            ClientPlayNetworking.registerReceiver(SyncScalePacket.ID, createS2CHandler(SyncScalePacket::decode, SyncScalePacket::handle));
            ClientPlayNetworking.registerReceiver(ClearScaleModifierCachePacket.ID, createS2CHandler(ClearScaleModifierCachePacket::decode, ClearScaleModifierCachePacket::handle));
            ClientPlayNetworking.registerReceiver(UpdateScaleDataPacket.ID, createS2CHandler(UpdateScaleDataPacket::decode, UpdateScaleDataPacket::handle));
        });
    }

    public static void sendS2C(ApugliPacketS2C packet, ServerPlayer player) {
        ServerPlayNetworking.send(player, packet.getFabricId(), packet.toBuf());
    }

    public static void sendS2CTrackingAndSelf(ApugliPacketS2C packet, ServerPlayer player) {
        for (ServerPlayer otherPlayer : PlayerLookup.tracking(player))
            ApugliPackets.sendS2C(packet, otherPlayer);
        ApugliPackets.sendS2C(packet, player);
    }

    private static <T extends ApugliPacketS2C> ClientPlayNetworking.PlayChannelHandler createS2CHandler(Function<FriendlyByteBuf, T> decode, Consumer<T> handler) {
        return (client, _handler, buf, responseSender) -> handler.accept(decode.apply(buf));
    }

    public static void registerC2S() {
        ServerPlayNetworking.registerGlobalReceiver(UpdateKeysPressedPacket.ID, createC2SHandler(UpdateKeysPressedPacket::decode, UpdateKeysPressedPacket::handle));
        ServerPlayNetworking.registerGlobalReceiver(ExecuteEntityActionServerPacket.ID, createC2SHandler(ExecuteEntityActionServerPacket::decode, ExecuteEntityActionServerPacket::handle));
        ServerPlayNetworking.registerGlobalReceiver(ExecuteBiEntityActionServerPacket.ID, createC2SHandler(ExecuteBiEntityActionServerPacket::decode, ExecuteBiEntityActionServerPacket::handle));
    }

    public static void sendC2S(ApugliPacketC2S packet) {
        ClientPlayNetworking.send(packet.getFabricId(), packet.toBuf());
    }

    private static <T extends ApugliPacketC2S> ServerPlayNetworking.PlayChannelHandler createC2SHandler(Function<FriendlyByteBuf, T> decode, TriConsumer<T, MinecraftServer, ServerPlayer> handler) {
        return (server, player, _handler, buf, sender) -> handler.accept(decode.apply(buf), server, player);
    }

}
