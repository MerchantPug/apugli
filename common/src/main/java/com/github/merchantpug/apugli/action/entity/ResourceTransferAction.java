package com.github.merchantpug.apugli.action.entity;

import com.github.merchantpug.apugli.Apugli;
import com.github.merchantpug.apugli.util.ModComponents;
import io.github.apace100.origins.component.OriginComponent;
import io.github.apace100.origins.power.CooldownPower;
import io.github.apace100.origins.power.Power;
import io.github.apace100.origins.power.VariableIntPower;
import io.github.apace100.origins.power.factory.action.ActionFactory;
import io.github.apace100.origins.util.SerializableData;
import io.github.apace100.origins.util.SerializableDataType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

public class ResourceTransferAction {
    public static void action(SerializableData.Instance data, Entity entity) {
        if (!(entity instanceof PlayerEntity)) return;
        PlayerEntity player = (PlayerEntity) entity;
        OriginComponent component = ModComponents.getOriginComponent(player);
        Power resource = component.getPower(data.get("resource"));
        Power provider = component.getPower(data.get("provider"));
        if (!(provider instanceof VariableIntPower || provider instanceof CooldownPower)) return;
        if (resource instanceof VariableIntPower) {
            VariableIntPower vip = (VariableIntPower)resource;
            if (provider instanceof VariableIntPower) {
                vip.setValue(((VariableIntPower) provider).getValue());
            } else {
                vip.setValue(((CooldownPower) provider).getRemainingTicks());
            }
            OriginComponent.sync(player);
        } else if (resource instanceof CooldownPower) {
            CooldownPower cdp = (CooldownPower)resource;
            if (provider instanceof VariableIntPower) {
                cdp.setCooldown(((VariableIntPower) provider).getValue());
            } else {
                cdp.setCooldown(((CooldownPower) provider).getRemainingTicks());
            }
            OriginComponent.sync(player);
        }
    }

    public static ActionFactory<Entity> getFactory() {
        return new ActionFactory<>(Apugli.identifier("resource_transfer"), new SerializableData()
                .add("resource", SerializableDataType.POWER_TYPE)
                .add("provider", SerializableDataType.POWER_TYPE),
                ResourceTransferAction::action
        );
    }
}