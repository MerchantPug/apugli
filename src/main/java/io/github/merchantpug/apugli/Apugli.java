package io.github.merchantpug.apugli;

import io.github.merchantpug.apugli.networking.packet.LightUpBlockPacket;
import io.github.merchantpug.apugli.networking.packet.RocketJumpPacket;
import io.github.merchantpug.apugli.registry.ApugliEntityActions;
import io.github.merchantpug.apugli.registry.ApugliEntityConditions;
import io.github.merchantpug.apugli.registry.ApugliPowers;
import io.github.merchantpug.apugli.util.ApugliNamespaceAlias;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Apugli implements ModInitializer {
	public static final String MODID = "apugli";
	public static final Logger LOGGER = LogManager.getLogger(Apugli.class);

	@Override
	public void onInitialize() {
		ApugliEntityActions.register();
		ApugliEntityConditions.register();
		ApugliPowers.init();

		ApugliNamespaceAlias.addAlias("toomanyorigins");
		ApugliNamespaceAlias.addAlias("cursedorigins");

		ServerPlayNetworking.registerGlobalReceiver(LightUpBlockPacket.ID, LightUpBlockPacket::handle);
		ServerPlayNetworking.registerGlobalReceiver(RocketJumpPacket.ID, RocketJumpPacket::handle);
	}

	public static Identifier identifier(String path) {
		return new Identifier(MODID, path);
	}
}
