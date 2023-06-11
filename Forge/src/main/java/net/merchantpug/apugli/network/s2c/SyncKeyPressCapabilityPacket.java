package net.merchantpug.apugli.network.s2c;

import io.github.edwinmindcraft.apoli.api.power.IActivePower;
import net.merchantpug.apugli.Apugli;
import net.merchantpug.apugli.capability.KeyPressCapability;
import net.merchantpug.apugli.network.ApugliPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

import java.util.HashSet;
import java.util.Set;

public record SyncKeyPressCapabilityPacket(int entityId,
                                           Set<IActivePower.Key> keysToCheck,
                                           Set<IActivePower.Key> currentlyUsedKeys) implements ApugliPacket {

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(entityId);

        buf.writeInt(keysToCheck.size());
        for (IActivePower.Key key : keysToCheck) {
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

        buf.writeInt(currentlyUsedKeys.size());
        for (IActivePower.Key key : currentlyUsedKeys) {
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
    }

    public static SyncKeyPressCapabilityPacket decode(FriendlyByteBuf buf) {
        int entityId = buf.readInt();

        Set<IActivePower.Key> keysToCheck = new HashSet<>();
        int keysToCheckSize = buf.readInt();
        for (int i = 0; i < keysToCheckSize; ++i) {
            if (!buf.readBoolean()) continue;
            CompoundTag tag = buf.readNbt();
            IActivePower.Key.CODEC.parse(NbtOps.INSTANCE, tag)
                    .resultOrPartial(msg -> Apugli.LOG.error("Failed to decode active power key. {}", msg))
                    .ifPresent(keysToCheck::add);
        }

        Set<IActivePower.Key> currentlyUsedKeys = new HashSet<>();
        int addedKeySize = buf.readInt();
        for (int i = 0; i < addedKeySize; ++i) {
            if (!buf.readBoolean()) continue;
            CompoundTag tag = buf.readNbt();
            IActivePower.Key.CODEC.parse(NbtOps.INSTANCE, tag)
                    .resultOrPartial(msg -> Apugli.LOG.error("Failed to decode active power key. {}", msg))
                    .ifPresent(currentlyUsedKeys::add);
        }

        return new SyncKeyPressCapabilityPacket(entityId, keysToCheck, currentlyUsedKeys);
    }

    @Override
    public ResourceLocation getFabricId() {
        throw new RuntimeException("ApugliPacket#getFabricId is not meant to be used in Forge specific packets.");
    }

    public static class Handler {
        public static void handle(SyncKeyPressCapabilityPacket packet) {
            Minecraft.getInstance().execute(() -> {
                Entity entity = Minecraft.getInstance().level.getEntity(packet.entityId);
                if (!(entity instanceof Player player)) {
                    Apugli.LOG.warn("Could not find player entity to sync keys with.");
                    return;
                }
                player.getCapability(KeyPressCapability.INSTANCE).ifPresent(capability -> {

                    for (IActivePower.Key key : packet.keysToCheck) {
                        capability.addKeyToCheck(key);
                    }

                    for (IActivePower.Key key : packet.currentlyUsedKeys) {
                        capability.addKey(key);
                    }
                });
            });
        }
    }
}
