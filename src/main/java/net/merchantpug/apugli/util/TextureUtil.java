package net.merchantpug.apugli.util;

import net.merchantpug.apugli.Apugli;
import net.merchantpug.apugli.power.TextureOrUrl;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.*;
import net.minecraft.util.Identifier;

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


}
