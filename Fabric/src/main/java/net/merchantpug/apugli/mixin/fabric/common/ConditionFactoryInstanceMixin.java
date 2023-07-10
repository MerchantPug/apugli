package net.merchantpug.apugli.mixin.fabric.common;

import com.google.gson.JsonElement;
import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import net.merchantpug.apugli.access.FactoryInstanceAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value = ConditionFactory.Instance.class, remap = false)
public class ConditionFactoryInstanceMixin implements FactoryInstanceAccess {
    @Unique
    private JsonElement apugli$jsonObject;

    @Override
    public JsonElement getJson() {
        return this.apugli$jsonObject;
    }

    @Override
    public void setJson(JsonElement json) {
        this.apugli$jsonObject = json;
    }
}
