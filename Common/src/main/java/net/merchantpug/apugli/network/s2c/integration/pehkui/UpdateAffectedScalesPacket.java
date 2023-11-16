package net.merchantpug.apugli.network.s2c.integration.pehkui;

import net.merchantpug.apugli.Apugli;
import net.merchantpug.apugli.integration.pehkui.PehkuiUtil;
import net.merchantpug.apugli.network.s2c.ApugliPacketS2C;
import net.merchantpug.apugli.platform.Services;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public record UpdateAffectedScalesPacket(int entityId) implements ApugliPacketS2C {
    public static final ResourceLocation ID = Apugli.asResource("update_affected_scales");

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(this.entityId());
    }

    public static UpdateAffectedScalesPacket decode(FriendlyByteBuf buf) {
        return new UpdateAffectedScalesPacket(buf.readInt());
    }

    @Override
    public ResourceLocation getFabricId() {
        return ID;
    }

    @Override
    public void handle() {
        // The lambda implementation of this Runnable breaks Forge servers.
        Minecraft.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                if (!Services.PLATFORM.isModLoaded("pehkui")) {
                    Apugli.LOG.warn("Attempted loading UpdateAffectedScalesPacket without Pehkui.");
                    return;
                }

                Entity entity = Minecraft.getInstance().level.getEntity(entityId());

                if (entity == null) {
                    Apugli.LOG.warn("Failed to find entity to update affected scales for.");
                    return;
                }
                PehkuiUtil.updateAffectedScales(entity);
            }
        });

    }
}