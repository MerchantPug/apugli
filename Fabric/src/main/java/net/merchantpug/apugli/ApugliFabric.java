package net.merchantpug.apugli;

import eu.midnightdust.lib.config.MidnightConfig;
import io.github.apace100.apoli.integration.PostPowerLoadCallback;
import io.github.apace100.apoli.power.PowerTypeRegistry;
import io.github.apace100.apoli.util.NamespaceAlias;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.merchantpug.apugli.network.ApugliPackets;
import net.merchantpug.apugli.network.s2c.UpdateUrlTexturesPacket;
import net.merchantpug.apugli.power.TextureOrUrlPower;
import net.merchantpug.apugli.util.ApugliConfig;
import net.merchantpug.apugli.util.TextureUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;

import java.util.HashMap;
import java.util.Map;

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

        PostPowerLoadCallback.EVENT.register((powerId, factoryId, isSubPower, json, powerType) -> {
            if (!(powerType.create(null) instanceof TextureOrUrlPower texturePower) || texturePower.getTextureUrl() == null) return;
            TextureUtil.getCache().clear();
            TextureUtil.cachePower(powerId, texturePower);
        });

        ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register((player, joined) -> ApugliPackets.sendS2C(new UpdateUrlTexturesPacket(TextureUtil.getCache()), player));

        NamespaceAlias.addAlias("ope", Apugli.ID);

        MidnightConfig.init(Apugli.ID, ApugliConfig.class);
    }
}
