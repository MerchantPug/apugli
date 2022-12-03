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

import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.Active;
import net.merchantpug.apugli.Apugli;
import net.fabricmc.fabric.api.networking.v1.*;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.metadata.ModOrigin;
import net.merchantpug.apugli.component.ApugliEntityComponents;
import net.merchantpug.apugli.component.KeyPressComponent;
import net.merchantpug.apugli.util.ApugliConfig;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerLoginNetworkHandler;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.HashSet;
import java.util.Set;

public class ApugliPacketsC2S {
    public static void register() {
        ServerLoginConnectionEvents.QUERY_START.register(ApugliPacketsC2S::handshake);
        ServerLoginNetworking.registerGlobalReceiver(ApugliPackets.HANDSHAKE, ApugliPacketsC2S::handleHandshakeReply);
        ServerPlayNetworking.registerGlobalReceiver(ApugliPackets.UPDATE_KEYS_PRESSED, ApugliPacketsC2S::onUpdateKeysPressed);
    }

    private static void onUpdateKeysPressed(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender sender) {
        Set<Active.Key> addedKeys = new HashSet<>();
        int addedKeySize = buf.readInt();
        for (int i = 0; i < addedKeySize; ++i) {
            addedKeys.add(ApoliDataTypes.KEY.receive(buf));
        }
        Set<Active.Key> removedKeys = new HashSet<>();
        int removedKeySize = buf.readInt();
        for (int i = 0; i < removedKeySize; ++i) {
             removedKeys.add(ApoliDataTypes.KEY.receive(buf));
        }
        server.execute(() -> {
            KeyPressComponent component = ApugliEntityComponents.KEY_PRESS_COMPONENT.get(player);
            addedKeys.forEach(component::addKey);
            removedKeys.forEach(component::removeKey);
            ApugliEntityComponents.KEY_PRESS_COMPONENT.sync(player);
        });
    }

    private static void handleHandshakeReply(MinecraftServer server, ServerLoginNetworkHandler handler, boolean understood, PacketByteBuf buf, ServerLoginNetworking.LoginSynchronizer synchronizer, PacketSender sender) {
        boolean shouldCheckVersion = ApugliConfig.performVersionCheck;
        if (FabricLoader.getInstance().getModContainer(Apugli.MODID).isPresent() && FabricLoader.getInstance().getModContainer(Apugli.MODID).get().getOrigin().getKind().equals(ModOrigin.Kind.NESTED)) {
            shouldCheckVersion = false;
        }

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

    private static void handshake(ServerLoginNetworkHandler serverLoginNetworkHandler, MinecraftServer minecraftServer, PacketSender packetSender, ServerLoginNetworking.LoginSynchronizer loginSynchronizer) {
        packetSender.sendPacket(ApugliPackets.HANDSHAKE, PacketByteBufs.empty());
    }
}
