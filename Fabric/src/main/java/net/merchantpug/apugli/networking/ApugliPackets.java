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

package net.merchantpug.apugli.networking;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.fabricmc.fabric.api.client.networking.v1.ClientLoginNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.*;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.metadata.ModOrigin;
import net.merchantpug.apugli.Apugli;;
import net.merchantpug.apugli.client.ApugliClientFabric;
import net.merchantpug.apugli.networking.c2s.ApugliPacketC2S;
import net.merchantpug.apugli.networking.c2s.UpdateKeysPressedPacket;
import net.merchantpug.apugli.networking.s2c.*;
import net.merchantpug.apugli.util.ApugliConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientHandshakePacketListenerImpl;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;
import org.apache.logging.log4j.util.TriConsumer;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;

public class ApugliPackets {
    public static final ResourceLocation HANDSHAKE = Apugli.asResource("handshake");

    public static void registerS2C() {
        ClientLoginNetworking.registerGlobalReceiver(ApugliPackets.HANDSHAKE, ApugliPackets::handleHandshake);
        ClientPlayConnectionEvents.INIT.register((clientPlayNetworkHandler, minecraftClient) -> {
            ClientPlayNetworking.registerReceiver(SendParticlesPacket.ID, createS2CHandler(SendParticlesPacket::decode, SendParticlesPacket::handle));
            ClientPlayNetworking.registerReceiver(SyncHitsOnTargetLessenedPacket.ID, createS2CHandler(SyncHitsOnTargetLessenedPacket::decode, SyncHitsOnTargetLessenedPacket::handle));
            ClientPlayNetworking.registerReceiver(SyncKeysLessenedPacket.ID, createS2CHandler(SyncKeysLessenedPacket::decode, SyncKeysLessenedPacket::handle));
            ClientPlayNetworking.registerReceiver(SyncExplosionPacket.ID, createS2CHandler(SyncExplosionPacket::decode, SyncExplosionPacket::handle));
            ClientPlayNetworking.registerReceiver(UpdateUrlTexturesPacket.ID, createS2CHandler(UpdateUrlTexturesPacket::decode, UpdateUrlTexturesPacket::handle));
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

    private static CompletableFuture<FriendlyByteBuf> handleHandshake(Minecraft minecraftClient, ClientHandshakePacketListenerImpl clientLoginNetworkHandler, FriendlyByteBuf packetByteBuf, Consumer<GenericFutureListener<? extends Future<? super Void>>> genericFutureListenerConsumer) {
        FriendlyByteBuf buf = PacketByteBufs.create();
        buf.writeInt(Apugli.SEMVER.length);
        for(int i = 0; i < Apugli.SEMVER.length; i++) {
            buf.writeInt(Apugli.SEMVER[i]);
        }
        ApugliClientFabric.isServerRunningApugli = true;
        return CompletableFuture.completedFuture(buf);
    }

    public static void registerC2S() {
        ServerLoginConnectionEvents.QUERY_START.register(ApugliPackets::handshake);
        ServerLoginNetworking.registerGlobalReceiver(ApugliPackets.HANDSHAKE, ApugliPackets::handleHandshakeReply);
        ServerPlayNetworking.registerGlobalReceiver(UpdateKeysPressedPacket.ID, createC2SHandler(UpdateKeysPressedPacket::decode, UpdateKeysPressedPacket::handle));
    }

    public static void sendC2S(ApugliPacketC2S packet) {
        ClientPlayNetworking.send(packet.getFabricId(), packet.toBuf());
    }

    private static void handshake(ServerLoginPacketListenerImpl serverLoginNetworkHandler, MinecraftServer minecraftServer, PacketSender packetSender, ServerLoginNetworking.LoginSynchronizer loginSynchronizer) {
        if (!ApugliConfig.performVersionCheck) return;
        packetSender.sendPacket(ApugliPackets.HANDSHAKE, PacketByteBufs.empty());
    }

    private static <T extends ApugliPacketC2S> ServerPlayNetworking.PlayChannelHandler createC2SHandler(Function<FriendlyByteBuf, T> decode, TriConsumer<T, MinecraftServer, ServerPlayer> handler) {
        return (server, player, _handler, buf, sender) -> handler.accept(decode.apply(buf), server, player);
    }

    private static void handleHandshakeReply(MinecraftServer server, ServerLoginPacketListenerImpl handler, boolean understood, FriendlyByteBuf buf, ServerLoginNetworking.LoginSynchronizer synchronizer, PacketSender sender) {
        boolean shouldCheckVersion = ApugliConfig.performVersionCheck && FabricLoader.getInstance().getModContainer(Apugli.ID).isEmpty() || !FabricLoader.getInstance().getModContainer(Apugli.ID).get().getOrigin().getKind().equals(ModOrigin.Kind.NESTED);

        if (shouldCheckVersion) {
            if (understood) {
                int clientSemVerLength = buf.readInt();
                int[] clientSemVer = new int[clientSemVerLength];
                boolean mismatch = clientSemVerLength != Apugli.SEMVER.length;
                for (int i = 0; i < clientSemVerLength; i++) {
                    clientSemVer[i] = buf.readInt();
                    if (i < clientSemVerLength - 1 && clientSemVer[i] != Apugli.SEMVER[i]) {
                        mismatch = true;
                    }
                }
                if (mismatch) {
                    StringBuilder clientVersionString = new StringBuilder();
                    for (int i = 0; i < clientSemVerLength; i++) {
                        clientVersionString.append(clientSemVer[i]);
                        if (i < clientSemVerLength - 1) {
                            clientVersionString.append(".");
                        }
                    }
                    handler.disconnect(Component.translatable("apugli.gui.version_mismatch", Apugli.VERSION, clientVersionString));
                }
            } else {
                handler.disconnect(Component.literal("This server requires you to install the Apugli mod (v" + Apugli.VERSION + ") to play."));
            }
        }
    }

}
