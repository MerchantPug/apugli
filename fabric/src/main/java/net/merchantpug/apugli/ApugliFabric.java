package net.merchantpug.apugli;

import eu.midnightdust.lib.config.MidnightConfig;
import io.github.apace100.apoli.Apoli;
import io.github.apace100.apoli.integration.PostPowerLoadCallback;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.util.IdentifierAlias;
import io.github.apace100.apoli.util.NamespaceAlias;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.EntityTrackingEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.merchantpug.apugli.integration.pehkui.PehkuiUtil;
import net.merchantpug.apugli.mixin.fabric.common.accessor.PowerTypeRegistryAccessor;
import net.merchantpug.apugli.network.ApugliPackets;
import net.merchantpug.apugli.network.s2c.UpdateUrlTexturesPacket;
import net.merchantpug.apugli.power.CustomProjectilePower;
import net.merchantpug.apugli.power.TextureOrUrlPower;
import net.merchantpug.apugli.registry.power.ApugliPowers;
import net.merchantpug.apugli.util.ApugliConfig;
import net.merchantpug.apugli.util.TextureUtil;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

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
        registerEvents();

        IdentifierAlias.addNamespaceAlias("ope", Apugli.ID);
        IdentifierAlias.addAlias(Apugli.asResource("edible_item"), Apoli.identifier("edible_item"));
        IdentifierAlias.addAlias(Apugli.asResource("modify_enchantment_level"), Apoli.identifier("modify_enchantment_level"));

        MidnightConfig.init(Apugli.ID, ApugliConfig.class);
    }

    public static void registerEvents() {
        ServerLifecycleEvents.START_DATA_PACK_RELOAD.register((server, serverResourceManager) -> TextureUtil.getCache().clear());
        ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register((player, joined) -> ApugliPackets.sendS2C(new UpdateUrlTexturesPacket(TextureUtil.getCache()), player));

        PostPowerLoadCallback.EVENT.register((powerId, factoryId, isSubPower, json, powerType) -> {
            if (!FabricLoader.getInstance().isModLoaded("pehkui") && factoryId.equals(Apugli.asResource("modify_scale"))) {
                Apugli.LOG.warn("Power '" + powerId + "' could not be loaded as it uses the `" + factoryId + "' power type, which requires the Pehkui mod to be present. (skipping).");
                PowerTypeRegistryAccessor.apugli$invokeRemove(powerId);
                return;
            }

            Power power = powerType.create(null);
            if ((power instanceof TextureOrUrlPower texturePower) && texturePower.getTextureUrl() != null) {
                TextureUtil.cachePower(powerId, texturePower);
            } else if (power instanceof CustomProjectilePower.Instance projectilePower && ApugliPowers.CUSTOM_PROJECTILE.get().getDataFromPower(projectilePower).isPresent("texture_url")) {
                ApugliPowers.CUSTOM_PROJECTILE.get().cacheTextureUrl(powerId, projectilePower);
            }
        });
    }
}
