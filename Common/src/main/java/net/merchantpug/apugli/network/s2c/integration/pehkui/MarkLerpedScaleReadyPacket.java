package net.merchantpug.apugli.network.s2c.integration.pehkui;

import com.google.common.collect.Maps;
import net.merchantpug.apugli.Apugli;
import net.merchantpug.apugli.integration.pehkui.LerpedApoliScaleModifier;
import net.merchantpug.apugli.network.s2c.ApugliPacketS2C;
import net.merchantpug.apugli.platform.Services;
import net.merchantpug.apugli.registry.power.ApugliPowers;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import virtuoel.pehkui.api.ScaleRegistries;

import java.util.Map;

public record MarkLerpedScaleReadyPacket(int entityId,
                                         ResourceLocation powerId,
                                         Map<ResourceLocation, Float> cachedMaxScales) implements ApugliPacketS2C {
    public static final ResourceLocation ID = Apugli.asResource("mark_lerped_scale_ready");

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(this.entityId());
        buf.writeResourceLocation(this.powerId());
        buf.writeInt(this.cachedMaxScales().size());
        for (Map.Entry<ResourceLocation, Float> entry : cachedMaxScales().entrySet()) {
            buf.writeResourceLocation(entry.getKey());
            buf.writeFloat(entry.getValue());
        }
    }

    public static MarkLerpedScaleReadyPacket decode(FriendlyByteBuf buf) {
        int entityId = buf.readInt();
        ResourceLocation powerId = buf.readResourceLocation();

        Map<ResourceLocation, Float> cachedMaxScales = Maps.newHashMap();
        int cachedMaxScalesSize = buf.readInt();

        for (int i = 0; i < cachedMaxScalesSize; ++i) {
            cachedMaxScales.put(buf.readResourceLocation(), buf.readFloat());
        }

        return new MarkLerpedScaleReadyPacket(entityId, powerId, cachedMaxScales);
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
                    Apugli.LOG.warn("Attempted loading UpdateLerpedScalePacket without Pehkui.");
                    return;
                }

                Entity entity = Minecraft.getInstance().level.getEntity(entityId());

                Object apoliModifier = ApugliPowers.MODIFY_SCALE.get().getApoliScaleModifier(powerId(), entity);

                if (!(apoliModifier instanceof LerpedApoliScaleModifier<?> lerpedApoliModifier)) {
                    Apugli.LOG.warn("Tried updating non-existent or non LerpedApoliScaleModifier.");
                    return;
                }

                for (Map.Entry<ResourceLocation, Float> entry : cachedMaxScales().entrySet()) {
                    lerpedApoliModifier.setCachedMaxScale(ScaleRegistries.getEntry(ScaleRegistries.SCALE_TYPES, entry.getKey()), entry.getValue());
                    lerpedApoliModifier.setReady(ScaleRegistries.getEntry(ScaleRegistries.SCALE_TYPES, entry.getKey()));
                }
            }
        });

    }
}
