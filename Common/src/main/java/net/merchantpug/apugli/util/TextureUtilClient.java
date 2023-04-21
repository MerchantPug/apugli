package net.merchantpug.apugli.util;

import com.google.common.hash.Hashing;
import com.mojang.blaze3d.platform.NativeImage;
import net.merchantpug.apugli.Apugli;
import net.merchantpug.apugli.mixin.xplatform.client.accessor.TextureManagerAccessor;
import net.merchantpug.apugli.platform.Services;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
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

public class TextureUtilClient {
    private static long fileSizeLimit = Long.MIN_VALUE;
    private static final HashMap<ResourceLocation, String> TEMP_POWER_ID_TO_URL = new HashMap<>();
    private static final HashMap<ResourceLocation, String> TEMP_REGISTERED_TEXTURES = new HashMap<>();
    private static final HashMap<ResourceLocation, String> TEMP_TEXTURE_TO_SHA256 = new HashMap<>();

    public static void registerPowerTexture(@Nullable ResourceLocation powerId, ResourceLocation textureId, String url, boolean refresh) {
        byte[] bytes = readTextureFromUrl(url);
        if (bytes == null) return;

        String sha256Hash = Hashing.sha256().hashBytes(bytes).toString();

        if (!TextureUtil.TEXTURE_TO_SHA256.containsKey(textureId) || !TextureUtil.TEXTURE_TO_SHA256.get(textureId).equals(sha256Hash) || refresh) {
            NativeImage texture;
            try {
                texture = NativeImage.read(new ByteArrayInputStream(bytes));
            } catch (IOException e) {
                Apugli.LOG.warn("Could not read texture from input: ", e);
                return;
            }
            TextureManager manager = Minecraft.getInstance().getTextureManager();
            DynamicTexture nativeImageBacked;
            if (!refresh && ((TextureManagerAccessor)manager).getTextures().containsKey(textureId) && ((TextureManagerAccessor)manager).getTextures().get(textureId) instanceof DynamicTexture existingTexture) {
                existingTexture.setPixels(texture);
                existingTexture.upload();
                nativeImageBacked = existingTexture;
            } else {
                nativeImageBacked = new DynamicTexture(texture);
            }
            Minecraft.getInstance().getTextureManager().register(textureId, nativeImageBacked);
            if (!refresh) {
                TEMP_REGISTERED_TEXTURES.put(textureId, url);
                TEMP_TEXTURE_TO_SHA256.put(textureId, sha256Hash);
                if (powerId == null) return;
                TEMP_POWER_ID_TO_URL.put(powerId, url);
            }
        }
    }

    public static boolean doesTextureExist(ResourceLocation id) {
        return ((TextureManagerAccessor)Minecraft.getInstance().getTextureManager()).getTextures().containsKey(id);
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
        for (ResourceLocation id : TextureUtil.getRegisteredTextures().keySet()) {
            Minecraft.getInstance().getTextureManager().release(id);
        }
        TextureUtil.clearMaps();
        fileSizeLimit = Long.MIN_VALUE;
    }

    public static @Nullable byte[] readTextureFromUrl(String url) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection(Minecraft.getInstance().getProxy());

            connection.addRequestProperty("Content-Type", "image/png");
            connection.setConnectTimeout(Services.CONFIG.getFileConnectionTimeout());
            connection.setDoInput(true);
            connection.setDoOutput(false);

            if (connection.getContentLengthLong() > getFileSizeLimit()) {
                Apugli.LOG.warn("Tried to get texture from URL but it was too large. Increase the Apugli config's file size limit value if necessary.");
                return null;
            }

            InputStream inputStream = connection.getInputStream();

            return IOUtils.toByteArray(inputStream);
        } catch (Exception e) {
            Apugli.LOG.warn("Could not get texture from URL: ", e);
            return null;
        }
    }

    public static long getFileSizeLimit() {
        if (fileSizeLimit != Long.MIN_VALUE) {
            return fileSizeLimit;
        }
        fileSizeLimit = 1024 * 1024 * 1024;
        try {
            String limit = Services.CONFIG.getFileSizeLimit().toUpperCase(Locale.ROOT);
            if (limit.endsWith("MB") && Pattern.matches("[0-9]+", limit.split("MB")[0])) {
                fileSizeLimit = Long.parseLong(Services.CONFIG.getFileSizeLimit().split("MB")[0]) * 1024 * 1024;
            } else if (limit.endsWith("KB") && Pattern.matches("[0-9]+", limit.split("KB")[0])) {
                fileSizeLimit = Long.parseLong(Services.CONFIG.getFileSizeLimit().split("KB")[0]) * 1024;
            } else if (limit.endsWith("B") && Pattern.matches("[0-9]+", limit.split("B")[0])) {
                fileSizeLimit = Long.parseLong(Services.CONFIG.getFileSizeLimit().split("B")[0]);
            } else {
                Apugli.LOG.warn("Could not parse File Size Limit in Apugli config, setting to 1MB.");
            }
        } catch (NumberFormatException ex) {
            Apugli.LOG.warn("Could not parse File Size Limit in Apugli config, setting to 1MB.", ex);
        }
        return fileSizeLimit;
    }
}
