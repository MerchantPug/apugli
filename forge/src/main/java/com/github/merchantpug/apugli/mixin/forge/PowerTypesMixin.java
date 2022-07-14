package com.github.merchantpug.apugli.mixin.forge;

import com.github.merchantpug.apugli.util.ApugliNamespaceAlias;
import com.google.gson.Gson;
import io.github.apace100.origins.power.PowerTypes;
import io.github.apace100.origins.registry.ModRegistries;
import io.github.apace100.origins.util.MultiJsonDataLoader;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.Optional;

@Mixin(value = PowerTypes.class, remap = false)
public abstract class PowerTypesMixin extends MultiJsonDataLoader {
    public PowerTypesMixin(Gson gson, String dataType) {
        super(gson,  dataType);
    }

    @ModifyVariable(method = "readPower(Lnet/minecraft/util/Identifier;Lcom/google/gson/JsonElement;ZLjava/util/function/BiFunction;)Lio/github/apace100/origins/power/PowerType;", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/util/Identifier;tryParse(Ljava/lang/String;)Lnet/minecraft/util/Identifier;"), ordinal = 1, remap = false)
    private Identifier resolveAlias(Identifier value) {
        if (!Optional.ofNullable(ModRegistries.POWER_FACTORY.get(value)).isPresent() && ApugliNamespaceAlias.isAlias(value)) {
            return ApugliNamespaceAlias.resolveAlias(value);
        }
        return value;
    }
}
