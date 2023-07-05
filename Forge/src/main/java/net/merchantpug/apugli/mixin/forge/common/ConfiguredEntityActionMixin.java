package net.merchantpug.apugli.mixin.forge.common;

import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredEntityAction;
import io.github.edwinmindcraft.apoli.api.registry.ApoliRegistries;
import net.merchantpug.apugli.Apugli;
import net.merchantpug.apugli.action.configuration.FabricActionConfiguration;
import net.merchantpug.apugli.action.factory.entity.CustomProjectileAction;
import net.merchantpug.apugli.util.TextureUtil;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;

@Mixin(ConfiguredEntityAction.class)
public class ConfiguredEntityActionMixin {
    @Inject(method = "<init>", at = @At("TAIL"))
    private void loadCustomProjectileActionUrls(Supplier factory, IDynamicFeatureConfiguration configuration, CallbackInfo ci) {
        if (factory.get() == ApoliRegistries.ENTITY_ACTION.get().getHolder(Apugli.asResource("custom_projectile")) && configuration instanceof FabricActionConfiguration<?> fabricConfig) {
            if (fabricConfig.data().isPresent("texture_url")) {
                String url = fabricConfig.data().getString("texture_url");
                ResourceLocation textureLocation = null;
                if (fabricConfig.data().isPresent("texture_location")) {
                    textureLocation = ResourceLocation.of(fabricConfig.data().getString("texture_location"), ':');
                }
                TextureUtil.cacheOneOff(CustomProjectileAction.getTextureUrl(url), url, textureLocation);
            }
        }
    }
}
