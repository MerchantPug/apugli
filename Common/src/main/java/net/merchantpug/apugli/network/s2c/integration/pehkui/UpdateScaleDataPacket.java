package net.merchantpug.apugli.network.s2c.integration.pehkui;

import net.merchantpug.apugli.Apugli;
import net.merchantpug.apugli.network.s2c.ApugliPacketS2C;
import net.merchantpug.apugli.platform.Services;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.apache.commons.compress.utils.Lists;
import virtuoel.pehkui.api.ScaleRegistries;

import java.util.List;

public record UpdateScaleDataPacket(int entityId, List<ResourceLocation> types) implements ApugliPacketS2C {
    public static final ResourceLocation ID = Apugli.asResource("update_scale_data");

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(this.entityId());
        buf.writeInt(this.types().size());
        for (int i = 0; i < this.types().size(); ++i) {
            buf.writeResourceLocation(this.types().get(i));
        }
    }

    public static UpdateScaleDataPacket decode(FriendlyByteBuf buf) {
        int entityId = buf.readInt();

        int typeSize = buf.readInt();
        List<ResourceLocation> types = Lists.newArrayList();
        for (int i = 0; i < typeSize; ++i) {
            ResourceLocation typeId = buf.readResourceLocation();
            types.add(typeId);
        }

        return new UpdateScaleDataPacket(entityId, types);
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
                    Apugli.LOG.warn("Attempted loading UpdateScaleDataPacket without Pehkui.");
                    return;
                }

                Entity entity = Minecraft.getInstance().level.getEntity(entityId());

                for (ResourceLocation typeId : types()) {
                    ScaleRegistries.getEntry(ScaleRegistries.SCALE_TYPES, typeId).getScaleData(entity).onUpdate();
                }
            }
        });

    }
}
