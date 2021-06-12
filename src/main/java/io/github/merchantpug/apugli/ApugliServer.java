package io.github.merchantpug.apugli;

import io.github.merchantpug.apugli.registry.ApugliEntityConditionsClient;
import io.github.merchantpug.apugli.registry.ApugliEntityConditionsServer;
import net.fabricmc.api.ClientModInitializer;

public class ApugliServer implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ApugliEntityConditionsServer.register();
	}
}
