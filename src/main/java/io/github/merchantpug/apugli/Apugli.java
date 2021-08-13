package io.github.merchantpug.apugli;

import io.github.merchantpug.apugli.registry.*;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Apugli implements ModInitializer {
	public static final String MODID = "apugli";
	public static final Logger LOGGER = LogManager.getLogger(Apugli.class);

	@Override
	public void onInitialize() {
		LOGGER.info("Apugli has initialized. Powering up your powered up game.");
		ApugliBlockActions.register();
		ApugliEntityActions.register();
		ApugliDamageConditions.register();
		ApugliEntityConditions.register();
		ApugliPowers.init();
	}

	public static Identifier identifier(String path) {
		return new Identifier(MODID, path);
	}
}
