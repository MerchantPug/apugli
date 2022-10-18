package the.great.migration.merchantpug.apugli.integration;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import eu.midnightdust.core.config.MidnightLibConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import the.great.migration.merchantpug.apugli.Apugli;

@Environment(EnvType.CLIENT)
public class ApugliModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> MidnightLibConfig.getScreen(parent, Apugli.MODID);
    }
}
