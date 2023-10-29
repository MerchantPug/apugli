package net.merchantpug.apugli;

import eu.midnightdust.lib.config.MidnightConfig;
import io.github.apace100.apoli.Apoli;
import io.github.apace100.apoli.integration.PostPowerLoadCallback;
import io.github.apace100.apoli.integration.PrePowerReloadCallback;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.util.NamespaceAlias;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.merchantpug.apugli.mixin.fabric.common.accessor.PowerTypeRegistryAccessor;
import net.merchantpug.apugli.network.ApugliPackets;
import net.merchantpug.apugli.network.s2c.UpdateUrlTexturesPacket;
import net.merchantpug.apugli.network.s2c.integration.pehkui.ClearScaleModifierCachePacket;
import net.merchantpug.apugli.power.CustomProjectilePower;
import net.merchantpug.apugli.power.TextureOrUrlPower;
import net.merchantpug.apugli.registry.power.ApugliPowers;
import net.merchantpug.apugli.util.ApugliConfig;
import net.merchantpug.apugli.util.TextureUtil;

public class ApugliFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        FabricLoader.getInstance().getModContainer(Apugli.ID).ifPresent(modContainer -> {
            String version = modContainer.getMetadata().getVersion().getFriendlyString();
            if (version.contains("+")) {
                version = version.split("\\+")[0];
            }
            if (version.contains("-")) {
                version = version.split("-")[0];
            }
            Apugli.VERSION = version;
        });

        Apugli.init();
        ApugliPackets.registerC2S();

        ServerLifecycleEvents.START_DATA_PACK_RELOAD.register((server, serverResourceManager) -> {
            if (FabricLoader.getInstance().isModLoaded("pehkui")) {
                ApugliPowers.MODIFY_SCALE.get().clearFromAll();
                ApugliPowers.MODIFY_SCALE.get().clearScaleTypeCache();
                ApugliPowers.MODIFY_SCALE.get().clearModifiersFromCache();
                server.getPlayerList().getPlayers().forEach(serverPlayer -> ApugliPackets.sendS2C(new ClearScaleModifierCachePacket(), serverPlayer));
            }
            TextureUtil.getCache().clear();
        });

        PostPowerLoadCallback.EVENT.register((powerId, factoryId, isSubPower, json, powerType) -> {
            if (!FabricLoader.getInstance().isModLoaded("pehkui") && factoryId.equals(Apugli.asResource("modify_scale"))) {
                Apugli.LOG.error("Power '" + powerId + "' could not be loaded as it requires the Pehkui mod to be present. (skipping).");
                PowerTypeRegistryAccessor.invokeRemove(powerId);
                return;
            }

            Power power = powerType.create(null);
            if ((power instanceof TextureOrUrlPower texturePower) && texturePower.getTextureUrl() != null) {
                TextureUtil.cachePower(powerId, texturePower);
            } else if (power instanceof CustomProjectilePower.Instance projectilePower && ApugliPowers.CUSTOM_PROJECTILE.get().getDataFromPower(projectilePower).isPresent("texture_url")) {
                ApugliPowers.CUSTOM_PROJECTILE.get().cacheTextureUrl(powerId, projectilePower);
            }
        });

        ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register((player, joined) -> ApugliPackets.sendS2C(new UpdateUrlTexturesPacket(TextureUtil.getCache()), player));

        NamespaceAlias.addAlias("ope", Apugli.ID);

        MidnightConfig.init(Apugli.ID, ApugliConfig.class);
    }
}
