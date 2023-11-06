package net.merchantpug.apugli.network.c2s.integration.pehkui;

import net.merchantpug.apugli.Apugli;
import net.merchantpug.apugli.network.c2s.ApugliPacketC2S;
import net.merchantpug.apugli.platform.Services;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import virtuoel.pehkui.api.ScaleRegistries;
import virtuoel.pehkui.api.ScaleType;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public record UpdateScalesPacket(int entityId, List<ResourceLocation> scaleTypes) implements ApugliPacketC2S {
    public static final ResourceLocation ID = Apugli.asResource("update_scales");

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(this.entityId());
        buf.writeInt(this.scaleTypes().size());
        for (ResourceLocation id : this.scaleTypes()) {

        }
    }

    public static UpdateScalesPacket decode(FriendlyByteBuf buf) {
        int entityId = buf.readInt();
        List<ResourceLocation> spdList = new ArrayList<>();
        int spdListSize = buf.readInt();
        for (int i = 0; i < spdListSize; ++i) {
            spdList.add(buf.readResourceLocation());
        }

        return new UpdateScalesPacket(entityId, spdList);
    }

    @Override
    public ResourceLocation getFabricId() {
        return ID;
    }

    @Override
    public void handle(MinecraftServer server, ServerPlayer player) {
        // The lambda implementation of this Runnable breaks Forge servers.
        server.execute(new Runnable() {
            @Override
            public void run() {
                if (!Services.PLATFORM.isModLoaded("pehkui")) {
                    Apugli.LOG.warn("Attempted loading UpdateScalesPacket without Pehkui.");
                }

                Entity entity = player.level().getEntity(entityId());

                Set<ScaleType> scaleTypes = scaleTypes().stream().map(id -> ScaleRegistries.getEntry(ScaleRegistries.SCALE_TYPES, id)).collect(Collectors.toSet());
                scaleTypes.forEach(scaleType -> scaleType.getScaleData(entity).onUpdate());
            }
        });

    }
}