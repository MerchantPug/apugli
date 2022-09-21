package net.merchantpug.apugli.util;

import net.merchantpug.apugli.Apugli;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.*;
import net.minecraft.util.Identifier;

import javax.annotation.Nullable;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;
import java.util.regex.Pattern;

public class TextureUtil {
    public static @Nullable NativeImageBackedTexture readTextureFromUrl(String url) {
        try {
            HttpURLConnection connection = (HttpURLConnection)new URL(url).openConnection(MinecraftClient.getInstance().getNetworkProxy());

            connection.addRequestProperty("Content-Type", "image/png");
            connection.setConnectTimeout(ApugliConfig.fileConnectionTimeout);
            connection.setDoInput(true);
            connection.setDoOutput(false);

            if (connection.getContentLengthLong() > getFileSizeLimit()) {
                Apugli.LOGGER.warn("Tried to get texture from URL but it was too large. Increase the Apugli config's file size limit value if necessary.");
                return null;
            }

            InputStream inputStream = connection.getInputStream();;
            return new NativeImageBackedTexture(NativeImage.read(inputStream));
        } catch (Exception e) {
            Apugli.LOGGER.warn("Could not get texture from URL: ", e);
            return null;
        }
    }

    public static long getFileSizeLimit() {
        long fileSize = 1024 * 1024 * 1024;
        try {
            if (ApugliConfig.fileSizeLimit.toUpperCase(Locale.ROOT).endsWith("MB") && Pattern.matches("[0-9]+", ApugliConfig.fileSizeLimit.split("MB")[0])) {
                fileSize = Long.parseLong(ApugliConfig.fileSizeLimit.split("MB")[0]) * 1024 * 1024;
            } else if (ApugliConfig.fileSizeLimit.toUpperCase(Locale.ROOT).endsWith("KB") && Pattern.matches("[0-9]+", ApugliConfig.fileSizeLimit.split("KB")[0])) {
                fileSize = Long.parseLong(ApugliConfig.fileSizeLimit.split("KB")[0]) * 1024;
            } else if (ApugliConfig.fileSizeLimit.toUpperCase(Locale.ROOT).endsWith("B") && Pattern.matches("[0-9]+", ApugliConfig.fileSizeLimit.split("B")[0])) {
                fileSize = Long.parseLong(ApugliConfig.fileSizeLimit.split("B")[0]);
            } else {
                Apugli.LOGGER.warn("Could not parse File Size Limit in Apugli config, setting to 1MB.");
            }
        } catch (NumberFormatException ex) {
            Apugli.LOGGER.warn("Could not parse File Size Limit in Apugli config, setting to 1MB.", ex);
        }
        return fileSize;
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
