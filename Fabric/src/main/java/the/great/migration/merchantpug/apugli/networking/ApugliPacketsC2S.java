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

package the.great.migration.merchantpug.apugli.networking;

import com.github.merchantpug.apugli.util.ApugliServerConfig;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.Active;
import io.netty.buffer.Unpooled;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.metadata.ModOrigin;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;
import net.minecraft.world.entity.Entity;
import the.great.migration.merchantpug.apugli.Apugli;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

public class ApugliPacketsC2S {
    public static void register() {
        ServerLoginConnectionEvents.QUERY_START.register(ApugliPacketsC2S::handshake);
        ServerLoginNetworking.registerGlobalReceiver(ApugliPackets.HANDSHAKE, ApugliPacketsC2S::handleHandshakeReply);
        ServerPlayNetworking.registerGlobalReceiver(ApugliPackets.SYNC_ACTIVE_KEYS_SERVER, ApugliPacketsC2S::onSyncActiveKeys);
    }

    private static void onSyncActiveKeys(MinecraftServer minecraftServer, ServerPlayer playerEntity, ServerGamePacketListenerImpl serverPlayNetworkHandler, FriendlyByteBuf packetByteBuf, PacketSender packetSender) {
        int count = packetByteBuf.readInt();
        UUID playerUuid = packetByteBuf.readUUID();
        Active.Key[] activeKeys = new Active.Key[count];
        for(int i = 0; i < count; i++) {
            activeKeys[i] = ApoliDataTypes.KEY.receive(packetByteBuf);
        }
        minecraftServer.execute(() -> {
            Entity entity = playerEntity.getLevel().getEntity(playerUuid);
            if(!(entity instanceof ServerPlayer playerEntity2)) {
                Apugli.LOGGER.warn("Tried modifying non ServerPlayerEntity's keys pressed.");
                return;
            }
            FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
            buf.writeInt(activeKeys.length);
            buf.writeInt(entity.getId());
            for(Active.Key key : activeKeys) {
                ApoliDataTypes.KEY.send(buf, key);
            }
            for(ServerPlayer player : PlayerLookup.tracking(playerEntity2)) {
                ServerPlayNetworking.send(player, ApugliPackets.SYNC_ACTIVE_KEYS_CLIENT, buf);
            }
            ServerPlayNetworking.send(playerEntity2, ApugliPackets.SYNC_ACTIVE_KEYS_CLIENT, buf);
            if(activeKeys.length == 0) {
                Apugli.currentlyUsedKeys.remove(playerEntity2.getUUID());
            } else {
                Apugli.currentlyUsedKeys.put(playerEntity2.getUUID(), new HashSet<>(List.of(activeKeys)));
            }
        });
    }

    private static void handleHandshakeReply(MinecraftServer minecraftServer, ServerLoginPacketListenerImpl serverLoginNetworkHandler, boolean understood, FriendlyByteBuf packetByteBuf, ServerLoginNetworking.LoginSynchronizer loginSynchronizer, PacketSender packetSender) {
        boolean shouldCheckVersion = ApugliServerConfig.performVersionCheck;
        if(FabricLoader.getInstance().getModContainer(Apugli.MODID).isPresent() && FabricLoader.getInstance().getModContainer(Apugli.MODID).get().getOrigin().getKind().equals(ModOrigin.Kind.NESTED)) {
            shouldCheckVersion = false;
        }

        if(shouldCheckVersion) {
            if(understood) {
                int clientSemVerLength = packetByteBuf.readInt();
                int[] clientSemVer = new int[clientSemVerLength];
                boolean mismatch = clientSemVerLength != Apugli.SEMVER.length;
                for(int i = 0; i < clientSemVerLength; i++) {
                    clientSemVer[i] = packetByteBuf.readInt();
                    if(i < clientSemVerLength - 1 && clientSemVer[i] != Apugli.SEMVER[i]) {
                        mismatch = true;
                    }
                }
                if(mismatch) {
                    StringBuilder clientVersionString = new StringBuilder();
                    for(int i = 0; i < clientSemVerLength; i++) {
                        clientVersionString.append(clientSemVer[i]);
                        if(i < clientSemVerLength - 1) {
                            clientVersionString.append(".");
                        }
                    }
                    serverLoginNetworkHandler.disconnect(Component.translatable("apugli.gui.version_mismatch", Apugli.VERSION, clientVersionString));
                }
            } else {
                serverLoginNetworkHandler.disconnect(Component.literal("This server requires you to install the Apugli mod (v" + Apugli.VERSION + ") to play."));
            }
        }
    }

    private static void handshake(ServerLoginPacketListenerImpl serverLoginNetworkHandler, MinecraftServer minecraftServer, PacketSender packetSender, ServerLoginNetworking.LoginSynchronizer loginSynchronizer) {
        packetSender.sendPacket(ApugliPackets.HANDSHAKE, PacketByteBufs.empty());
    }
}
