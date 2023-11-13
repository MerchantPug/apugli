package net.merchantpug.apugli.network.c2s.integration.pehkui;

import net.merchantpug.apugli.Apugli;
import net.merchantpug.apugli.integration.pehkui.LerpedApoliScaleModifier;
import net.merchantpug.apugli.network.c2s.ApugliPacketC2S;
import net.merchantpug.apugli.platform.Services;
import net.merchantpug.apugli.registry.power.ApugliPowers;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public record ResetScaleCheckPacket(ResourceLocation powerId) implements ApugliPacketC2S {
    public static final ResourceLocation ID = Apugli.asResource("mark_lerped_scale_ready");

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeResourceLocation(this.powerId());
    }

    public static ResetScaleCheckPacket decode(FriendlyByteBuf buf) {
        ResourceLocation powerId = buf.readResourceLocation();

        return new ResetScaleCheckPacket(powerId);
    }

    @Override
    public ResourceLocation getFabricId() {
        return ID;
    }

    @Override
    public void handle(MinecraftServer server, ServerPlayer player) {
        server.execute(() -> {
            if (!Services.PLATFORM.isModLoaded("pehkui")) {
                Apugli.LOG.warn("Attempted loading UpdateLerpedScalePacket without Pehkui.");
                return;
            }

            Object apoliModifier = ApugliPowers.MODIFY_SCALE.get().getApoliScaleModifier(powerId(), player);

            if (apoliModifier instanceof LerpedApoliScaleModifier<?> lerpedApoliScaleModifier) {
                lerpedApoliScaleModifier.invalidate();
            }
        });
    }
}
