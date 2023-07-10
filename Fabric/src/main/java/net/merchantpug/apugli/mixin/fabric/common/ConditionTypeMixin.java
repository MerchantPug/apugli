package net.merchantpug.apugli.mixin.fabric.common;

import com.google.gson.JsonElement;
import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.apoli.power.factory.condition.ConditionType;
import net.merchantpug.apugli.access.FactoryInstanceAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ConditionType.class, remap = false)
public class ConditionTypeMixin<T> {

    @Inject(method = "read(Lcom/google/gson/JsonElement;)Lio/github/apace100/apoli/power/factory/condition/ConditionFactory$Instance;", at = @At(value = "RETURN"), remap = false)
    private void setConditionJson(JsonElement jsonElement, CallbackInfoReturnable<ConditionFactory<T>.Instance> cir) {
        ConditionFactory<T>.Instance condition = cir.getReturnValue();
        ((FactoryInstanceAccess)condition).setJson(jsonElement);
    }

}
