package com.github.merchantpug.apugli.mixin.fabric;

import com.github.merchantpug.apugli.util.ApugliNamespaceAlias;
import com.google.gson.Gson;
import io.github.apace100.origins.power.PowerTypes;
import io.github.apace100.origins.registry.ModRegistries;
import io.github.apace100.origins.util.MultiJsonDataLoader;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = PowerTypes.class, remap = false)
public abstract class PowerTypesMixin extends MultiJsonDataLoader implements IdentifiableResourceReloadListener {

    public PowerTypesMixin(Gson gson, String dataType) {
        super(gson, dataType);
    }

    @ModifyVariable(method = "readPower(Lnet/minecraft/util/Identifier;Lcom/google/gson/JsonElement;ZLjava/util/function/BiFunction;)Lio/github/apace100/origins/power/PowerType;", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/util/Identifier;tryParse(Ljava/lang/String;)Lnet/minecraft/util/Identifier;"), ordinal = 1, remap = false)
    private Identifier resolveAlias(Identifier factoryId) {
        if (!ModRegistries.POWER_FACTORY.getOrEmpty(factoryId).isPresent() && ApugliNamespaceAlias.isAlias(factoryId)) {
            return ApugliNamespaceAlias.resolveAlias(factoryId);
        }
        return factoryId;
    }
}
