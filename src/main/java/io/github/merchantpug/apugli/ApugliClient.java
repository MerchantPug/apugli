package io.github.merchantpug.apugli;

import io.github.merchantpug.apugli.networking.ApugliPacketsS2C;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ApugliClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ApugliPacketsS2C.register();
	}
}
