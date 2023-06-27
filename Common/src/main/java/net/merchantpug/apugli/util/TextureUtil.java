package net.merchantpug.apugli.util;

import net.merchantpug.apugli.Apugli;
import net.merchantpug.apugli.platform.Services;
import net.merchantpug.apugli.power.TextureOrUrlPower;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang3.tuple.Triple;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class TextureUtil {
    private static final Map<ResourceLocation, Triple<ResourceLocation, String, ResourceLocation>> CACHE = new HashMap<>();

    public static Map<ResourceLocation, Triple<ResourceLocation, String, ResourceLocation>> getCache() {
        return CACHE;
    }

    public static void cachePower(ResourceLocation powerId, TextureOrUrlPower power) {
        ResourceLocation urlTextureLocation = new ResourceLocation(Apugli.ID, power.getPowerClassString().toLowerCase(Locale.ROOT) + "/" + powerId.getNamespace() + "/" + powerId.getPath());
        CACHE.put(powerId, Triple.of(urlTextureLocation, power.getTextureUrl(), power.getTextureLocation()));
    }
}
