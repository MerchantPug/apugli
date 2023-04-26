package net.merchantpug.apugli.platform;

import com.google.auto.service.AutoService;
import net.merchantpug.apugli.platform.services.IConfigHelper;
import net.merchantpug.apugli.util.ApugliConfig;

@SuppressWarnings("unchecked")
@AutoService(IConfigHelper.class)
public class FabricConfigHelper implements IConfigHelper {

    @Override
    public String getFileSizeLimit() {
        return ApugliConfig.fileSizeLimit;
    }

    @Override
    public int getFileConnectionTimeout() {
        return ApugliConfig.fileConnectionTimeout;
    }

    @Override
    public boolean shouldPerformVersionCheck() {
        return ApugliConfig.performVersionCheck;
    }

}
