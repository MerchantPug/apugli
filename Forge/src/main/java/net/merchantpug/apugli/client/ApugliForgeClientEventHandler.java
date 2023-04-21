package net.merchantpug.apugli.client;

import io.github.edwinmindcraft.apoli.api.power.IActivePower;
import net.merchantpug.apugli.Apugli;
import net.merchantpug.apugli.capability.KeyPressCapability;
import net.merchantpug.apugli.mixin.forge.common.accessor.ApoliClientEventHandlerAccessor;
import net.merchantpug.apugli.network.ApugliPacketHandler;
import net.merchantpug.apugli.network.c2s.UpdateKeysPressedPacket;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

@Mod.EventBusSubscriber(modid = Apugli.ID, value = Dist.CLIENT)
public class ApugliForgeClientEventHandler {
    private static HashMap<String, Boolean> lastKeyBindingStates = new HashMap<>();

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        handleActiveKeys();
    }

    public static void handleActiveKeys() {
        Player player = Minecraft.getInstance().player;
        if (player == null) return;
        Set<IActivePower.Key> addedKeys = new HashSet<>();
        Set<IActivePower.Key> removedKeys = new HashSet<>();
        HashMap<String, Boolean> currentKeyBindingStates = new HashMap<>();
        player.getCapability(KeyPressCapability.INSTANCE).ifPresent(capability -> {
            if (!capability.getKeysToCheck().isEmpty()) {
                if (!ApoliClientEventHandlerAccessor.getInitializedKeyBindingMap()) {
                    ApoliClientEventHandlerAccessor.setInitializedKeyBindingMap(true);
                    Minecraft client = Minecraft.getInstance();
                    for (int i = 0; i < client.options.keyMappings.length; i++) {
                        ApoliClientEventHandlerAccessor.getIdToKeyBindingMap().put(client.options.keyMappings[i].getName(), client.options.keyMappings[i]);
                    }
                }
                capability.getKeysToCheck().forEach(key -> {
                    KeyMapping keyBinding = ApoliClientEventHandlerAccessor.getIdToKeyBindingMap().get(key.key());
                    if (keyBinding != null) {
                        if (!currentKeyBindingStates.containsKey(key.key())) {
                            currentKeyBindingStates.put(key.key(), keyBinding.isDown());
                        }
                        if (currentKeyBindingStates.get(key.key()) && (key.continuous() || !lastKeyBindingStates.getOrDefault(key.key(), false))) {
                            capability.addKey(key);
                            if (!lastKeyBindingStates.getOrDefault(key.key(), false)) {
                                addedKeys.add(key);
                            }
                        } else if ((!currentKeyBindingStates.get(key.key()) || !key.continuous()) && lastKeyBindingStates.getOrDefault(key.key(), false)) {
                            capability.removeKey(key);
                            removedKeys.add(key);
                        }
                    }
                });
                ApugliPacketHandler.sendC2S(new UpdateKeysPressedPacket(addedKeys, removedKeys));
            }
            lastKeyBindingStates = currentKeyBindingStates;
        });
    }

}
