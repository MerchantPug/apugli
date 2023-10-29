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

public record SyncScalePacket(int entityId, List<ResourceLocation> types,
                              ResourceLocation mappedScaleModifierId,
                              List<?> modifiers,
                              boolean remove) implements ApugliPacketS2C {
    public static final ResourceLocation ID = Apugli.asResource("sync_scale");

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(this.entityId());

        buf.writeInt(this.types().size());
        for (int i = 0; i < this.types().size(); ++i) {
            buf.writeResourceLocation(this.types().get(i));
        }

        buf.writeResourceLocation(this.mappedScaleModifierId());
        Services.PLATFORM.getModifiersDataType().send(buf, this.modifiers());
        buf.writeBoolean(this.remove());
    }

    public static SyncScalePacket decode(FriendlyByteBuf buf) {
        int entityId = buf.readInt();

        int typeSize = buf.readInt();
        List<ResourceLocation> types = Lists.newArrayList();
        for (int i = 0; i < typeSize; ++i) {
            ResourceLocation typeId = buf.readResourceLocation();
            types.add(typeId);
        }

        ResourceLocation mappedScaleModifierId = buf.readResourceLocation();
        List<?> modifiers = Services.PLATFORM.getModifiersDataType().receive(buf);
        boolean remove = buf.readBoolean();
        return new SyncScalePacket(entityId, types, mappedScaleModifierId, modifiers, remove);
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

                if (ApugliPowers.MODIFY_SCALE.get().getModifierFromCache(mappedScaleModifierId()) == null) {
                    ApugliPowers.MODIFY_SCALE.get().addModifierToCache(mappedScaleModifierId(), new ApoliScaleModifier(modifiers(), mappedScaleModifierId()));
                }
                ApoliScaleModifier apoliModifier = ApugliPowers.MODIFY_SCALE.get().getModifierFromCache(mappedScaleModifierId());

                if (remove()) {
                    for (ResourceLocation scaleTypeId : types()) {
                        ScaleType scaleType = ScaleRegistries.getEntry(ScaleRegistries.SCALE_TYPES, scaleTypeId);
                        ScaleData scaleData = scaleType.getScaleData(entity);
                        ((ScaleDataAccess)scaleData).apugli$removeFromApoliScaleModifiers(mappedScaleModifierId());
                        scaleData.getBaseValueModifiers().remove(apoliModifier);
                    }
                } else {
                    for (ResourceLocation scaleTypeId : types()) {
                        ScaleType scaleType = ScaleRegistries.getEntry(ScaleRegistries.SCALE_TYPES, scaleTypeId);
                        ScaleData scaleData = scaleType.getScaleData(entity);
                        ((ScaleDataAccess)scaleData).apugli$addToApoliScaleModifiers(mappedScaleModifierId());
                        scaleData.getBaseValueModifiers().add(apoliModifier);
                    }
                }
            }
        });

    }
}
