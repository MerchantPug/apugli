package net.merchantpug.apugli.mixin.fabric.common;

import com.google.gson.JsonElement;
import io.github.apace100.apoli.power.factory.action.ActionFactory;
import net.merchantpug.apugli.access.FactoryInstanceAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value = ActionFactory.Instance.class, remap = false)
public class ActionFactoryInstanceMixin implements FactoryInstanceAccess {
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
