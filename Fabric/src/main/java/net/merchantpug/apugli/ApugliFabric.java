package net.merchantpug.apugli;

import eu.midnightdust.lib.config.MidnightConfig;
import io.github.apace100.apoli.integration.PostPowerLoadCallback;
import io.github.apace100.apoli.integration.PostPowerReloadCallback;
import io.github.apace100.apoli.util.NamespaceAlias;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.merchantpug.apugli.networking.ApugliPackets;
import net.merchantpug.apugli.networking.s2c.UpdateUrlTexturesPacket;
import net.merchantpug.apugli.power.TextureOrUrlPower;
import net.merchantpug.apugli.util.ApugliConfig;
import net.merchantpug.apugli.util.TextureUtil;
import net.minecraft.server.level.ServerPlayer;

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
            TextureUtil.handleUrlTexture(powerId, texturePower);
        });

        PostPowerReloadCallback.EVENT.register(() -> {
            if (Apugli.getServer() == null) return;
            for (ServerPlayer player : Apugli.getServer().getPlayerList().getPlayers()) {
                ApugliPackets.sendS2C(new UpdateUrlTexturesPacket(TextureUtil.getTexturePowers()), player);
            }
        });

        NamespaceAlias.addAlias("ope", Apugli.ID);

        MidnightConfig.init(Apugli.ID, ApugliConfig.class);
    }
}
