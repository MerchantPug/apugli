package io.github.merchantpug.apugli.mixin.fabric;

import io.github.apace100.origins.power.factory.condition.ConditionFactory;
import io.github.apace100.origins.power.factory.condition.ConditionType;
import io.github.merchantpug.apugli.Apugli;
import io.github.merchantpug.apugli.util.ApugliNamespaceAlias;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(value = ConditionType.class)
public class ConditionTypeMixin<T> {
    @Shadow @Final private Registry<ConditionFactory<T>> conditionRegistry;

    @ModifyArg(method = "read(Lcom/google/gson/JsonElement;)Lio/github/apace100/origins/power/factory/condition/ConditionFactory$Instance;", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/registry/Registry;getOrEmpty(Lnet/minecraft/util/Identifier;)Ljava/util/Optional;"), remap = false)
    private Identifier resolveAlias(@Nullable Identifier id) {
        if (id == null) return null;
        if (!this.conditionRegistry.containsId(id) && ApugliNamespaceAlias.isAlias(id)) {
            return ApugliNamespaceAlias.resolveAlias(id);
        }
        return id;
    }
}
