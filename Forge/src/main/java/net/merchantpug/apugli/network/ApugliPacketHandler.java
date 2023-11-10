package net.merchantpug.apugli.network;

import net.merchantpug.apugli.Apugli;
import net.merchantpug.apugli.network.c2s.ApugliPacketC2S;
import net.merchantpug.apugli.network.c2s.ExecuteBiEntityActionServerPacket;
import net.merchantpug.apugli.network.c2s.ExecuteEntityActionServerPacket;
import net.merchantpug.apugli.network.c2s.UpdateKeysPressedPacket;
import net.merchantpug.apugli.network.s2c.AddKeyToCheckPacket;
import net.merchantpug.apugli.network.s2c.ApugliPacketS2C;
import net.merchantpug.apugli.network.s2c.ExecuteBiEntityActionClientPacket;
import net.merchantpug.apugli.network.s2c.ExecuteEntityActionClientPacket;
import net.merchantpug.apugli.network.s2c.ForcePlayerPosePacket;
import net.merchantpug.apugli.network.s2c.ModifyEnchantmentLevelPacket;
import net.merchantpug.apugli.network.s2c.SendParticlesPacket;
import net.merchantpug.apugli.network.s2c.SyncExplosionPacket;
import net.merchantpug.apugli.network.s2c.SyncHitsOnTargetCapabilityPacket;
import net.merchantpug.apugli.network.s2c.SyncHitsOnTargetLessenedPacket;
import net.merchantpug.apugli.network.s2c.SyncKeyPressCapabilityPacket;
import net.merchantpug.apugli.network.s2c.SyncKeysLessenedPacket;
import net.merchantpug.apugli.network.s2c.UpdateUrlTexturesPacket;
import net.merchantpug.apugli.network.s2c.integration.pehkui.MarkLerpedScaleReadyPacket;
import net.merchantpug.apugli.network.s2c.integration.pehkui.SyncScalePacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
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
        INSTANCE.registerMessage(i++, UpdateKeysPressedPacket.class, UpdateKeysPressedPacket::encode, UpdateKeysPressedPacket::decode, createC2SHandler(UpdateKeysPressedPacket::handle));
        INSTANCE.registerMessage(i++, SendParticlesPacket.class, SendParticlesPacket::encode, SendParticlesPacket::decode, ApugliPacketHandler.createS2CHandler(SendParticlesPacket::handle));
        INSTANCE.registerMessage(i++, SyncExplosionPacket.class, SyncExplosionPacket::encode, SyncExplosionPacket::decode, ApugliPacketHandler.createS2CHandler(SyncExplosionPacket::handle));
        INSTANCE.registerMessage(i++, UpdateUrlTexturesPacket.class, UpdateUrlTexturesPacket::encode, UpdateUrlTexturesPacket::decode, ApugliPacketHandler.createS2CHandler(UpdateUrlTexturesPacket::handle));
        INSTANCE.registerMessage(i++, SyncHitsOnTargetCapabilityPacket.class, SyncHitsOnTargetCapabilityPacket::encode, SyncHitsOnTargetCapabilityPacket::decode, ApugliPacketHandler.createS2CHandler(SyncHitsOnTargetCapabilityPacket::handle));
        INSTANCE.registerMessage(i++, SyncHitsOnTargetLessenedPacket.class, SyncHitsOnTargetLessenedPacket::encode, SyncHitsOnTargetLessenedPacket::decode, ApugliPacketHandler.createS2CHandler(SyncHitsOnTargetLessenedPacket::handle));
        INSTANCE.registerMessage(i++, SyncKeyPressCapabilityPacket.class, SyncKeyPressCapabilityPacket::encode, SyncKeyPressCapabilityPacket::decode, ApugliPacketHandler.createS2CHandler(SyncKeyPressCapabilityPacket::handle));
        INSTANCE.registerMessage(i++, SyncKeysLessenedPacket.class, SyncKeysLessenedPacket::encode, SyncKeysLessenedPacket::decode, ApugliPacketHandler.createS2CHandler(SyncKeysLessenedPacket::handle));
        INSTANCE.registerMessage(i++, AddKeyToCheckPacket.class, AddKeyToCheckPacket::encode, AddKeyToCheckPacket::decode, ApugliPacketHandler.createS2CHandler(AddKeyToCheckPacket::handle));
        INSTANCE.registerMessage(i++, ExecuteEntityActionClientPacket.class, ExecuteEntityActionClientPacket::encode, ExecuteEntityActionClientPacket::decode, ApugliPacketHandler.createS2CHandler(ExecuteEntityActionClientPacket::handle));
        INSTANCE.registerMessage(i++, ExecuteEntityActionServerPacket.class, ExecuteEntityActionServerPacket::encode, ExecuteEntityActionServerPacket::decode, ApugliPacketHandler.createC2SHandler(ExecuteEntityActionServerPacket::handle));
        INSTANCE.registerMessage(i++, ExecuteBiEntityActionClientPacket.class, ExecuteBiEntityActionClientPacket::encode, ExecuteBiEntityActionClientPacket::decode, ApugliPacketHandler.createS2CHandler(ExecuteBiEntityActionClientPacket::handle));
        INSTANCE.registerMessage(i++, ExecuteBiEntityActionServerPacket.class, ExecuteBiEntityActionServerPacket::encode, ExecuteBiEntityActionServerPacket::decode, ApugliPacketHandler.createC2SHandler(ExecuteBiEntityActionServerPacket::handle));
        INSTANCE.registerMessage(i++, ForcePlayerPosePacket.class, ForcePlayerPosePacket::encode, ForcePlayerPosePacket::decode, ApugliPacketHandler.createS2CHandler(ForcePlayerPosePacket::handle));
        INSTANCE.registerMessage(i++, SyncScalePacket.class, SyncScalePacket::encode, SyncScalePacket::decode, ApugliPacketHandler.createS2CHandler(SyncScalePacket::handle));
        INSTANCE.registerMessage(i++, MarkLerpedScaleReadyPacket.class, MarkLerpedScaleReadyPacket::encode, MarkLerpedScaleReadyPacket::decode, ApugliPacketHandler.createS2CHandler(MarkLerpedScaleReadyPacket::handle));
        INSTANCE.registerMessage(i++, ModifyEnchantmentLevelPacket.class, ModifyEnchantmentLevelPacket::encode, ModifyEnchantmentLevelPacket::decode, ApugliPacketHandler.createS2CHandler(ModifyEnchantmentLevelPacket::handle));
    }

    public static void sendC2S(ApugliPacketC2S packet) {
        ApugliPacketHandler.INSTANCE.sendToServer(packet);
    }

    private static <MSG extends ApugliPacketC2S> BiConsumer<MSG, Supplier<NetworkEvent.Context>> createC2SHandler(TriConsumer<MSG, MinecraftServer, ServerPlayer> handler) {
        return (msg, ctx) -> {
            handler.accept(msg, ctx.get().getSender().getServer(), ctx.get().getSender());
            ctx.get().setPacketHandled(true);
        };
    }

    public static void sendS2C(ApugliPacketS2C packet, ServerPlayer player) {
        ApugliPacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), packet);
    }

    public static void sendS2CTrackingAndSelf(ApugliPacketS2C packet, Entity entity) {
        if (entity instanceof ServerPlayer player) {
            ApugliPacketHandler.INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), packet);
            return;
        }
        ApugliPacketHandler.INSTANCE.send(PacketDistributor.TRACKING_ENTITY.with(() -> entity), packet);
    }

    private static <MSG extends ApugliPacketS2C> BiConsumer<MSG, Supplier<NetworkEvent.Context>> createS2CHandler(Consumer<MSG> handler) {
        return (msg, ctx) -> {
            handler.accept(msg);
            ctx.get().setPacketHandled(true);
        };
    }

}
