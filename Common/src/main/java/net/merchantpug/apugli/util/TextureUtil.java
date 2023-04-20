package net.merchantpug.apugli.util;

import net.merchantpug.apugli.power.TextureOrUrlPower;
import net.minecraft.resources.ResourceLocation;
import java.util.HashMap;
import java.util.Set;

public class TextureUtil {
    private static final HashMap<ResourceLocation, String> POWER_ID_TO_URL = new HashMap<>();
    private static final HashMap<ResourceLocation, String> REGISTERED_TEXTURES = new HashMap<>();
    protected static final HashMap<ResourceLocation, String> TEXTURE_TO_SHA256 = new HashMap<>();

    public static Set<ResourceLocation> getTexturePowers() {
        return POWER_ID_TO_URL.keySet();
    }

    public static HashMap<ResourceLocation, String> getPowerIdToUrl() {
        return POWER_ID_TO_URL;
    }

    public static HashMap<ResourceLocation, String> getRegisteredTextures() {
        return REGISTERED_TEXTURES;
    }

    public static void handleUrlTexture(ResourceLocation id, TextureOrUrlPower power) {
        POWER_ID_TO_URL.put(id, power.getTextureUrl());
    }

    protected static void clearMaps() {
        POWER_ID_TO_URL.clear();
        REGISTERED_TEXTURES.clear();
        TEXTURE_TO_SHA256.clear();
    }
}
