package com.github.merchantpug.apugli.util;

import com.github.merchantpug.apugli.Apugli;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = Apugli.MODID)
public class ApugliConfig implements ConfigData {
    @ConfigEntry.Gui.CollapsibleObject
    public HitsOnTargetConfig hitsOnTarget = new HitsOnTargetConfig();

    public static class HitsOnTargetConfig {
        public int resetTimerTicks = 100;
    }
}
