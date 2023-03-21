package net.merchantpug.apugli.networking.s2c;

import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerTypeRegistry;
import net.merchantpug.apugli.Apugli;
import net.merchantpug.apugli.power.TextureOrUrl;
import net.merchantpug.apugli.util.TextureUtil;
import net.merchantpug.apugli.util.TextureUtilClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.util.HashSet;
import java.util.Set;

public record UpdateUrlTexturesPacket(Set<Identifier> powerTypes) implements ApugliPacketS2C {
    public static final Identifier ID = Apugli.identifier("update_url_textures");

    @Override
    public void encode(PacketByteBuf buf) {
        buf.writeInt(powerTypes.size());
        for (Identifier powerType : powerTypes) {
            buf.writeIdentifier(powerType);
        }
    }

    public static UpdateUrlTexturesPacket decode(PacketByteBuf buf) {
        int powerTypesSize = buf.readInt();
        Set<Identifier> powerTypes = new HashSet<>();
        for (int i = 0; i < powerTypesSize; ++i) {
            powerTypes.add(buf.readIdentifier());
        }
        return new UpdateUrlTexturesPacket(powerTypes);
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public void handle(MinecraftClient client) {
        client.execute(() -> {
            powerTypes.forEach(identifier -> {
                Power power = PowerTypeRegistry.get(identifier).create(null);
                if (!(power instanceof TextureOrUrl texturePower)) {
                    Apugli.LOGGER.warn("Tried updating texture from non TexturePower power.");
                    return;
                }

                if (texturePower.getTextureLocation() != null && TextureUtilClient.doesTextureExist(texturePower.getTextureLocation())) return;

                TextureUtilClient.registerPowerTexture(identifier, texturePower.getUrlTextureIdentifier(), texturePower.getTextureUrl(), false);
            });
            TextureUtilClient.update();
        });
    }
}
