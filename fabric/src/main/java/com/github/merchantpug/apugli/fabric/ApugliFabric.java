package com.github.merchantpug.apugli.fabric;

import com.github.merchantpug.apugli.Apugli;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public class ApugliFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        FabricLoader.getInstance().getModContainer(Apugli.MODID).ifPresent(modContainer -> {
            Apugli.VERSION = modContainer.getMetadata().getVersion().getFriendlyString();
        });

        Apugli.init();
    }
}
