package net.merchantpug.apugli.networking.s2c;

import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerTypeRegistry;
import net.merchantpug.apugli.Apugli;
import net.merchantpug.apugli.power.TextureOrUrlPower;
import net.merchantpug.apugli.util.TextureUtilClient;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.HashSet;
import java.util.Set;

public record UpdateUrlTexturesPacket(Set<ResourceLocation> powerTypes) implements ApugliPacketS2C {
    public static final ResourceLocation ID = Apugli.asResource("update_url_textures");

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(powerTypes.size());
        for (ResourceLocation powerType : powerTypes) {
            buf.writeResourceLocation(powerType);
        }
    }

    public static UpdateUrlTexturesPacket decode(FriendlyByteBuf buf) {
        int powerTypesSize = buf.readInt();
        Set<ResourceLocation> powerTypes = new HashSet<>();
        for (int i = 0; i < powerTypesSize; ++i) {
            powerTypes.add(buf.readResourceLocation());
        }
        return new UpdateUrlTexturesPacket(powerTypes);
    }

    @Override
    public ResourceLocation getFabricId() {
        return ID;
    }

    @Override
    public void handle() {
        Minecraft.getInstance().execute(() -> {
            powerTypes.forEach(identifier -> {
                Power power = PowerTypeRegistry.get(identifier).create(null);
                if (!(power instanceof TextureOrUrlPower texturePower)) {
                    Apugli.LOG.warn("Tried updating texture from non TexturePower power.");
                    return;
                }

                if (texturePower.getTextureLocation() != null && TextureUtilClient.doesTextureExist(texturePower.getTextureLocation())) return;

                TextureUtilClient.registerPowerTexture(identifier, texturePower.getUrlTextureIdentifier(), texturePower.getTextureUrl(), false);
            });
            TextureUtilClient.update();
        });
    }
}
