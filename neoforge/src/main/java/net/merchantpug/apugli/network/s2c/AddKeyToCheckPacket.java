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

public record AddKeyToCheckPacket(int entityId, IActivePower.Key key) implements ApugliPacketS2C {

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(this.entityId());
        IActivePower.Key.CODEC.encodeStart(NbtOps.INSTANCE, this.key())
                .resultOrPartial(msg -> {
                    buf.writeBoolean(false);
                    Apugli.LOG.error("Failed to encode previously active power key for syncing keys to check. {}", this.key());
                })
                .ifPresent(tag -> {
                    buf.writeBoolean(true);
                    buf.writeNbt((CompoundTag) tag);
                });
    }

    public static AddKeyToCheckPacket decode(FriendlyByteBuf buf) {
        int entityId = buf.readInt();
        if (buf.readBoolean()) {
            CompoundTag tag = buf.readNbt();
            IActivePower.Key key = IActivePower.Key.CODEC.parse(NbtOps.INSTANCE, tag)
                    .resultOrPartial(msg -> Apugli.LOG.error("Failed to decode active power key while syncing keys to check. {}", msg))
                    .orElseThrow();
            return new AddKeyToCheckPacket(entityId, key);
        }
        return null;
    }

    @Override
    public ResourceLocation getFabricId() {
        throw new RuntimeException("ApugliPacket#getFabricId is not meant to be used in Forge specific packets.");
    }

    @Override
    public void handle() {
        Minecraft.getInstance().execute(() -> {
            Entity entity = Minecraft.getInstance().level.getEntity(this.entityId());
            if (!(entity instanceof Player player)) {
                Apugli.LOG.warn("Could not a client player to add a key to check with.");
                return;
            }
            if (!player.isLocalPlayer()) return;

            player.getCapability(KeyPressCapability.INSTANCE).ifPresent(capability -> {
                capability.addKeyToCheck(this.key());
            });
        });
    }
}
