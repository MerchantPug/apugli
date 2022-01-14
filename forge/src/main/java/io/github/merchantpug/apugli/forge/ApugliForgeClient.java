package io.github.merchantpug.apugli.forge;

import io.github.merchantpug.apugli.ApugliClient;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ApugliForgeClient {
    public static void initialize() {
        ApugliClient.register();
    }
}