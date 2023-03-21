package net.merchantpug.apugli.util;

import net.merchantpug.apugli.power.TextureOrUrl;
import net.minecraft.util.Identifier;

import java.util.*;

public class TextureUtil {
    private static final HashMap<Identifier, String> POWER_ID_TO_URL = new HashMap<>();
    private static final HashMap<Identifier, String> REGISTERED_TEXTURES = new HashMap<>();
    protected static final HashMap<Identifier, String> TEXTURE_TO_SHA256 = new HashMap<>();

    public static Set<Identifier> getTexturePowers() {
        return POWER_ID_TO_URL.keySet();
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

    protected static void clearMaps() {
        POWER_ID_TO_URL.clear();
        REGISTERED_TEXTURES.clear();
        TEXTURE_TO_SHA256.clear();
    }
}
