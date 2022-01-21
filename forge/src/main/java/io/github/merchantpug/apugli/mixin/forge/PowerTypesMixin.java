package io.github.merchantpug.apugli.mixin.forge;

import com.google.gson.Gson;
import io.github.apace100.origins.power.PowerTypes;
import io.github.apace100.origins.registry.ModRegistries;
import io.github.apace100.origins.util.MultiJsonDataLoader;
import io.github.merchantpug.apugli.util.ApugliNamespaceAlias;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.Optional;

@Mixin(value = PowerTypes.class, remap = false)
public abstract class PowerTypesMixin extends MultiJsonDataLoader {
    public PowerTypesMixin(Gson gson, String dataType) {
        super(gson,  dataType);
    }

    @ModifyArg(method = "readPower(Lnet/minecraft/util/Identifier;Lcom/google/gson/JsonElement;ZLjava/util/function/BiFunction;)Lio/github/apace100/origins/power/PowerType;", at = @At(value = "INVOKE", target = "Lme/shedaniel/architectury/registry/Registry;get(Lnet/minecraft/util/Identifier;)Ljava/lang/Object;"))
    private Identifier resolveAlias(Identifier value) {
        if (!Optional.ofNullable(ModRegistries.POWER_FACTORY.get(value)).isPresent() && ApugliNamespaceAlias.isAlias(value)) {
            return ApugliNamespaceAlias.resolveAlias(value);
        }
        return value;
    }
}
