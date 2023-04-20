package net.merchantpug.apugli;

import net.merchantpug.apugli.capability.KeyPressCapability;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Apugli.ID)
public class ApugliForgeEvents {

    @SubscribeEvent
    public static void attachCapabilities(final AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player player) {
            event.addCapability(KeyPressCapability.IDENTIFIER, new KeyPressCapability(player));
        }
    }

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        if (event.getEntity().getCapability(KeyPressCapability.INSTANCE).isPresent()) {
            event.getEntity().getCapability(KeyPressCapability.INSTANCE).ifPresent(KeyPressCapability::tick);
        }
    }

}
