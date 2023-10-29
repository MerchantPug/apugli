package net.merchantpug.apugli.network.s2c.integration.pehkui;

import net.merchantpug.apugli.Apugli;
import net.merchantpug.apugli.network.s2c.ApugliPacketS2C;
import net.merchantpug.apugli.platform.Services;
import net.merchantpug.apugli.registry.power.ApugliPowers;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public record ClearScaleModifierCachePacket() implements ApugliPacketS2C {
    public static final ResourceLocation ID = Apugli.asResource("clear_scale_modifier_cache");

    @Override
    public void encode(FriendlyByteBuf buf) {
    }

    public static ClearScaleModifierCachePacket decode(FriendlyByteBuf buf) {
        return new ClearScaleModifierCachePacket();
    }

    @Override
    public ResourceLocation getFabricId() {
        return ID;
    }

    @Override
    public void handle() {
        Minecraft.getInstance().execute(() -> {
            if (!Services.PLATFORM.isModLoaded("pehkui")) {
                Apugli.LOG.warn("Attempted loading ClearScaleModifierCachePacket without Pehkui.");
                return;
            }

            ApugliPowers.MODIFY_SCALE.get().clearModifiersFromCache();
        });

    }
}
