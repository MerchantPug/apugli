package net.merchantpug.apugli.network.s2c.integration.pehkui;

import net.merchantpug.apugli.Apugli;
import net.merchantpug.apugli.access.ScaleDataAccess;
import net.merchantpug.apugli.integration.pehkui.ApoliScaleModifier;
import net.merchantpug.apugli.integration.pehkui.LerpedApoliScaleModifier;
import net.merchantpug.apugli.network.s2c.ApugliPacketS2C;
import net.merchantpug.apugli.platform.Services;
import net.merchantpug.apugli.registry.power.ApugliPowers;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.apache.commons.compress.utils.Lists;
import virtuoel.pehkui.api.ScaleData;
import virtuoel.pehkui.api.ScaleRegistries;
import virtuoel.pehkui.api.ScaleType;

import java.util.List;
import java.util.Optional;

public record SyncScalePacket(int entityId, List<ResourceLocation> types,
                              ResourceLocation powerId,
                              List<?> modifiers,
                              Optional<Integer> lerpTickMax,
                              Optional<Float> previousScale,
                              boolean remove,
                              boolean removeFromCache) implements ApugliPacketS2C {
    public static final ResourceLocation ID = Apugli.asResource("sync_scale");

    public SyncScalePacket(int entityId, List<ResourceLocation> types,
                           ResourceLocation mappedScaleModifierId,
                           List<?> modifiers,
                           Optional<Integer> lerpTickMax,
                           Optional<Float> previousScale) {
        this(entityId, types, mappedScaleModifierId, modifiers, lerpTickMax, previousScale, false, false);
    }

    public SyncScalePacket(int entityId, List<ResourceLocation> types,
                           ResourceLocation mappedScaleModifierId,
                           boolean removeFromCache) {
        this(entityId, types, mappedScaleModifierId, List.of(), Optional.empty(), Optional.empty(), true, removeFromCache);
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(this.entityId());

        buf.writeInt(this.types().size());
        for (int i = 0; i < this.types().size(); ++i) {
            buf.writeResourceLocation(this.types().get(i));
        }

        buf.writeResourceLocation(this.powerId());
        Services.PLATFORM.getModifiersDataType().send(buf, this.modifiers());
        buf.writeBoolean(this.lerpTickMax().isPresent());
        this.lerpTickMax().ifPresent(buf::writeInt);
        buf.writeBoolean(this.previousScale().isPresent());
        this.previousScale().ifPresent(buf::writeFloat);
        buf.writeBoolean(this.remove());
        buf.writeBoolean(this.removeFromCache());
    }

    public static SyncScalePacket decode(FriendlyByteBuf buf) {
        int entityId = buf.readInt();

        int typeSize = buf.readInt();
        List<ResourceLocation> types = Lists.newArrayList();
        for (int i = 0; i < typeSize; ++i) {
            ResourceLocation typeId = buf.readResourceLocation();
            types.add(typeId);
        }

        ResourceLocation powerId = buf.readResourceLocation();
        List<?> modifiers = Services.PLATFORM.getModifiersDataType().receive(buf);

        Optional<Integer> lerpTickMax = Optional.empty();
        if (buf.readBoolean()) {
            lerpTickMax = Optional.of(buf.readInt());
        }
        Optional<Float> previousScale = Optional.empty();
        if (buf.readBoolean()) {
            previousScale = Optional.of(buf.readFloat());
        }

        boolean remove = buf.readBoolean();
        boolean removeFromCache = buf.readBoolean();
        return new SyncScalePacket(entityId, types, powerId, modifiers, lerpTickMax, previousScale, remove, removeFromCache);
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

                if (!(entity instanceof LivingEntity living)) {
                    return;
                }

                Optional<?> power = Services.POWER.getPowers(living, ApugliPowers.MODIFY_SCALE.get(), true).stream().filter(p -> Services.POWER.getPowerId(p).equals(powerId())).findFirst();
                if (power.isEmpty()) {
                    Apugli.LOG.warn("Could not get power '" + powerId() + "' for SyncScalePacket.");
                    return;
                }
                ResourceLocation mappedScaleModifierId = ApugliPowers.MODIFY_SCALE.get().getMappedScaleModifierId(power.get());

                if (ApugliPowers.MODIFY_SCALE.get().getModifierFromCache(mappedScaleModifierId, entity) == null) {
                    if (lerpTickMax().isPresent()) {
                        ApugliPowers.MODIFY_SCALE.get().addModifierToCache(mappedScaleModifierId, entity, new LerpedApoliScaleModifier<>(power.get(), modifiers(), mappedScaleModifierId, lerpTickMax().get(), previousScale()));
                    } else {
                        ApugliPowers.MODIFY_SCALE.get().addModifierToCache(mappedScaleModifierId, entity, new ApoliScaleModifier<>(power.get(), modifiers(), mappedScaleModifierId));
                    }
                }
                ApoliScaleModifier<?> apoliModifier = ApugliPowers.MODIFY_SCALE.get().getModifierFromCache(mappedScaleModifierId, entity);

                for (ResourceLocation scaleTypeId : types()) {
                    ScaleType scaleType = ScaleRegistries.getEntry(ScaleRegistries.SCALE_TYPES, scaleTypeId);
                    ScaleData scaleData = scaleType.getScaleData(entity);
                    ((ScaleDataAccess) scaleData).apugli$removeFromApoliScaleModifiers(mappedScaleModifierId);
                    if (!remove()) {
                        ((ScaleDataAccess) scaleData).apugli$addToApoliScaleModifiers(mappedScaleModifierId);
                    }
                }

                if (removeFromCache()) {
                    ApugliPowers.MODIFY_SCALE.get().removeModifierFromCache(mappedScaleModifierId, entity);
                    ApugliPowers.MODIFY_SCALE.get().removeScaleTypesFromCache(power.get(), entity);
                }
            }
        });

    }
}
