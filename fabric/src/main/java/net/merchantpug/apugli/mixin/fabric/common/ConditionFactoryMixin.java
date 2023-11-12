package net.merchantpug.apugli.mixin.fabric.common;

import com.google.gson.JsonObject;
import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import net.merchantpug.apugli.access.FactoryInstanceAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ConditionFactory.class, remap = false)
public class ConditionFactoryMixin<T> {

    @Inject(method = "read(Lcom/google/gson/JsonObject;)Lio/github/apace100/apoli/power/factory/condition/ConditionFactory$Instance;", at = @At(value = "RETURN"), remap = false, cancellable = true)
    private void apugli$setConditionJson(JsonObject json, CallbackInfoReturnable<ConditionFactory<T>.Instance> cir) {
        ConditionFactory<T>.Instance condition = cir.getReturnValue();
        ((FactoryInstanceAccess)condition).apugli$setJson(json);
        cir.setReturnValue(condition);
    }

}
