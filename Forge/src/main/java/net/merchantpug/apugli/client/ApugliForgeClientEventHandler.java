package net.merchantpug.apugli.client;

import io.github.edwinmindcraft.apoli.api.power.IActivePower;
import net.merchantpug.apugli.Apugli;
import net.merchantpug.apugli.access.HumanoidMobRendererAccess;
import net.merchantpug.apugli.capability.entity.KeyPressCapability;
import net.merchantpug.apugli.client.renderer.*;
import net.merchantpug.apugli.client.util.TextureUtilClient;
import net.merchantpug.apugli.condition.factory.entity.CachedBlockInRadiusCondition;
import net.merchantpug.apugli.mixin.forge.client.accessor.EntityRenderersEventAddLayersAccessor;
import net.merchantpug.apugli.mixin.forge.client.accessor.PlayerModelAccessor;
import net.merchantpug.apugli.mixin.forge.common.accessor.ApoliClientEventHandlerAccessor;
import net.merchantpug.apugli.network.ApugliPacketHandler;
import net.merchantpug.apugli.network.c2s.UpdateKeysPressedPacket;
import net.merchantpug.apugli.platform.Services;
import net.merchantpug.apugli.registry.ApugliEntityTypes;
import net.merchantpug.apugli.registry.power.ApugliPowers;
import net.merchantpug.apugli.util.FOVUtil;
import net.minecraft.client.Camera;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RenderNameTagEvent;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class ApugliForgeClientEventHandler {

    @Mod.EventBusSubscriber(modid = Apugli.ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
    public static class ForgeEvents {
        private static HashMap<String, Boolean> lastKeyBindingStates = new HashMap<>();

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

        @SubscribeEvent(priority = EventPriority.LOWEST)
        public static void updateFov(ViewportEvent.ComputeFov event) {
            Camera activeRenderInfo = event.getCamera();
            double partialTicks = event.getPartialTick();
            boolean useFOVSetting = event.usedConfiguredFov();

            if (useFOVSetting && activeRenderInfo.getEntity() instanceof LivingEntity living) {
                double fov = FOVUtil.undoModifications(event.getFOV(), activeRenderInfo, partialTicks);
                event.setFOV(ApugliPowers.MODIFY_FOV.get().getFov(fov, activeRenderInfo, living));
            }
        }

        @SubscribeEvent
        public static void onClientTick(TickEvent.ClientTickEvent event) {
            if (event.phase != TickEvent.Phase.START) return;
            handleActiveKeys();
        }

        @SubscribeEvent
        public static void preventNameTagRender(RenderNameTagEvent event) {
            if (!(event.getEntity() instanceof Player otherPlayer)) return;

            Player localPlayer = Minecraft.getInstance().player;
            if(Services.POWER.getPowers(otherPlayer, ApugliPowers.PREVENT_LABEL_RENDER.get()).stream().anyMatch(power -> power.shouldHide(localPlayer))) {
                event.setResult(Event.Result.DENY);
            }
        }

        @SubscribeEvent
        public static void onLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
            CachedBlockInRadiusCondition.clearCache();
            if (event.getConnection() == null) return;
            TextureUtilClient.clear();
        }

    }

    @Mod.EventBusSubscriber(modid = Apugli.ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ModEvents {


        @SubscribeEvent
        public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
            event.registerEntityRenderer(ApugliEntityTypes.CUSTOM_AREA_EFFECT_CLOUD.get(), NoopRenderer::new);
            event.registerEntityRenderer(ApugliEntityTypes.CUSTOM_PROJECTILE.get(), CustomProjectileRenderer::new);
        }

        @SubscribeEvent
        public static void registerRenderLayers(EntityRenderersEvent.AddLayers event) {
            ItemInHandRenderer itemInHandRenderer = Minecraft.getInstance().getEntityRenderDispatcher().getItemInHandRenderer();
            ((EntityRenderersEventAddLayersAccessor)event).getRenderers().forEach((entityType, entityRenderer) -> {
                if (!(entityRenderer instanceof LivingEntityRenderer<?, ?> livingEntityRenderer)) return;
                livingEntityRenderer.addLayer(new EntityTextureOverlayLayer(livingEntityRenderer, false, event.getEntityModels()));

                livingEntityRenderer.addLayer(new EnergySwirlLayer(livingEntityRenderer));

                if (livingEntityRenderer.getModel() instanceof ArmedModel) {
                    livingEntityRenderer.addLayer(new PowerItemInHandLayer(livingEntityRenderer, itemInHandRenderer));
                }

                if (livingEntityRenderer.getModel() instanceof HumanoidModel<?> humanoidModel) {
                    livingEntityRenderer.addLayer(new PowerHumanoidArmorLayer(livingEntityRenderer, humanoidModel, humanoidModel));
                }

                if (entityRenderer instanceof HumanoidMobRenderer<?, ?> humanoidMobRenderer) {
                    humanoidMobRenderer.addLayer(new PowerCustomHeadLayer(humanoidMobRenderer, event.getEntityModels(), ((HumanoidMobRendererAccess)humanoidMobRenderer).getHeadSize().x(), ((HumanoidMobRendererAccess)humanoidMobRenderer).getHeadSize().y(), ((HumanoidMobRendererAccess)humanoidMobRenderer).getHeadSize().z(), itemInHandRenderer));
                    ((HumanoidMobRendererAccess)humanoidMobRenderer).setHeadSize(null);
                } else if (livingEntityRenderer.getModel() instanceof HeadedModel) {
                    livingEntityRenderer.addLayer(new PowerCustomHeadLayer(livingEntityRenderer, event.getEntityModels(), itemInHandRenderer));
                }
            });

            event.getSkins().forEach(s -> {
                PlayerRenderer playerRenderer = event.getSkin(s);
                playerRenderer.addLayer(new EnergySwirlLayer<>(playerRenderer));
                playerRenderer.addLayer(new EntityTextureOverlayLayer<>(playerRenderer, ((PlayerModelAccessor)playerRenderer.getModel()).isSlim(), event.getEntityModels()));
                playerRenderer.addLayer(new PowerItemInHandLayer<>(playerRenderer, itemInHandRenderer));
                playerRenderer.addLayer(new PowerCustomHeadLayer<>(playerRenderer, event.getEntityModels(), itemInHandRenderer));
                playerRenderer.addLayer(new PowerHumanoidArmorLayer<>(playerRenderer, playerRenderer.getModel(), playerRenderer.getModel()));
            });
        }
    }

}
