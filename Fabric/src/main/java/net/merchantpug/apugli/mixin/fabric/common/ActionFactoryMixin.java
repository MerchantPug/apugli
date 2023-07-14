package net.merchantpug.apugli.mixin.fabric.common;

import com.google.gson.JsonObject;
import io.github.apace100.apoli.power.factory.action.ActionFactory;
import net.merchantpug.apugli.access.FactoryInstanceAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ActionFactory.class, remap = false)
public class ActionFactoryMixin<T> {

    @Inject(method = "read(Lcom/google/gson/JsonObject;)Lio/github/apace100/apoli/power/factory/action/ActionFactory$Instance;", at = @At(value = "RETURN"), remap = false, cancellable = true)
    private void setConditionJson(JsonObject json, CallbackInfoReturnable<ActionFactory<T>.Instance> cir) {
        ActionFactory<T>.Instance condition = cir.getReturnValue();
        ((FactoryInstanceAccess)condition).apugli$setJson(json);
        cir.setReturnValue(condition);
    }

}
