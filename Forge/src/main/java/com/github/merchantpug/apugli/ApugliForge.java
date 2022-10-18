package com.github.merchantpug.apugli;

import net.minecraftforge.fml.common.Mod;

@Mod(Apugli.ID)
public class ApugliForge {
    
    public ApugliForge() {
    
        // This method is invoked by the Forge mod loader when it is ready
        // to load your mod. You can access Forge and Common code in this
        // project.
    
        // Use Forge to bootstrap the Common mod.
        Apugli.LOG.info("Hello Forge world!");
        Apugli.init();
    }
}