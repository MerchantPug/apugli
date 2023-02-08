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

package net.merchantpug.apugli;

import net.merchantpug.apugli.networking.ApugliPackets;
import net.merchantpug.apugli.registry.ApugliPowerFactories;
import net.merchantpug.apugli.registry.action.ApugliBiEntityActions;
import net.merchantpug.apugli.registry.action.ApugliBlockActions;
import net.merchantpug.apugli.registry.action.ApugliItemActions;
import net.merchantpug.apugli.registry.condition.ApugliBiEntityConditions;
import net.merchantpug.apugli.registry.condition.ApugliBlockConditions;
import net.merchantpug.apugli.registry.condition.ApugliDamageConditions;
import net.merchantpug.apugli.registry.condition.ApugliEntityConditions;
import net.merchantpug.apugli.util.ApugliConfig;
import eu.midnightdust.lib.config.MidnightConfig;
import io.github.apace100.apoli.util.NamespaceAlias;
import net.merchantpug.apugli.registry.action.ApugliEntityActions;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Apugli implements ModInitializer {
	public static final String MODID = "apugli";
	public static final Logger LOGGER = LogManager.getLogger(Apugli.class);
	public static String VERSION = "";
	public static int[] SEMVER;

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

		ApugliPackets.registerC2S();

		NamespaceAlias.addAlias("ope", MODID);

		MidnightConfig.init(Apugli.MODID, ApugliConfig.class);
	}

	public static Identifier identifier(String path) {
		return new Identifier(MODID, path);
	}
}
