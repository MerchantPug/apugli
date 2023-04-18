<<<<<<<< HEAD:src/main/java/net/merchantpug/apugli/ApugliPreLaunch.java
package net.merchantpug.apugli;
========
package the.great.migration.merchantpug.apugli;
>>>>>>>> pr/25:Fabric/src/main/java/the/great/migration/merchantpug/apugli/ApugliPreLaunch.java

import com.llamalad7.mixinextras.MixinExtrasBootstrap;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;

public class ApugliPreLaunch implements PreLaunchEntrypoint {
    @Override
    public void onPreLaunch() {
        MixinExtrasBootstrap.init();
    }
}
