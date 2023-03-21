package net.merchantpug.apugli.util;

import com.google.common.hash.Hashing;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.merchantpug.apugli.Apugli;
import net.merchantpug.apugli.mixin.client.TextureManagerAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.*;
import net.minecraft.util.Identifier;
import org.apache.commons.io.IOUtils;

import javax.annotation.Nullable;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Locale;
import java.util.regex.Pattern;

@Environment(EnvType.CLIENT)
public class TextureUtilClient {
    private static long fileSizeLimit = Long.MIN_VALUE;
    private static final HashMap<Identifier, String> TEMP_POWER_ID_TO_URL = new HashMap<>();
    private static final HashMap<Identifier, String> TEMP_REGISTERED_TEXTURES = new HashMap<>();
    private static final HashMap<Identifier, String> TEMP_TEXTURE_TO_SHA256 = new HashMap<>();

    public static void registerPowerTexture(@Nullable Identifier powerId, Identifier textureId, String url, boolean refresh) {
        byte[] bytes = readTextureFromUrl(url);
        if (bytes == null) return;

        String sha256Hash = Hashing.sha256().hashBytes(bytes).toString();

        if (!TextureUtil.TEXTURE_TO_SHA256.containsKey(textureId) || !TextureUtil.TEXTURE_TO_SHA256.get(textureId).equals(sha256Hash) || refresh) {
            NativeImage texture;
            try {
                texture = NativeImage.read(new ByteArrayInputStream(bytes));
            } catch (IOException e) {
                Apugli.LOGGER.warn("Could not read texture from input: ", e);
                return;
            }
            TextureManager manager = MinecraftClient.getInstance().getTextureManager();
            NativeImageBackedTexture nativeImageBacked;
            if (!refresh && ((TextureManagerAccessor)manager).textures().containsKey(textureId) && ((TextureManagerAccessor)manager).textures().get(textureId) instanceof NativeImageBackedTexture existingTexture) {
                existingTexture.setImage(texture);
                existingTexture.upload();
                nativeImageBacked = existingTexture;
            } else {
                nativeImageBacked = new NativeImageBackedTexture(texture);
            }
            MinecraftClient.getInstance().getTextureManager().registerTexture(textureId, nativeImageBacked);
            if (!refresh) {
                TEMP_REGISTERED_TEXTURES.put(textureId, url);
                TEMP_TEXTURE_TO_SHA256.put(textureId, sha256Hash);
                if (powerId == null) return;
                TEMP_POWER_ID_TO_URL.put(powerId, url);
            }
        }
    }

    public static boolean doesTextureExist(Identifier id) {
        return ((TextureManagerAccessor)MinecraftClient.getInstance().getTextureManager()).textures().containsKey(id);
    }

    public static void update() {
        TextureUtil.getRegisteredTextures().keySet().removeIf(entry -> !TEMP_REGISTERED_TEXTURES.containsKey(entry));
        TextureUtil.getPowerIdToUrl().keySet().removeIf(entry -> !TEMP_POWER_ID_TO_URL.containsKey(entry));
        TextureUtil.TEXTURE_TO_SHA256.keySet().removeIf(entry -> !TEMP_TEXTURE_TO_SHA256.containsKey(entry));
        clearTempMaps();
        fileSizeLimit = Long.MIN_VALUE;
    }

    private static void clearTempMaps() {
        TEMP_POWER_ID_TO_URL.clear();
        TEMP_REGISTERED_TEXTURES.clear();
        TEMP_TEXTURE_TO_SHA256.clear();
    }

    public static void clear() {
        for (Identifier id : TextureUtil.getRegisteredTextures().keySet()) {
            MinecraftClient.getInstance().getTextureManager().destroyTexture(id);
        }
        TextureUtil.clearMaps();
        fileSizeLimit = Long.MIN_VALUE;
    }

    public static @Nullable byte[] readTextureFromUrl(String url) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection(MinecraftClient.getInstance().getNetworkProxy());

            connection.addRequestProperty("Content-Type", "image/png");
            connection.setConnectTimeout(ApugliConfig.fileConnectionTimeout);
            connection.setDoInput(true);
            connection.setDoOutput(false);

            if (connection.getContentLengthLong() > getFileSizeLimit()) {
                Apugli.LOGGER.warn("Tried to get texture from URL but it was too large. Increase the Apugli config's file size limit value if necessary.");
                return null;
            }

            InputStream inputStream = connection.getInputStream();

            return IOUtils.toByteArray(inputStream);
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
}
