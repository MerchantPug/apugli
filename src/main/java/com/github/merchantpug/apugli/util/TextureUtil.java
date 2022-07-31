package com.github.merchantpug.apugli.util;

import com.github.merchantpug.apugli.Apugli;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.*;
import net.minecraft.util.Identifier;

import javax.annotation.Nullable;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class TextureUtil {
    public static @Nullable NativeImageBackedTexture readTextureFromUrl(String url) {
        try {
            HttpURLConnection connection = (HttpURLConnection)new URL(url).openConnection(MinecraftClient.getInstance().getNetworkProxy());

            connection.addRequestProperty("Content-Type", "image/png");
            connection.setDoInput(true);
            connection.setDoOutput(false);

            InputStream inputStream = connection.getInputStream();;
            return new NativeImageBackedTexture(NativeImage.read(inputStream));
        } catch (Exception e) {
            Apugli.LOGGER.warn("Could not read texture from URL {}: ", url, e);
            return null;
        }
    }

    public static void registerEntityTextureOverlayTexture(Identifier id, String url) {
        AbstractTexture abstractTexture = MinecraftClient.getInstance().getTextureManager().getOrDefault(id, MissingSprite.getMissingSpriteTexture());
        if (abstractTexture == MissingSprite.getMissingSpriteTexture()) {
            NativeImageBackedTexture texture = TextureUtil.readTextureFromUrl(url);
            if (texture == null) return;
            MinecraftClient.getInstance().getTextureManager().registerTexture(id, texture);
        }
    }
}
