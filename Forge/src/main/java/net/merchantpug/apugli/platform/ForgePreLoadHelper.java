package net.merchantpug.apugli.platform;

import com.google.auto.service.AutoService;
import net.merchantpug.apugli.platform.services.IPreLoadHelper;
import net.minecraftforge.fml.loading.FMLLoader;

@AutoService(IPreLoadHelper.class)
public class ForgePreLoadHelper implements IPreLoadHelper {
    @Override
    public boolean isModLoadedEarly(String modId) {
        return FMLLoader.getLoadingModList().getModFileById(modId) != null;
    }

}
