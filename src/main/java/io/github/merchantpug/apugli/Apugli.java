package io.github.merchantpug.apugli;

import io.github.apace100.apoli.util.NamespaceAlias;
import io.github.merchantpug.apugli.registry.*;
import io.github.merchantpug.apugli.registry.action.ApugliBiEntityActions;
import io.github.merchantpug.apugli.registry.action.ApugliBlockActions;
import io.github.merchantpug.apugli.registry.action.ApugliEntityActions;
import io.github.merchantpug.apugli.registry.action.ApugliItemActions;
import io.github.merchantpug.apugli.registry.condition.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Apugli implements ModInitializer {
	public static final String MODID = "apugli";
	public static final Logger LOGGER = LogManager.getLogger(Apugli.class);
	public static String VERSION = "";

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
		});
		LOGGER.info("Apugli " + VERSION + " has initialized. Powering up your powered up game.");
		ApugliBiEntityActions.register();
		ApugliBlockActions.register();
		ApugliEntityActions.register();
		ApugliItemActions.register();

		ApugliBiEntityConditions.register();
		ApugliBlockConditions.register();
		ApugliDamageConditions.register();

		ApugliEntityConditions.register();
		ApugliPowerFactories.register();

		MobBehaviors.register();

		NamespaceAlias.addAlias("ope", MODID);
	}


	public static Identifier identifier(String path) {
		return new Identifier(MODID, path);
	}
}
