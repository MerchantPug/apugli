package net.merchantpug.apugli.mixin.xplatform.client;

import io.github.apace100.apoli.power.Power;
import net.merchantpug.apugli.Apugli;
import net.merchantpug.apugli.platform.Services;
import net.merchantpug.apugli.power.TextureOrUrlPower;
import net.merchantpug.apugli.util.TextureUtil;
import net.merchantpug.apugli.client.util.TextureUtilClient;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Mixin(Minecraft.class)
public class MinecraftClientMixin {
    @Inject(method = "reloadResourcePacks(Z)Ljava/util/concurrent/CompletableFuture;", at = @At("RETURN"))
    private void reloadUrlTextures(boolean force, CallbackInfoReturnable<CompletableFuture<Void>> cir) {
        for (Map.Entry<ResourceLocation, String> entry : TextureUtil.getPowerIdToUrl().entrySet()) {
            ResourceLocation id = entry.getKey();
            String url = entry.getValue();
            Power power = Services.POWER.createPowerFromId(id);
            if (!(power instanceof TextureOrUrlPower textureOrUrl)) {
                Apugli.LOG.warn("Tried reloading URL textures from power '{}' but couldn't as its power type does not implement TextureOrUrl.", id);
            } else if (textureOrUrl.getTextureLocation() == null || !TextureUtilClient.doesTextureExist(textureOrUrl.getTextureLocation())) {
                TextureUtilClient.registerPowerTexture(null, textureOrUrl.getUrlTextureIdentifier(), url, true);
            } else {
                TextureUtil.getRegisteredTextures().remove(textureOrUrl.getUrlTextureIdentifier());
            }
        }
    }
}
