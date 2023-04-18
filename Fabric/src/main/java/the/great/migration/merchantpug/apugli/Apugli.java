/*
MIT License

Copyright (c) 2021 apace100

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */

<<<<<<<< HEAD:src/main/java/net/merchantpug/apugli/Apugli.java
package net.merchantpug.apugli;

import io.github.apace100.apoli.integration.PostPowerLoadCallback;
import io.github.apace100.apoli.integration.PostPowerReloadCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.merchantpug.apugli.networking.ApugliPackets;
import net.merchantpug.apugli.networking.s2c.UpdateUrlTexturesPacket;
import net.merchantpug.apugli.power.TextureOrUrl;
import net.merchantpug.apugli.registry.ApugliPowerFactories;
import net.merchantpug.apugli.registry.action.ApugliBiEntityActions;
import net.merchantpug.apugli.registry.action.ApugliBlockActions;
import net.merchantpug.apugli.registry.action.ApugliItemActions;
import net.merchantpug.apugli.registry.condition.ApugliBiEntityConditions;
import net.merchantpug.apugli.registry.condition.ApugliBlockConditions;
import net.merchantpug.apugli.registry.condition.ApugliDamageConditions;
import net.merchantpug.apugli.registry.condition.ApugliEntityConditions;
import net.merchantpug.apugli.util.ApugliConfig;
========
package the.great.migration.merchantpug.apugli;

import com.github.merchantpug.apugli.registry.action.ApugliBiEntityActions;
import com.github.merchantpug.apugli.registry.action.ApugliBlockActions;
import com.github.merchantpug.apugli.registry.action.ApugliEntityActions;
import com.github.merchantpug.apugli.registry.action.ApugliItemActions;
import com.github.merchantpug.apugli.registry.condition.ApugliBiEntityConditions;
import com.github.merchantpug.apugli.registry.condition.ApugliBlockConditions;
import com.github.merchantpug.apugli.registry.condition.ApugliDamageConditions;
import com.github.merchantpug.apugli.registry.condition.ApugliEntityConditions;
import com.github.merchantpug.apugli.registry.power.ApugliPowers;
import com.github.merchantpug.apugli.util.ApugliConfig;
import com.github.merchantpug.apugli.util.ApugliServerConfig;
>>>>>>>> pr/25:Fabric/src/main/java/the/great/migration/merchantpug/apugli/Apugli.java
import eu.midnightdust.lib.config.MidnightConfig;
import io.github.apace100.apoli.util.NamespaceAlias;
<<<<<<<< HEAD:src/main/java/net/merchantpug/apugli/Apugli.java
import net.merchantpug.apugli.registry.action.ApugliEntityActions;
========
>>>>>>>> pr/25:Fabric/src/main/java/the/great/migration/merchantpug/apugli/Apugli.java
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
<<<<<<<< HEAD:src/main/java/net/merchantpug/apugli/Apugli.java
import net.merchantpug.apugli.util.TextureUtil;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
========
import net.minecraft.resources.ResourceLocation;
>>>>>>>> pr/25:Fabric/src/main/java/the/great/migration/merchantpug/apugli/Apugli.java
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import the.great.migration.merchantpug.apugli.networking.ApugliPacketsC2S;

public class Apugli implements ModInitializer {
	public static final String MODID = "apugli";
	public static final Logger LOGGER = LogManager.getLogger(Apugli.class);
	public static String VERSION = "";
	public static int[] SEMVER;

	private static MinecraftServer server;

	@Override
	public void onInitialize() {
		ServerLifecycleEvents.SERVER_STARTED.register(s -> server = s);

		FabricLoader.getInstance().getModContainer(MODID).ifPresent(modContainer -> {
			VERSION = modContainer.getMetadata().getVersion().getFriendlyString();
			if(VERSION.contains("+")) {
				VERSION = VERSION.split("\\+")[0];
			}
			if(VERSION.contains("-")) {
				VERSION = VERSION.split("-")[0];
			}
			String[] splitVersion = VERSION.split("\\.");
			SEMVER = new int[splitVersion.length];
			for(int i = 0; i < SEMVER.length; i++) {
				SEMVER[i] = Integer.parseInt(splitVersion[i]);
			}
		});
		LOGGER.info("Apugli " + VERSION + " has initialized. Powering up your powered up game.");
<<<<<<<< HEAD:src/main/java/net/merchantpug/apugli/Apugli.java
		ApugliBiEntityActions.register();
		ApugliBlockActions.register();
		ApugliEntityActions.register();
		ApugliItemActions.register();

		ApugliBiEntityConditions.register();
		ApugliBlockConditions.register();
		ApugliDamageConditions.register();
		ApugliEntityConditions.register();

		ApugliPowerFactories.register();
========
		ApugliBiEntityActions.registerAll();
		ApugliBlockActions.registerAll();
		ApugliEntityActions.registerAll();
		ApugliItemActions.registerAll();
		
		ApugliBiEntityConditions.registerAll();
		ApugliBlockConditions.registerAll();
		ApugliEntityConditions.registerAll();
		ApugliDamageConditions.registerAll();
		
		ApugliPowers.registerAll();
>>>>>>>> pr/25:Fabric/src/main/java/the/great/migration/merchantpug/apugli/Apugli.java

		ApugliPackets.registerC2S();

		PostPowerLoadCallback.EVENT.register((powerId, factoryId, isSubPower, json, powerType) -> {
			if (!(powerType.create(null) instanceof TextureOrUrl texturePower) || texturePower.getTextureUrl() == null) return;
			TextureUtil.handleUrlTexture(powerId, texturePower);
		});

		PostPowerReloadCallback.EVENT.register(() -> {
			if (server == null) return;
			for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
				ApugliPackets.sendS2C(new UpdateUrlTexturesPacket(TextureUtil.getTexturePowers()), player);
			}
		});

		NamespaceAlias.addAlias("ope", MODID);

<<<<<<<< HEAD:src/main/java/net/merchantpug/apugli/Apugli.java
========
		ServerPlayConnectionEvents.DISCONNECT.register(((handler, server) -> {
			keysToCheck.remove(handler.player.getUUID());
			currentlyUsedKeys.remove(handler.player.getUUID());
		}));

>>>>>>>> pr/25:Fabric/src/main/java/the/great/migration/merchantpug/apugli/Apugli.java
		MidnightConfig.init(Apugli.MODID, ApugliConfig.class);
	}

	public static ResourceLocation identifier(String path) {
		return new ResourceLocation(MODID, path);
	}
	
}
