package net.merchantpug.apugli.network.s2c;

import net.merchantpug.apugli.Apugli;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;

public record ForcePlayerPosePacket(int entityId, Pose pose) implements ApugliPacketS2C {

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(this.entityId());
        buf.writeBoolean(this.pose() != null);
        if (this.pose() != null) {
            buf.writeEnum(this.pose());
        }
    }

    public static ForcePlayerPosePacket decode(FriendlyByteBuf buf) {
        int entityId = buf.readInt();
        Pose pose = null;
        if (buf.readBoolean()) {
            pose = buf.readEnum(Pose.class);
        }
        return new ForcePlayerPosePacket(entityId, pose);
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
                Entity entity = Minecraft.getInstance().level.getEntity(entityId());
                if (!(entity instanceof Player player)) {
                    Apugli.LOG.warn("Could not find player entity to sync keys with.");
                    return;
                }
                player.setForcedPose(pose());
            }
        });
    }
}
