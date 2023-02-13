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
import net.merchantpug.apugli.Apugli;
import net.merchantpug.apugli.ApugliClient;
import net.merchantpug.apugli.networking.c2s.UpdateKeysPressedPacket;
import net.merchantpug.apugli.networking.s2c.*;
import net.merchantpug.apugli.util.ApugliConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientLoginNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerLoginNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.util.TriConsumer;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public class ApugliPackets {
    public static final Identifier HANDSHAKE = Apugli.identifier("handshake");

    public static void registerS2C() {
        ClientLoginNetworking.registerGlobalReceiver(ApugliPackets.HANDSHAKE, ApugliPackets::handleHandshake);
        ClientPlayConnectionEvents.INIT.register((clientPlayNetworkHandler, minecraftClient) -> {
            ClientPlayNetworking.registerReceiver(SendKeyToCheckPacket.ID, createS2CHandler(SendKeyToCheckPacket::decode, SendKeyToCheckPacket::handle));
            ClientPlayNetworking.registerReceiver(SendParticlesPacket.ID, createS2CHandler(SendParticlesPacket::decode, SendParticlesPacket::handle));
            ClientPlayNetworking.registerReceiver(SyncKeysLessenedPacket.ID, createS2CHandler(SyncKeysLessenedPacket::decode, SyncKeysLessenedPacket::handle));
            ClientPlayNetworking.registerReceiver(SyncRocketJumpExplosionPacket.ID, createS2CHandler(SyncRocketJumpExplosionPacket::decode, SyncRocketJumpExplosionPacket::handle));
            ClientPlayNetworking.registerReceiver(UpdateUrlTexturesPacket.ID, createS2CHandler(UpdateUrlTexturesPacket::decode, UpdateUrlTexturesPacket::handle));
        });
    }

    public static void registerC2S() {
        if (ApugliConfig.performVersionCheck) {
            ServerLoginConnectionEvents.QUERY_START.register(ApugliPackets::handshake);
            ServerLoginNetworking.registerGlobalReceiver(ApugliPackets.HANDSHAKE, ApugliPackets::handleHandshakeReply);
        }
        ServerPlayNetworking.registerGlobalReceiver(UpdateKeysPressedPacket.ID, createC2SHandler(UpdateKeysPressedPacket::decode, UpdateKeysPressedPacket::handle));
    }

    public static void sendC2SPacket(ApugliPacket packet) {
        ClientPlayNetworking.send(packet.getId(), packet.toBuf());
    }

    public static void sendS2CPacket(ApugliPacket packet, ServerPlayerEntity player) {
        ServerPlayNetworking.send(player, packet.getId(), packet.toBuf());
    }

    private static <T extends ApugliPacket> ServerPlayNetworking.PlayChannelHandler createC2SHandler(Function<PacketByteBuf, T> decode, TriConsumer<T, MinecraftServer, ServerPlayerEntity> handler) {
        return (server, player, _handler, buf, sender) -> handler.accept(decode.apply(buf), server, player);
    }

    private static <T extends ApugliPacket> ClientPlayNetworking.PlayChannelHandler createS2CHandler(Function<PacketByteBuf, T> decode, BiConsumer<T, MinecraftClient> handler) {
        return (client, _handler, buf, responseSender) -> handler.accept(decode.apply(buf), client);
    }

    private static void handshake(ServerLoginNetworkHandler serverLoginNetworkHandler, MinecraftServer minecraftServer, PacketSender packetSender, ServerLoginNetworking.LoginSynchronizer loginSynchronizer) {
        packetSender.sendPacket(ApugliPackets.HANDSHAKE, PacketByteBufs.empty());
    }

    private static CompletableFuture<PacketByteBuf> handleHandshake(MinecraftClient minecraftClient, ClientLoginNetworkHandler clientLoginNetworkHandler, PacketByteBuf packetByteBuf, Consumer<GenericFutureListener<? extends Future<? super Void>>> genericFutureListenerConsumer) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(Apugli.SEMVER.length);
        for(int i = 0; i < Apugli.SEMVER.length; i++) {
            buf.writeInt(Apugli.SEMVER[i]);
        }
        ApugliClient.isServerRunningApugli = true;
        return CompletableFuture.completedFuture(buf);
    }

    private static void handleHandshakeReply(MinecraftServer server, ServerLoginNetworkHandler handler, boolean understood, PacketByteBuf buf, ServerLoginNetworking.LoginSynchronizer synchronizer, PacketSender sender) {
        boolean shouldCheckVersion = FabricLoader.getInstance().getModContainer(Apugli.MODID).isEmpty() || !FabricLoader.getInstance().getModContainer(Apugli.MODID).get().getOrigin().getKind().equals(ModOrigin.Kind.NESTED);

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
                    handler.disconnect(Text.translatable("apugli.gui.version_mismatch", Apugli.VERSION, clientVersionString));
                }
            } else {
                handler.disconnect(Text.literal("This server requires you to install the Apugli mod (v" + Apugli.VERSION + ") to play."));
            }
        }
    }
}
