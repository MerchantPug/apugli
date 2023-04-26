package net.merchantpug.apugli.client;

import io.github.edwinmindcraft.apoli.api.power.IActivePower;
import net.merchantpug.apugli.Apugli;
import net.merchantpug.apugli.capability.KeyPressCapability;
import net.merchantpug.apugli.client.renderer.EnergySwirlLayer;
import net.merchantpug.apugli.client.renderer.EntityTextureOverlayLayer;
import net.merchantpug.apugli.mixin.forge.client.accessor.EntityRenderersEventAddLayersAccessor;
import net.merchantpug.apugli.mixin.forge.client.accessor.PlayerModelAccessor;
import net.merchantpug.apugli.mixin.forge.common.accessor.ApoliClientEventHandlerAccessor;
import net.merchantpug.apugli.network.ApugliPacketHandler;
import net.merchantpug.apugli.network.c2s.UpdateKeysPressedPacket;
import net.merchantpug.apugli.platform.Services;
import net.merchantpug.apugli.registry.power.ApugliPowers;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MushroomCowRenderer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RenderNameTagEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

@Mod.EventBusSubscriber(modid = Apugli.ID, value = Dist.CLIENT)
public class ApugliForgeClientEventHandler {
    private static HashMap<String, Boolean> lastKeyBindingStates = new HashMap<>();

    @SubscribeEvent
    public static void preventNameTagRender(RenderNameTagEvent event) {
        if (!(event.getEntity() instanceof Player otherPlayer)) return;

        Player localPlayer = Minecraft.getInstance().player;
        if(Services.POWER.getPowers(otherPlayer, ApugliPowers.PREVENT_LABEL_RENDER.get()).stream().anyMatch(power -> power.shouldHide(localPlayer))) {
            event.setResult(Event.Result.DENY);
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        handleActiveKeys();
    }

    @SubscribeEvent
    public static void registerRenderLayers(EntityRenderersEvent.AddLayers event) {
        ((EntityRenderersEventAddLayersAccessor)event).getRenderers().forEach((entityType, entityRenderer) -> {
            if (!(entityRenderer instanceof LivingEntityRenderer livingEntityRenderer)) return;
            livingEntityRenderer.addLayer(new EnergySwirlLayer(livingEntityRenderer));
            livingEntityRenderer.addLayer(new EntityTextureOverlayLayer(livingEntityRenderer, ((PlayerModelAccessor)livingEntityRenderer.getModel()).isSlim(), event.getEntityModels()));
        });

        LivingEntityRenderer<Player, EntityModel<Player>> playerRenderer = event.getRenderer(EntityType.PLAYER);
        playerRenderer.addLayer(new EnergySwirlLayer<>(playerRenderer));
        playerRenderer.addLayer(new EntityTextureOverlayLayer<>(playerRenderer, ((PlayerModelAccessor)playerRenderer.getModel()).isSlim(), event.getEntityModels()));
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
