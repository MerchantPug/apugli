package net.merchantpug.apugli.mixin.client;

import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerTypeRegistry;
import net.merchantpug.apugli.Apugli;
import net.merchantpug.apugli.power.TextureOrUrl;
import net.merchantpug.apugli.util.TextureUtil;
import net.merchantpug.apugli.util.TextureUtilClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    @Inject(method = "reloadResources(Z)Ljava/util/concurrent/CompletableFuture;", at = @At("RETURN"))
    private void reloadUrlTextures(boolean force, CallbackInfoReturnable<CompletableFuture<Void>> cir) {
        for (Map.Entry<Identifier, String> entry : TextureUtil.getPowerIdToUrl().entrySet()) {
            Identifier id = entry.getKey();
            String url = entry.getValue();
            Power power = PowerTypeRegistry.get(id).create(null);
            if (!(power instanceof TextureOrUrl textureOrUrl)) {
                Apugli.LOGGER.warn("Tried reloading URL textures from power '{}' but couldn't as its power type does not implement TextureOrUrl.", id);
            } else if (textureOrUrl.getTextureLocation() == null || !TextureUtilClient.doesTextureExist(textureOrUrl.getTextureLocation())) {
                TextureUtilClient.registerPowerTexture(textureOrUrl.getUrlTextureIdentifier(), url);
            } else {
                TextureUtil.getRegisteredTextures().remove(textureOrUrl.getUrlTextureIdentifier());
            }
        }
    }
}
