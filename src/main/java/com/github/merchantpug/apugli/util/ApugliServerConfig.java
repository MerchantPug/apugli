package com.github.merchantpug.apugli.util;

import com.github.merchantpug.apugli.Apugli;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;

@Config(name = Apugli.MODID + "_server")
public class ApugliServerConfig implements ConfigData {
    public boolean performVersionCheck = true;
}
