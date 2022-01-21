package io.github.merchantpug.apugli.mixin.forge;

import io.github.apace100.origins.power.factory.action.ActionType;
import io.github.merchantpug.apugli.util.ApugliNamespaceAlias;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(value = ActionType.class, remap = false)
public class ActionTypeMixin<T> {
    @ModifyArg(method = "read(Lcom/google/gson/JsonElement;)Lio/github/apace100/origins/power/factory/action/ActionFactory$Instance;", at = @At(value = "INVOKE", target = "Lme/shedaniel/architectury/registry/Registry;get(Lnet/minecraft/util/Identifier;)Ljava/lang/Object;"))
    private Identifier resolveAlias(@Nullable Identifier id) {
        if (ApugliNamespaceAlias.isAlias(id)) {
            return ApugliNamespaceAlias.resolveAlias(id);
        }
        return id;
    }
}
