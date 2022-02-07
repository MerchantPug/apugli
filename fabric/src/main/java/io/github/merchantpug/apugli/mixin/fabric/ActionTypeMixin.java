package io.github.merchantpug.apugli.mixin.fabric;

import io.github.apace100.origins.power.factory.action.ActionFactory;
import io.github.apace100.origins.power.factory.action.ActionType;
import io.github.merchantpug.apugli.util.ApugliNamespaceAlias;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(value = ActionType.class)
public class ActionTypeMixin<T> {

    @Shadow @Final private Registry<ActionFactory<T>> actionFactoryRegistry;

    @ModifyArg(method = "read(Lcom/google/gson/JsonElement;)Lio/github/apace100/origins/power/factory/action/ActionFactory$Instance;", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/registry/Registry;getOrEmpty(Lnet/minecraft/util/Identifier;)Ljava/util/Optional;"), remap = false)
    private Identifier resolveAlias(@Nullable Identifier id) {
        if (id == null) return null;
        if (!actionFactoryRegistry.containsId(id) && ApugliNamespaceAlias.isAlias(id)) {
            return ApugliNamespaceAlias.resolveAlias(id);
        }
        return id;
    }
}
