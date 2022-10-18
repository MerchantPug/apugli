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
import eu.midnightdust.lib.config.MidnightConfig;
import io.github.apace100.apoli.power.Active;
import io.github.apace100.apoli.util.NamespaceAlias;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import the.great.migration.merchantpug.apugli.networking.ApugliPacketsC2S;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

public class Apugli implements ModInitializer {
	public static final String MODID = "apugli";
	public static final Logger LOGGER = LogManager.getLogger(Apugli.class);
	public static String VERSION = "";
	public static int[] SEMVER;

	public static HashMap<UUID, HashSet<Active.Key>> keysToCheck = new HashMap<>();
	public static HashMap<UUID, HashSet<Active.Key>> currentlyUsedKeys = new HashMap<>();

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
		ApugliBiEntityActions.registerAll();
		ApugliBlockActions.registerAll();
		ApugliEntityActions.registerAll();
		ApugliItemActions.registerAll();
		
		ApugliBiEntityConditions.registerAll();
		ApugliBlockConditions.registerAll();
		ApugliEntityConditions.registerAll();
		ApugliDamageConditions.registerAll();
		
		ApugliPowers.registerAll();

		ApugliPacketsC2S.register();

		NamespaceAlias.addAlias("ope", MODID);

		ServerPlayConnectionEvents.DISCONNECT.register(((handler, server) -> {
			keysToCheck.remove(handler.player.getUUID());
			currentlyUsedKeys.remove(handler.player.getUUID());
		}));

		MidnightConfig.init(Apugli.MODID, ApugliConfig.class);
		MidnightConfig.init(Apugli.MODID + "_server", ApugliServerConfig.class);
	}

	public static ResourceLocation identifier(String path) {
		return new ResourceLocation(MODID, path);
	}
	
}
