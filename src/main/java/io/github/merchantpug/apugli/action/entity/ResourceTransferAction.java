package io.github.merchantpug.apugli.action.entity;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.CooldownPower;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.VariableIntPower;
import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.merchantpug.apugli.Apugli;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;

public class ResourceTransferAction {
    public static void action(SerializableData.Instance data, Entity entity) {
        if (!(entity instanceof LivingEntity)) return;
        PowerHolderComponent component = PowerHolderComponent.KEY.get(entity);
        Power resource = component.getPower(data.get("resource"));
        Power provider = component.getPower(data.get("provider"));
        if (!(provider instanceof VariableIntPower || provider instanceof CooldownPower)) return;
        if (resource instanceof VariableIntPower vip) {
            if (provider instanceof VariableIntPower) {
                vip.setValue(((VariableIntPower) provider).getValue());
            } else {
                vip.setValue(((CooldownPower) provider).getRemainingTicks());
            }
            PowerHolderComponent.syncPower(entity, vip.getType());
        } else if (resource instanceof CooldownPower cdp) {
            if (provider instanceof VariableIntPower) {
                cdp.setCooldown(((VariableIntPower) provider).getValue());
            } else {
                cdp.setCooldown(((CooldownPower) provider).getRemainingTicks());
            }
            PowerHolderComponent.syncPower(entity, cdp.getType());
        }
    }

    public static ActionFactory<Entity> getFactory() {
        return new ActionFactory<>(Apugli.identifier("resource_transfer"), new SerializableData()
                .add("resource", ApoliDataTypes.POWER_TYPE)
                .add("provider", ApoliDataTypes.POWER_TYPE),
                ResourceTransferAction::action
        );
    }
}
