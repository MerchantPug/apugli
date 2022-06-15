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

package com.github.merchantpug.apugli;

import com.github.merchantpug.apugli.networking.ApugliPacketsC2S;
import com.github.merchantpug.apugli.registry.ApugliPowerFactories;
import com.github.merchantpug.apugli.registry.action.ApugliBiEntityActions;
import com.github.merchantpug.apugli.registry.action.ApugliBlockActions;
import com.github.merchantpug.apugli.registry.action.ApugliItemActions;
import com.github.merchantpug.apugli.registry.condition.ApugliBiEntityConditions;
import com.github.merchantpug.apugli.registry.condition.ApugliBlockConditions;
import com.github.merchantpug.apugli.registry.condition.ApugliDamageConditions;
import com.github.merchantpug.apugli.registry.condition.ApugliEntityConditions;
import com.github.merchantpug.apugli.util.ApugliConfig;
import com.github.merchantpug.apugli.util.ApugliServerConfig;
import io.github.apace100.apoli.power.Active;
import io.github.apace100.apoli.util.NamespaceAlias;
import com.github.merchantpug.apugli.registry.action.ApugliEntityActions;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.HashSet;

public class Apugli implements ModInitializer {
	public static final String MODID = "apugli";
	public static final Logger LOGGER = LogManager.getLogger(Apugli.class);
	public static String VERSION = "";
	public static int[] SEMVER;

	public static HashMap<PlayerEntity, HashSet<Active.Key>> currentlyUsedKeys = new HashMap<>();

	public static ApugliConfig config;
	public static ApugliServerConfig serverConfig;
	private static final HashMap<String, String> DEPENDENTS = new HashMap<>();

	@Override
	public void onInitialize() {
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
		ApugliBiEntityActions.register();
		ApugliBlockActions.register();
		ApugliEntityActions.register();
		ApugliItemActions.register();

		ApugliBiEntityConditions.register();
		ApugliBlockConditions.register();
		ApugliEntityConditions.register();
		ApugliDamageConditions.register();

		ApugliPowerFactories.register();

		AutoConfig.register(ApugliConfig.class, JanksonConfigSerializer::new);
		config = AutoConfig.getConfigHolder(ApugliConfig.class).getConfig();

		AutoConfig.register(ApugliServerConfig.class, JanksonConfigSerializer::new);
		serverConfig = AutoConfig.getConfigHolder(ApugliServerConfig.class).getConfig();

		ApugliPacketsC2S.register();

		NamespaceAlias.addAlias("ope", MODID);

		ServerPlayConnectionEvents.DISCONNECT.register(((handler, server) -> currentlyUsedKeys.clear()));
	}

	public static Identifier identifier(String path) {
		return new Identifier(MODID, path);
	}

	public static HashMap<String, String> getDependents() {
		return DEPENDENTS;
	}

	public static void addDependent(String value, String apugliVersion) {
		DEPENDENTS.put(value, apugliVersion);
	}

	public static boolean shouldCheckVersion() {
		boolean value = serverConfig.performVersionCheck;
		for (String dependentModId : DEPENDENTS.keySet()) {
			if (FabricLoader.getInstance().isModLoaded(dependentModId) && DEPENDENTS.get(dependentModId).matches(VERSION)) {
				Apugli.LOGGER.info("Apugli will not check client versions.");
				value = false;
				break;
			}
		}
		return value;
	}
}
