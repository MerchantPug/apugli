package net.merchantpug.apugli.network.s2c.integration.pehkui;

import net.merchantpug.apugli.Apugli;
import net.merchantpug.apugli.integration.pehkui.ApoliScaleModifier;
import net.merchantpug.apugli.integration.pehkui.LerpedApoliScaleModifier;
import net.merchantpug.apugli.integration.pehkui.PehkuiUtil;
import net.merchantpug.apugli.network.s2c.ApugliPacketS2C;
import net.merchantpug.apugli.platform.Services;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

import java.util.Optional;

public record UpdateLerpedScalePacket(int entityId,
                                      ResourceLocation mappedScaleModifierId,
                                      int lerpTicks,
                                      Optional<Optional<Float>> previousScale) implements ApugliPacketS2C {
    public static final ResourceLocation ID = Apugli.asResource("update_lerped_scale");

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(this.entityId());

        buf.writeResourceLocation(this.mappedScaleModifierId());
        buf.writeInt(this.lerpTicks());
        buf.writeBoolean(this.previousScale().isPresent());
        this.previousScale().ifPresent(opt -> buf.writeBoolean(opt.isPresent()));
        this.previousScale().ifPresent(opt -> opt.ifPresent(buf::writeFloat));
    }

    public static UpdateLerpedScalePacket decode(FriendlyByteBuf buf) {
        int entityId = buf.readInt();

        ResourceLocation mappedScaleModifierId = buf.readResourceLocation();

        int lerpValue = buf.readInt();
        Optional<Optional<Float>> previousScale = Optional.empty();
        if (buf.readBoolean()) {
            if (buf.readBoolean()) {
                previousScale = Optional.of(Optional.of(buf.readFloat()));
            } else {
                previousScale = Optional.of(Optional.empty());
            }
        }

        return new UpdateLerpedScalePacket(entityId, mappedScaleModifierId, lerpValue, previousScale);
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

                if (entity == null) {
                    return;
                }

                ApoliScaleModifier<?> apoliModifier = PehkuiUtil.getModifierFromCache(mappedScaleModifierId(), entity);

                if (!(apoliModifier instanceof LerpedApoliScaleModifier<?> lerpedApoliModifier)) {
                    Apugli.LOG.warn("Tried updating non-existent or non LerpedApoliScaleModifier.");
                    return;
                }
                if (previousScale().isPresent()) {
                    previousScale().get().ifPresentOrElse(lerpedApoliModifier::setPreviousScale, lerpedApoliModifier::removePreviousScale);
                }
                lerpedApoliModifier.setTicks(lerpTicks());
            }
        });

    }
}
