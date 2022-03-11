package io.github.merchantpug.apugli.util;

import io.github.merchantpug.apugli.Apugli;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = Apugli.MODID)
public class ApugliClientConfig implements ConfigData {

    @ConfigEntry.Gui.CollapsibleObject
    public KeybindConfig keybindConfig = new KeybindConfig();

    public static class KeybindConfig {
        public boolean shouldRegisterKeybinds = false;
    }
}
