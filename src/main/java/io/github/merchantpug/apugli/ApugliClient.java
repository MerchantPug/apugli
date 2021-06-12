package io.github.merchantpug.apugli;

import io.github.merchantpug.apugli.registry.ApugliEntityConditionsClient;
import net.fabricmc.api.ClientModInitializer;

public class ApugliClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ApugliEntityConditionsClient.register();
	}
}
