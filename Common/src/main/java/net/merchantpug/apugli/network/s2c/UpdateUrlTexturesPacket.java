package net.merchantpug.apugli.network.s2c;

import net.merchantpug.apugli.Apugli;
import net.merchantpug.apugli.client.util.TextureUtilClient;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang3.tuple.Triple;

import java.util.HashMap;
import java.util.Map;

public record UpdateUrlTexturesPacket(Map<ResourceLocation, Triple<ResourceLocation, String, ResourceLocation>> powerData) implements ApugliPacketS2C {
    public static final ResourceLocation ID = Apugli.asResource("update_url_textures");

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(powerData.size());
        for (Map.Entry<ResourceLocation, Triple<ResourceLocation, String, ResourceLocation>> entry : powerData.entrySet()) {
            buf.writeResourceLocation(entry.getKey());
            buf.writeResourceLocation(entry.getValue().getLeft());
            buf.writeUtf(entry.getValue().getMiddle());
            buf.writeBoolean(entry.getValue().getRight() != null);
            if (entry.getValue().getRight() != null) {
                buf.writeResourceLocation(entry.getValue().getRight());
            }
        }
    }

    public static UpdateUrlTexturesPacket decode(FriendlyByteBuf buf) {
        int powerTypesSize = buf.readInt();
        Map<ResourceLocation, Triple<ResourceLocation, String, ResourceLocation>> powerData = new HashMap<>();
        for (int i = 0; i < powerTypesSize; ++i) {
            ResourceLocation powerId = buf.readResourceLocation();
            ResourceLocation textureId = buf.readResourceLocation();
            String textureUrl = buf.readUtf();
            boolean hasTextureLocation = buf.readBoolean();
            ResourceLocation textureLocation = null;
            if (hasTextureLocation) {
                textureLocation =buf.readResourceLocation();
            }
            powerData.put(powerId, Triple.of(textureId, textureUrl, textureLocation));
        }
        return new UpdateUrlTexturesPacket(powerData);
    }

    @Override
    public ResourceLocation getFabricId() {
        return ID;
    }

    @Override
    public void handle() {
        Minecraft.getInstance().execute(() -> {
            powerData.forEach((key, value) -> {
                if (value.getRight() != null && TextureUtilClient.doesTextureExist(value.getRight())) return;

                TextureUtilClient.registerPowerTexture(key, value.getLeft(), value.getMiddle(), false);
            });
            TextureUtilClient.update();
        });
    }
}
