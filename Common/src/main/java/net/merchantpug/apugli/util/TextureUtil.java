package net.merchantpug.apugli.util;

import net.merchantpug.apugli.Apugli;
import net.merchantpug.apugli.power.TextureOrUrlPower;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang3.tuple.Triple;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class TextureUtil {
    private static final Set<Triple<ResourceLocation, String, ResourceLocation>> CACHE = new HashSet<>();

    public static Set<Triple<ResourceLocation, String, ResourceLocation>> getCache() {
        return CACHE;
    }

    public static void cachePower(ResourceLocation powerId, TextureOrUrlPower power) {
        ResourceLocation urlTextureLocation = new ResourceLocation(Apugli.ID, power.getPowerClassString().toLowerCase(Locale.ROOT) + "/" + powerId.getNamespace() + "/" + powerId.getPath());
        CACHE.add(Triple.of(urlTextureLocation, power.getTextureUrl(), power.getTextureLocation()));
    }

    public static void cacheOneOff(ResourceLocation urlTextureLocation, String url, @Nullable ResourceLocation textureLocation) {
        CACHE.add(Triple.of(urlTextureLocation, url, textureLocation));
    }
}
