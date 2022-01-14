package io.github.merchantpug.apugli.fabric;

import io.github.merchantpug.apugli.Apugli;
import net.fabricmc.api.ModInitializer;

public class ApugliFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        Apugli.init();
    }
}
