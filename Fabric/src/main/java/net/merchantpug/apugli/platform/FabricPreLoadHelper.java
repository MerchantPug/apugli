package net.merchantpug.apugli.platform;

import com.google.auto.service.AutoService;
import net.fabricmc.loader.api.FabricLoader;
import net.merchantpug.apugli.platform.services.IPreLoadHelper;

@AutoService(IPreLoadHelper.class)
public class FabricPreLoadHelper implements IPreLoadHelper {
    @Override
    public boolean isModLoadedEarly(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }

}
