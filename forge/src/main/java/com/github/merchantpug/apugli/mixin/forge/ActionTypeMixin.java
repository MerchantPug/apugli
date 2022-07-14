package com.github.merchantpug.apugli.mixin.forge;

import com.github.merchantpug.apugli.util.ApugliNamespaceAlias;
import io.github.apace100.origins.power.factory.action.ActionFactory;
import io.github.apace100.origins.power.factory.action.ActionType;
import me.shedaniel.architectury.registry.Registry;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = ActionType.class, remap = false)
public class ActionTypeMixin<T> {
    @Shadow @Final private Registry<ActionFactory<T>> actionFactoryRegistry;

    @ModifyVariable(method = "read(Lcom/google/gson/JsonElement;)Lio/github/apace100/origins/power/factory/action/ActionFactory$Instance;", at = @At(value = "STORE", target = "Lnet/minecraft/util/Identifier;tryParse(Ljava/lang/String;)Lnet/minecraft/util/Identifier;"), remap = false)
    private Identifier resolveAlias(Identifier id) {
        if (!actionFactoryRegistry.contains(id) && ApugliNamespaceAlias.isAlias(id)) {
            return ApugliNamespaceAlias.resolveAlias(id);
        }
        return id;
    }
}
