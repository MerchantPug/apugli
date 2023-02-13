package net.merchantpug.apugli.util;

import net.merchantpug.apugli.Apugli;
import net.merchantpug.apugli.power.TextureOrUrl;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.*;
import net.minecraft.util.Identifier;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.regex.Pattern;

public class TextureUtil {
    private static long fileSizeLimit = Long.MIN_VALUE;
    private static final HashMap<Identifier, String> POWER_ID_TO_URL = new HashMap<>();
    private static final HashMap<Identifier, String> REGISTERED_TEXTURES = new HashMap<>();

    public static List<Identifier> getTexturePowers() {
        return POWER_ID_TO_URL.keySet().stream().toList();
    }

    public static HashMap<Identifier, String> getPowerIdToUrl() {
        return POWER_ID_TO_URL;
    }

    public static HashMap<Identifier, String> getRegisteredTextures() {
        return REGISTERED_TEXTURES;
    }

    public static void handleUrlTexture(Identifier id, TextureOrUrl power) {
        POWER_ID_TO_URL.put(id, power.getTextureUrl());
    }

    public static void clear() {
        for (Identifier id : REGISTERED_TEXTURES.keySet()) {
            MinecraftClient.getInstance().getTextureManager().destroyTexture(id);
        }
        REGISTERED_TEXTURES.clear();
        POWER_ID_TO_URL.clear();
    }

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

            InputStream inputStream = connection.getInputStream();
            return new NativeImageBackedTexture(NativeImage.read(inputStream));
        } catch (Exception e) {
            Apugli.LOGGER.warn("Could not get texture from URL: ", e);
            return null;
        }
    }

    public static long getFileSizeLimit() {
        if (fileSizeLimit != Long.MIN_VALUE) {
            return fileSizeLimit;
        }
        fileSizeLimit = 1024 * 1024 * 1024;
        try {
            String limit = ApugliConfig.fileSizeLimit.toUpperCase(Locale.ROOT);
            if (limit.endsWith("MB") && Pattern.matches("[0-9]+", limit.split("MB")[0])) {
                fileSizeLimit = Long.parseLong(ApugliConfig.fileSizeLimit.split("MB")[0]) * 1024 * 1024;
            } else if (limit.endsWith("KB") && Pattern.matches("[0-9]+", limit.split("KB")[0])) {
                fileSizeLimit = Long.parseLong(ApugliConfig.fileSizeLimit.split("KB")[0]) * 1024;
            } else if (limit.endsWith("B") && Pattern.matches("[0-9]+", limit.split("B")[0])) {
                fileSizeLimit = Long.parseLong(ApugliConfig.fileSizeLimit.split("B")[0]);
            } else {
                Apugli.LOGGER.warn("Could not parse File Size Limit in Apugli config, setting to 1MB.");
            }
        } catch (NumberFormatException ex) {
            Apugli.LOGGER.warn("Could not parse File Size Limit in Apugli config, setting to 1MB.", ex);
        }
        return fileSizeLimit;
    }

    public static void registerPowerTexture(Identifier id, String url) {
        NativeImageBackedTexture texture = TextureUtil.readTextureFromUrl(url);
        MinecraftClient.getInstance().getTextureManager().registerTexture(id, texture);
        REGISTERED_TEXTURES.put(id, url);
    }

    public static boolean doesTextureExist(Identifier id) {
        try (AbstractTexture abstractTexture = new ResourceTexture(id)) {
            abstractTexture.load(MinecraftClient.getInstance().getResourceManager());
            return true;
        } catch (IOException iOException) {
            return false;
        }
    }
}
