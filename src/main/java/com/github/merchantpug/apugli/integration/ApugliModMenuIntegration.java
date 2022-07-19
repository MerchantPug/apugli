package com.github.merchantpug.apugli.integration;

import com.github.merchantpug.apugli.Apugli;
import com.github.merchantpug.apugli.util.ApugliConfig;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import eu.midnightdust.core.config.MidnightLibConfig;
import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ApugliModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> MidnightLibConfig.getScreen(parent, Apugli.MODID);
    }
}
