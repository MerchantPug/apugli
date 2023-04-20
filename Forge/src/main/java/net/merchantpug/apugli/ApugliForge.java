package net.merchantpug.apugli;

import net.merchantpug.apugli.network.ApugliPacketHandler;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Apugli.ID)
public class ApugliForge {
    
    public ApugliForge() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        Apugli.VERSION = ModLoadingContext.get().getActiveContainer().getModInfo().getVersion().toString();

        Apugli.init();
        addModBusEventListeners(eventBus);
    }

    private void addModBusEventListeners(IEventBus eventBus) {
        eventBus.addListener((FMLCommonSetupEvent event) -> {
            ApugliPacketHandler.register();
        });
    }

}