package net.merchantpug.apugli.networking.s2c;

import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerTypeRegistry;
import net.merchantpug.apugli.Apugli;
import net.merchantpug.apugli.power.TextureOrUrl;
import net.merchantpug.apugli.util.TextureUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public record UpdateUrlTexturesPacket(List<Identifier> powerTypes) implements ApugliPacketS2C {
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
        List<Identifier> powerTypes = new ArrayList<>();
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
            TextureUtil.clear();
            powerTypes.forEach(identifier -> {
                Power power = PowerTypeRegistry.get(identifier).create(null);
                if (!(power instanceof TextureOrUrl texturePower)) {
                    Apugli.LOGGER.warn("Tried updating texture from non TexturePower power.");
                    return;
                }

                if (texturePower.getTextureLocation() != null && TextureUtil.doesTextureExist(texturePower.getTextureLocation())) return;

                TextureUtil.registerPowerTexture(texturePower.getUrlTextureIdentifier(), texturePower.getTextureUrl());
                TextureUtil.getPowerIdToUrl().put(identifier, texturePower.getTextureUrl());
            });
        });
    }
}
