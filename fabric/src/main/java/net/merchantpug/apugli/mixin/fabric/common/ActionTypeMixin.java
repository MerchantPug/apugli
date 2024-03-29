package net.merchantpug.apugli.mixin.fabric.common;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.apoli.power.factory.action.ActionType;
import io.github.apace100.apoli.util.IdentifierAlias;
import net.merchantpug.apugli.Apugli;
import net.merchantpug.apugli.action.factory.entity.CustomProjectileAction;
import net.merchantpug.apugli.util.TextureUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Optional;

@Mixin(value = ActionType.class, remap = false)
public class ActionTypeMixin<T> {

    @Shadow @Final private String actionTypeName;

    @Inject(method = "read(Lcom/google/gson/JsonElement;)Lio/github/apace100/apoli/power/factory/action/ActionFactory$Instance;", at = @At(value = "RETURN"), locals = LocalCapture.CAPTURE_FAILHARD, remap = false)
    private void apugli$handleActions(JsonElement jsonElement, CallbackInfoReturnable<ActionFactory<T>.Instance> cir, JsonObject jsonObject, ResourceLocation type, Optional actionFactory) {
        if (this.actionTypeName.equals("EntityAction") && (type.equals(Apugli.asResource("custom_projectile")) || IdentifierAlias.hasAlias(type) && IdentifierAlias.resolveAlias(type).equals(Apugli.asResource("custom_projectile")))) {
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
