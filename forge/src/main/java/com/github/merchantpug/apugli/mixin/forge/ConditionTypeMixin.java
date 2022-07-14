package com.github.merchantpug.apugli.mixin.forge;

import com.github.merchantpug.apugli.util.ApugliNamespaceAlias;
import io.github.apace100.origins.power.factory.condition.ConditionFactory;
import io.github.apace100.origins.power.factory.condition.ConditionType;
import me.shedaniel.architectury.registry.Registry;
import net.minecraft.util.Identifier;
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
        if (!conditionRegistry.contains(id) && ApugliNamespaceAlias.isAlias(id)) {
            return ApugliNamespaceAlias.resolveAlias(id);
        }
        return id;
    }
}
