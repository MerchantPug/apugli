<<<<<<<< HEAD:src/main/java/net/merchantpug/apugli/integration/ApugliModMenuIntegration.java
package net.merchantpug.apugli.integration;

import net.merchantpug.apugli.Apugli;
========
package the.great.migration.merchantpug.apugli.integration;

>>>>>>>> pr/25:Fabric/src/main/java/the/great/migration/merchantpug/apugli/integration/ApugliModMenuIntegration.java
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
