package io.github.merchantpug.apugli.fabric;

import io.github.merchantpug.apugli.Apugli;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ApugliFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        FabricLoader.getInstance().getModContainer(Apugli.MODID).ifPresent(modContainer -> {
            Apugli.VERSION = modContainer.getMetadata().getVersion().getFriendlyString();
        });

        Apugli.init();
    }
}
