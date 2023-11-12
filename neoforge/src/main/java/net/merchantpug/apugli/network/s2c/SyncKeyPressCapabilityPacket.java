package net.merchantpug.apugli.network.s2c;

import io.github.edwinmindcraft.apoli.api.power.IActivePower;
import net.merchantpug.apugli.Apugli;
import net.merchantpug.apugli.capability.entity.KeyPressCapability;
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
                                           Set<IActivePower.Key> previouslyUsedKeys,
                                           Set<IActivePower.Key> currentlyUsedKeys) implements ApugliPacketS2C {

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(entityId);

        buf.writeInt(previouslyUsedKeys.size());
        for (IActivePower.Key key : previouslyUsedKeys) {
            IActivePower.Key.CODEC.encodeStart(NbtOps.INSTANCE, key)
                    .resultOrPartial(msg -> {
                        buf.writeBoolean(false);
                        Apugli.LOG.error("Failed to encode previously used active power key for syncing. {}", key);
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
                        Apugli.LOG.error("Failed to encode currently used active power key for syncing. {}", key);
                    })
                    .ifPresent(tag -> {
                        buf.writeBoolean(true);
                        buf.writeNbt((CompoundTag) tag);
                    });
        }
    }

    public static SyncKeyPressCapabilityPacket decode(FriendlyByteBuf buf) {
        int entityId = buf.readInt();

        Set<IActivePower.Key> previouslyUsedKeys = new HashSet<>();
        int previouslyUsedKeySize = buf.readInt();
        for (int i = 0; i < previouslyUsedKeySize; ++i) {
            if (!buf.readBoolean()) continue;
            CompoundTag tag = buf.readNbt();
            IActivePower.Key.CODEC.parse(NbtOps.INSTANCE, tag)
                    .resultOrPartial(msg -> Apugli.LOG.error("Failed to decode active power key while syncing. {}", msg))
                    .ifPresent(previouslyUsedKeys::add);
        }

        Set<IActivePower.Key> currentlyUsedKeys = new HashSet<>();
        int currentlyUsedKeySize = buf.readInt();
        for (int i = 0; i < currentlyUsedKeySize; ++i) {
            if (!buf.readBoolean()) continue;
            CompoundTag tag = buf.readNbt();
            IActivePower.Key.CODEC.parse(NbtOps.INSTANCE, tag)
                    .resultOrPartial(msg -> Apugli.LOG.error("Failed to decode active power key while syncing. {}", msg))
                    .ifPresent(currentlyUsedKeys::add);
        }

        return new SyncKeyPressCapabilityPacket(entityId, previouslyUsedKeys, currentlyUsedKeys);
    }

    @Override
    public ResourceLocation getFabricId() {
        throw new RuntimeException("ApugliPacket#getFabricId is not meant to be used in Forge specific packets.");
    }

    @Override
    public void handle() {
        // The lambda implementation of this Runnable breaks Forge servers.
        Minecraft.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                Entity entity = Minecraft.getInstance().level.getEntity(entityId);
                if (!(entity instanceof Player player)) {
                    Apugli.LOG.warn("Could not find player entity to sync keys with.");
                    return;
                }
                player.getCapability(KeyPressCapability.INSTANCE).ifPresent(capability -> {
                    for (IActivePower.Key key : currentlyUsedKeys) {
                        capability.addKey(key);
                    }
                    for (IActivePower.Key key : previouslyUsedKeys) {
                        capability.addPreviousKey(key);
                    }
                });
            }
        });
    }
}
