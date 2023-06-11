package net.merchantpug.apugli.network;

import net.merchantpug.apugli.Apugli;
import net.merchantpug.apugli.network.c2s.UpdateKeysPressedPacket;
import net.merchantpug.apugli.network.s2c.SyncHitsOnTargetCapabilityPacket;
import net.merchantpug.apugli.network.s2c.SyncHitsOnTargetLessenedPacket;
import net.merchantpug.apugli.network.s2c.SyncKeyPressCapabilityPacket;
import net.merchantpug.apugli.network.s2c.SyncKeysLessenedPacket;
import net.merchantpug.apugli.networking.ApugliPacket;
import net.merchantpug.apugli.networking.c2s.ExecuteBiEntityActionServerPacket;
import net.merchantpug.apugli.networking.c2s.ExecuteEntityActionServerPacket;
import net.merchantpug.apugli.networking.s2c.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import org.apache.logging.log4j.util.TriConsumer;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ApugliPacketHandler {
    private static final String PROTOCOL_VERISON = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            Apugli.asResource("main"),
            () -> PROTOCOL_VERISON,
            PROTOCOL_VERISON::equals,
            PROTOCOL_VERISON::equals
    );

    public static void register() {
        int i = 0;
        INSTANCE.messageBuilder(UpdateKeysPressedPacket.class, i++, NetworkDirection.PLAY_TO_SERVER)
                .encoder(UpdateKeysPressedPacket::encode)
                .decoder(UpdateKeysPressedPacket::decode)
                .consumerNetworkThread(createC2SHandler(UpdateKeysPressedPacket.Handler::handle))
                .add();
        INSTANCE.messageBuilder(ExecuteEntityActionServerPacket.class, i++, NetworkDirection.PLAY_TO_SERVER)
                .encoder(ExecuteEntityActionServerPacket::encode)
                .decoder(ExecuteEntityActionServerPacket::decode)
                .consumerNetworkThread(createC2SHandler(ExecuteEntityActionServerPacket.Handler::handle))
                .add();
        INSTANCE.messageBuilder(ExecuteBiEntityActionServerPacket.class, i++, NetworkDirection.PLAY_TO_SERVER)
                .encoder(ExecuteBiEntityActionServerPacket::encode)
                .decoder(ExecuteBiEntityActionServerPacket::decode)
                .consumerNetworkThread(createC2SHandler(ExecuteBiEntityActionServerPacket.Handler::handle))
                .add();

        INSTANCE.messageBuilder(SendParticlesPacket.class, i++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(SendParticlesPacket::encode)
                .decoder(SendParticlesPacket::decode)
                .consumerNetworkThread(createS2CHandler(SendParticlesPacket.Handler::handle))
                .add();
        INSTANCE.messageBuilder(SyncExplosionPacket.class, i++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(SyncExplosionPacket::encode)
                .decoder(SyncExplosionPacket::decode)
                .consumerNetworkThread(createS2CHandler(SyncExplosionPacket.Handler::handle))
                .add();
        INSTANCE.messageBuilder(UpdateUrlTexturesPacket.class, i++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(UpdateUrlTexturesPacket::encode)
                .decoder(UpdateUrlTexturesPacket::decode)
                .consumerNetworkThread(createS2CHandler(UpdateUrlTexturesPacket.Handler::handle))
                .add();
        INSTANCE.messageBuilder(SyncHitsOnTargetCapabilityPacket.class, i++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(SyncHitsOnTargetCapabilityPacket::encode)
                .decoder(SyncHitsOnTargetCapabilityPacket::decode)
                .consumerNetworkThread(createS2CHandler(SyncHitsOnTargetCapabilityPacket.Handler::handle))
                .add();
        INSTANCE.messageBuilder(SyncHitsOnTargetLessenedPacket.class, i++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(SyncHitsOnTargetLessenedPacket::encode)
                .decoder(SyncHitsOnTargetLessenedPacket::decode)
                .consumerNetworkThread(createS2CHandler(SyncHitsOnTargetLessenedPacket.Handler::handle))
                .add();
        INSTANCE.messageBuilder(SyncKeyPressCapabilityPacket.class, i++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(SyncKeyPressCapabilityPacket::encode)
                .decoder(SyncKeyPressCapabilityPacket::decode)
                .consumerNetworkThread(createS2CHandler(SyncKeyPressCapabilityPacket.Handler::handle))
                .add();
        INSTANCE.messageBuilder(SyncKeysLessenedPacket.class, i++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(SyncKeysLessenedPacket::encode)
                .decoder(SyncKeysLessenedPacket::decode)
                .consumerNetworkThread(createS2CHandler(SyncKeysLessenedPacket.Handler::handle))
                .add();
        INSTANCE.messageBuilder(ExecuteEntityActionClientPacket.class, i++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(ExecuteEntityActionClientPacket::encode)
                .decoder(ExecuteEntityActionClientPacket::decode)
                .consumerNetworkThread(createS2CHandler(ExecuteEntityActionClientPacket.Handler::handle))
                .add();
        INSTANCE.messageBuilder(ExecuteBiEntityActionClientPacket.class, i++, NetworkDirection.LOGIN_TO_CLIENT)
                .encoder(ExecuteBiEntityActionClientPacket::encode)
                .decoder(ExecuteBiEntityActionClientPacket::decode)
                .consumerNetworkThread(createS2CHandler(ExecuteBiEntityActionClientPacket.Handler::handle))
                .add();
    }

    public static void sendC2S(ApugliPacket packet) {
        ApugliPacketHandler.INSTANCE.sendToServer(packet);
    }

    private static <MSG extends ApugliPacket> BiConsumer<MSG, Supplier<NetworkEvent.Context>> createC2SHandler(TriConsumer<MSG, MinecraftServer, ServerPlayer> handler) {
        return (msg, ctx) -> {
            handler.accept(msg, ctx.get().getSender().getServer(), ctx.get().getSender());
            ctx.get().setPacketHandled(true);
        };
    }

    public static void sendS2C(ApugliPacket packet, ServerPlayer player) {
        ApugliPacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), packet);
    }

    public static void sendS2CTrackingAndSelf(ApugliPacket packet, Entity entity) {
        if (entity instanceof ServerPlayer player) {
            ApugliPacketHandler.INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), packet);
            return;
        }
        ApugliPacketHandler.INSTANCE.send(PacketDistributor.TRACKING_ENTITY.with(() -> entity), packet);
    }

    private static <MSG extends ApugliPacket> BiConsumer<MSG, Supplier<NetworkEvent.Context>> createS2CHandler(Consumer<MSG> handler) {
        return (msg, ctx) -> {
            handler.accept(msg);
            ctx.get().setPacketHandled(true);
        };
    }

}
