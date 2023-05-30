package net.merchantpug.apugli;

import net.merchantpug.apugli.network.ApugliPacketHandler;
import net.merchantpug.apugli.util.ApugliConfigs;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Apugli.ID)
public class ApugliForge {
    
    public ApugliForge() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        String version = ModLoadingContext.get().getActiveContainer().getModInfo().getVersion().toString();
        if (version.contains("+")) {
            version = version.split("\\+")[0];
        }
        if (version.contains("-")) {
            version = version.split("-")[0];
        }
        Apugli.VERSION = version;

        Apugli.init();


        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ApugliConfigs.CLIENT_SPECS);
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, ApugliConfigs.SERVER_SPECS);

        eventBus.addListener((FMLCommonSetupEvent event) -> {
            ApugliPacketHandler.register();
        });
    }

}