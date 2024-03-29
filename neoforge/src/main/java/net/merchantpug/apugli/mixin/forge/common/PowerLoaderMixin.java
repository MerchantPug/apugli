package net.merchantpug.apugli.mixin.forge.common;

import com.google.gson.JsonElement;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import io.github.edwinmindcraft.apoli.common.data.PowerLoader;
import net.merchantpug.apugli.Apugli;
import net.merchantpug.apugli.power.integration.pehkui.ModifyScalePower;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.ModList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;
import java.util.stream.Stream;

@Mixin(PowerLoader.class)
public class PowerLoaderMixin {
    @Inject(method = "lambda$accept$3(Lnet/minecraft/resources/ResourceLocation;Lcom/google/gson/JsonElement;)Ljava/util/stream/Stream;", at = @At("HEAD"), remap = false)
    private static void apugli$capturePowerId(ResourceLocation resourceLocation, JsonElement x, CallbackInfoReturnable<Stream> cir, @Share("powerId") LocalRef<ResourceLocation> ref) {
        ref.set(resourceLocation);
    }

    @ModifyExpressionValue(method = "lambda$accept$3(Lnet/minecraft/resources/ResourceLocation;Lcom/google/gson/JsonElement;)Ljava/util/stream/Stream;", at = @At(value = "INVOKE", target = "Ljava/util/Optional;isPresent()Z"), remap = false)
    private static boolean apugli$stopErrorsFromBeingLogged(boolean original, @Local Optional<ConfiguredPower<?, ?>> powerDefinition) {
        if (powerDefinition.isPresent() && powerDefinition.get().getFactory() instanceof ModifyScalePower && !ModList.get().isLoaded("pehkui")) {
            return false;
        }
        return original;
    }

    @ModifyVariable(method = "lambda$accept$3(Lnet/minecraft/resources/ResourceLocation;Lcom/google/gson/JsonElement;)Ljava/util/stream/Stream;", at = @At(value = "INVOKE_ASSIGN", target = "Lcom/mojang/serialization/DataResult;resultOrPartial(Ljava/util/function/Consumer;)Ljava/util/Optional;"), remap = false)
    private static Optional<ConfiguredPower<?, ?>> apugli$preventLoadingOfIntegrationPowers(Optional<ConfiguredPower<?, ?>> power, @Share("powerId") LocalRef<ResourceLocation> ref) {
        if (power.isPresent() && power.get().getFactory() instanceof ModifyScalePower && !ModList.get().isLoaded("pehkui")) {
            Apugli.LOG.warn("Power '" + ref.get() + "' could not be loaded as it uses the 'apugli:modify_scale' power type, which requires the Pehkui mod to be present. (skipping).");
            return Optional.empty();
        }
        return power;
    }
}
