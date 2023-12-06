package net.merchantpug.apugli.network.s2c.integration.pehkui;

import net.merchantpug.apugli.Apugli;
import net.merchantpug.apugli.access.ScaleDataAccess;
import net.merchantpug.apugli.integration.pehkui.ApoliScaleModifier;
import net.merchantpug.apugli.network.s2c.ApugliPacketS2C;
import net.merchantpug.apugli.platform.Services;
import net.merchantpug.apugli.registry.power.ApugliPowers;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.apache.commons.compress.utils.Lists;
import virtuoel.pehkui.api.ScaleData;
import virtuoel.pehkui.api.ScaleRegistries;
import virtuoel.pehkui.api.ScaleType;

import java.util.List;

public record SyncScalePacket(int entityId, List<ResourceLocation> scaleTypes,
                              ResourceLocation powerId,
                              boolean remove) implements ApugliPacketS2C {
    public static final ResourceLocation ID = Apugli.asResource("sync_scale");

    @Deprecated
    public static SyncScalePacket addScaleToClient(int entityId, List<ResourceLocation> scaleTypes,
                           ResourceLocation powerId) {
        return new SyncScalePacket(entityId, scaleTypes, powerId, false);
    }

    public static SyncScalePacket removeScaleFromClient(int entityId, List<ResourceLocation> scaleTypes,
                           ResourceLocation powerId) {
        return new SyncScalePacket(entityId, scaleTypes, powerId, true);
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(this.entityId());

        buf.writeInt(this.scaleTypes().size());
        for (int i = 0; i < this.scaleTypes().size(); ++i) {
            buf.writeResourceLocation(this.scaleTypes().get(i));
        }

        buf.writeResourceLocation(this.powerId());
        buf.writeBoolean(this.remove());
    }

    public static SyncScalePacket decode(FriendlyByteBuf buf) {
        int entityId = buf.readInt();

        int typeSize = buf.readInt();
        List<ResourceLocation> scaleTypes = Lists.newArrayList();
        for (int i = 0; i < typeSize; ++i) {
            ResourceLocation typeId = buf.readResourceLocation();
            scaleTypes.add(typeId);
        }

        ResourceLocation powerId = buf.readResourceLocation();

        boolean remove = buf.readBoolean();
        return new SyncScalePacket(entityId, scaleTypes, powerId, remove);
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
                    Apugli.LOG.warn("Attempted loading SyncScalePacket without Pehkui.");
                    return;
                }

                Entity entity = Minecraft.getInstance().level.getEntity(entityId());

                if (entity == null) {
                    return;
                }

                Object object = ApugliPowers.MODIFY_SCALE.get().getApoliScaleModifier(powerId(), entity);
                if (!(object instanceof ApoliScaleModifier<?> apoliModifier)) {
                    Apugli.LOG.warn("Could not find ApoliScaleModifier for syncing removal from order list.");
                    return;
                }

                for (ResourceLocation scaleTypeId : scaleTypes()) {
                    ScaleType scaleType = ScaleRegistries.getEntry(ScaleRegistries.SCALE_TYPES, scaleTypeId);
                    ScaleData scaleData = scaleType.getScaleData(entity);
                    ((ScaleDataAccess) scaleData).apugli$removeFromApoliScaleModifiers(powerId());
                    scaleData.getBaseValueModifiers().remove(apoliModifier);
                    if (!remove()) {
                        ((ScaleDataAccess) scaleData).apugli$addToApoliScaleModifiers(powerId());
                        scaleData.getBaseValueModifiers().add(apoliModifier);
                    }
                }
            }
        });

    }
}
