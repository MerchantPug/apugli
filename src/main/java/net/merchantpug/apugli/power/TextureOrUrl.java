package net.merchantpug.apugli.power;

import net.minecraft.util.Identifier;

import javax.annotation.Nullable;

public interface TextureOrUrl {
    @Nullable
    Identifier getTextureLocation();

    @Nullable
    String getTextureUrl();

    Identifier getUrlTextureIdentifier();
}
