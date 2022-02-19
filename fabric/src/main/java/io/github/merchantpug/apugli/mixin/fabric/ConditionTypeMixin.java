package io.github.merchantpug.apugli.mixin.fabric;

import io.github.apace100.origins.power.factory.condition.ConditionFactory;
import io.github.apace100.origins.power.factory.condition.ConditionType;
import io.github.merchantpug.apugli.util.ApugliNamespaceAlias;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = ConditionType.class, remap = false)
public class ConditionTypeMixin<T> {
    @Shadow @Final private Registry<ConditionFactory<T>> conditionRegistry;

    @ModifyVariable(method = "read(Lcom/google/gson/JsonElement;)Lio/github/apace100/origins/power/factory/condition/ConditionFactory$Instance;", at = @At(value = "STORE", target = "Lnet/minecraft/util/Identifier;tryParse(Ljava/lang/String;)Lnet/minecraft/util/Identifier;"), remap = false)
    private Identifier resolveAlias(Identifier id) {
        if (!this.conditionRegistry.getOrEmpty(id).isPresent() && ApugliNamespaceAlias.isAlias(id)) {
            return ApugliNamespaceAlias.resolveAlias(id);
        }
        return id;
    }
}
