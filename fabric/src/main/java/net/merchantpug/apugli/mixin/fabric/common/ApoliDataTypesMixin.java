package net.merchantpug.apugli.mixin.fabric.common;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.apoli.power.factory.action.EntityActions;
import io.github.apace100.apoli.registry.ApoliRegistryKeys;
import io.github.apace100.calio.util.IdentifierAlias;
import net.merchantpug.apugli.Apugli;
import net.merchantpug.apugli.action.factory.entity.CustomProjectileAction;
import net.merchantpug.apugli.util.TextureUtil;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.function.BiFunction;

@Mixin(value = ApoliDataTypes.class, remap = false)
public class ApoliDataTypesMixin {

    @Inject(method = "lambda$action$38", at = @At(value = "RETURN"), locals = LocalCapture.CAPTURE_FAILHARD, remap = false)
    private static void apugli$handleActions(Registry registry, IdentifierAlias aliases, BiFunction errorHandler, JsonElement jsonElement, CallbackInfoReturnable<ActionFactory.Instance> cir, JsonObject jsonObject, ResourceLocation factoryId) {
        if (registry.key().equals(ApoliRegistryKeys.ENTITY_ACTION) && (factoryId.equals(Apugli.asResource("custom_projectile")) || EntityActions.ALIASES.hasAlias(factoryId) && EntityActions.ALIASES.resolveAlias(factoryId, registry::containsKey).equals(Apugli.asResource("custom_projectile")))) {
            if (jsonObject.has("texture_url")) {
                String url = GsonHelper.getAsString(jsonObject, "texture_url");
                ResourceLocation textureLocation = null;
                if (jsonObject.has("texture_location")) {
                    textureLocation = ResourceLocation.of(GsonHelper.getAsString(jsonObject, "texture_location"), ':');
                }
                TextureUtil.cacheOneOff(CustomProjectileAction.getTextureUrl(url), url, textureLocation);
            }
        }
    }

}
