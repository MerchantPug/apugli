package com.github.merchantpug.apugli.util;

import the.great.migration.merchantpug.apugli.Apugli;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.texture.*;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;
import java.util.regex.Pattern;

public class TextureUtil {
    public static @Nullable DynamicTexture readTextureFromUrl(String url) {
        try {
            HttpURLConnection connection = (HttpURLConnection)new URL(url).openConnection(Minecraft.getInstance().getProxy());

            connection.addRequestProperty("Content-Type", "image/png");
            connection.setConnectTimeout(ApugliConfig.fileConnectionTimeout);
            connection.setDoInput(true);
            connection.setDoOutput(false);

            if(connection.getContentLengthLong() > getFileSizeLimit()) {
                Apugli.LOGGER.warn("Tried to get texture from URL but it was too large. Increase the Apugli config's file size limit value if necessary.");
                return null;
            }

            InputStream inputStream = connection.getInputStream();;
            return new DynamicTexture(NativeImage.read(inputStream));
        } catch (Exception e) {
            Apugli.LOGGER.warn("Could not get texture from URL: ", e);
            return null;
        }
    }

    public static long getFileSizeLimit() {
        long fileSize = 1024 * 1024 * 1024;
        try {
            if(ApugliConfig.fileSizeLimit.toUpperCase(Locale.ROOT).endsWith("MB") && Pattern.matches("[0-9]+", ApugliConfig.fileSizeLimit.split("MB")[0])) {
                fileSize = Long.parseLong(ApugliConfig.fileSizeLimit.split("MB")[0]) * 1024 * 1024;
            } else if(ApugliConfig.fileSizeLimit.toUpperCase(Locale.ROOT).endsWith("KB") && Pattern.matches("[0-9]+", ApugliConfig.fileSizeLimit.split("KB")[0])) {
                fileSize = Long.parseLong(ApugliConfig.fileSizeLimit.split("KB")[0]) * 1024;
            } else if(ApugliConfig.fileSizeLimit.toUpperCase(Locale.ROOT).endsWith("B") && Pattern.matches("[0-9]+", ApugliConfig.fileSizeLimit.split("B")[0])) {
                fileSize = Long.parseLong(ApugliConfig.fileSizeLimit.split("B")[0]);
            } else {
                Apugli.LOGGER.warn("Could not parse File Size Limit in Apugli config, setting to 1MB.");
            }
        } catch (NumberFormatException ex) {
            Apugli.LOGGER.warn("Could not parse File Size Limit in Apugli config, setting to 1MB.", ex);
        }
        return fileSize;
    }

    public static void registerEntityTextureOverlayTexture(ResourceLocation id, String url) {
        AbstractTexture abstractTexture = Minecraft.getInstance().getTextureManager().getTexture(id, MissingTextureAtlasSprite.getTexture());
        if(abstractTexture == MissingTextureAtlasSprite.getTexture()) {
            DynamicTexture texture = TextureUtil.readTextureFromUrl(url);
            if(texture == null) return;
            Minecraft.getInstance().getTextureManager().register(id, texture);
        }
    }
}
