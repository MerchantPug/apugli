package net.merchantpug.apugli.platform;

import com.google.auto.service.AutoService;
import net.merchantpug.apugli.platform.services.IConfigHelper;
import net.merchantpug.apugli.util.ApugliConfigs;

@AutoService(IConfigHelper.class)
public class ForgeConfigHelper implements IConfigHelper {
    @Override
    public String getFileSizeLimit() {
        return ApugliConfigs.CLIENT.fileDownloadOptions.getFileSizeLimit();
    }

    @Override
    public int getFileConnectionTimeout() {
        return ApugliConfigs.CLIENT.fileDownloadOptions.getFileConnectionTimeout();
    }

    @Override
    public boolean shouldPerformVersionCheck() {
        return ApugliConfigs.SERVER.shouldPerformVersionCheck();
    }
}
