package net.merchantpug.apugli.mixin.xplatform.client;

import net.merchantpug.apugli.client.util.TextureUtilClient;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
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
        for (Map.Entry<ResourceLocation, Tuple<ResourceLocation, String>> entry : TextureUtilClient.getPowerIdToUrl().entrySet()) {
            ResourceLocation powerId = entry.getKey();
            ResourceLocation textureLocation = entry.getValue().getA();
            String url = entry.getValue().getB();
            if (!TextureUtilClient.doesTextureExist(textureLocation)) {
                TextureUtilClient.registerPowerTexture(powerId, textureLocation, url, true);
            } else {
                TextureUtilClient.getRegisteredTextures().remove(textureLocation);
            }
        }
    }
}
