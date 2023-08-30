package net.merchantpug.apugli.network.c2s;

import io.github.edwinmindcraft.apoli.api.power.IActivePower;
import net.merchantpug.apugli.Apugli;
import net.merchantpug.apugli.capability.entity.KeyPressCapability;
import net.merchantpug.apugli.network.ApugliPacketHandler;
import net.merchantpug.apugli.network.s2c.SyncKeysLessenedPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.PacketDistributor;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public record UpdateKeysPressedPacket(Set<IActivePower.Key> addedKeys,
                                      Set<IActivePower.Key> removedKeys) implements ApugliPacketC2S {

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(addedKeys.size());
        for (IActivePower.Key key : addedKeys) {
            IActivePower.Key.CODEC.encodeStart(NbtOps.INSTANCE, key)
                    .resultOrPartial(msg -> {
                        buf.writeBoolean(false);
                        Apugli.LOG.error("Failed to encode added active power key. {}", key);
                    })
                    .ifPresent(tag -> {
                        buf.writeBoolean(true);
                        buf.writeNbt((CompoundTag) tag);
                    });
        }

        buf.writeInt(removedKeys.size());
        for (IActivePower.Key key : removedKeys) {
            IActivePower.Key.CODEC.encodeStart(NbtOps.INSTANCE, key)
                    .resultOrPartial(msg -> {
                        buf.writeBoolean(false);
                        Apugli.LOG.error("Failed to encode active power key. {}", key);
                    })
                    .ifPresent(tag -> {
                        buf.writeBoolean(true);
                        buf.writeNbt((CompoundTag) tag);
                    });
        }
    }

    public static UpdateKeysPressedPacket decode(FriendlyByteBuf buf) {
        Set<IActivePower.Key> addedKeys = new HashSet<>();
        int addedKeySize = buf.readInt();
        for (int i = 0; i < addedKeySize; ++i) {
            if (!buf.readBoolean()) continue;
            CompoundTag tag = buf.readNbt();
            IActivePower.Key.CODEC.parse(NbtOps.INSTANCE, tag)
                    .resultOrPartial(msg -> Apugli.LOG.error("Failed to decode active power key. {}", msg))
                    .ifPresent(addedKeys::add);
        }

        Set<IActivePower.Key> removedKeys = new HashSet<>();
        int removedKeySize = buf.readInt();
        for (int i = 0; i < removedKeySize; ++i) {
            if (!buf.readBoolean()) continue;
            CompoundTag tag = buf.readNbt();
            IActivePower.Key.CODEC.parse(NbtOps.INSTANCE, tag)
                    .resultOrPartial(msg -> Apugli.LOG.error("Failed to decode active power key. {}", msg))
                    .ifPresent(removedKeys::add);
        }

        return new UpdateKeysPressedPacket(addedKeys, removedKeys);
    }

    @Override
    public ResourceLocation getFabricId() {
        throw new RuntimeException("ApugliPacket#getFabricId is not meant to be used in Forge specific packets.");
    }

    @Override
    public void handle(MinecraftServer server, ServerPlayer player) {
        server.execute(() -> {
            player.getCapability(KeyPressCapability.INSTANCE).ifPresent(capability -> {
                addedKeys.forEach(capability::addKey);
                removedKeys.forEach(capability::removeKey);

                Set<IActivePower.Key> keysToAdd = capability.getCurrentlyUsedKeys().stream().filter(key -> !capability.getPreviouslyUsedKeys().contains(key)).collect(Collectors.toSet());
                Set<IActivePower.Key> keysToRemove = capability.getPreviouslyUsedKeys().stream().filter(key -> !capability.getCurrentlyUsedKeys().contains(key)).collect(Collectors.toSet());
                capability.setPreviouslyUsedKeys();

                ApugliPacketHandler.INSTANCE.send(PacketDistributor.TRACKING_ENTITY.with(() -> player), new SyncKeysLessenedPacket(player.getId(), keysToAdd, keysToRemove));
            });
        });
    }
}
