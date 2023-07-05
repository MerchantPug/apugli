package net.merchantpug.apugli.network.s2c;

import net.merchantpug.apugli.Apugli;
import net.merchantpug.apugli.client.util.TextureUtilClient;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang3.tuple.Triple;

import java.util.HashSet;
import java.util.Set;

public record UpdateUrlTexturesPacket(Set<Triple<ResourceLocation, String, ResourceLocation>> urlTextures) implements ApugliPacketS2C {
    public static final ResourceLocation ID = Apugli.asResource("update_url_textures");

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(urlTextures.size());
        for (Triple<ResourceLocation, String, ResourceLocation> entry : urlTextures) {
            buf.writeResourceLocation(entry.getLeft());
            buf.writeUtf(entry.getMiddle());
            buf.writeBoolean(entry.getRight() != null);
            if (entry.getRight() != null) {
                buf.writeResourceLocation(entry.getRight());
            }
        }
    }

    public static UpdateUrlTexturesPacket decode(FriendlyByteBuf buf) {
        int powerTypesSize = buf.readInt();
        Set<Triple<ResourceLocation, String, ResourceLocation>> powerData = new HashSet<>();
        for (int i = 0; i < powerTypesSize; ++i) {
            ResourceLocation textureId = buf.readResourceLocation();
            String textureUrl = buf.readUtf();
            boolean hasTextureLocation = buf.readBoolean();
            ResourceLocation textureLocation = null;
            if (hasTextureLocation) {
                textureLocation =buf.readResourceLocation();
            }
            powerData.add(Triple.of(textureId, textureUrl, textureLocation));
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
            urlTextures.forEach((value) -> {
                if (value.getRight() != null && TextureUtilClient.doesTextureExist(value.getRight())) return;

                TextureUtilClient.registerPowerTexture(value.getLeft(), value.getMiddle(), false);
            });
            TextureUtilClient.update();
        });
    }
}
