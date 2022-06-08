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
import io.github.apace100.apoli.power.Active;
import io.github.apace100.apoli.util.NamespaceAlias;
import com.github.merchantpug.apugli.registry.action.ApugliEntityActions;
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
	public static HashMap<PlayerEntity, HashSet<Active.Key>> currentlyUsedKeys = new HashMap<>();

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
		ApugliEntityConditions.register();
		ApugliDamageConditions.register();

		ApugliPowerFactories.register();

		ApugliPacketsC2S.register();

		NamespaceAlias.addAlias("ope", MODID);

		ServerPlayConnectionEvents.DISCONNECT.register(((handler, server) -> currentlyUsedKeys.clear()));
	}

	public static Identifier identifier(String path) {
		return new Identifier(MODID, path);
	}
}
