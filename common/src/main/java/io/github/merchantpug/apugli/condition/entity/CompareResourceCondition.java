package io.github.merchantpug.apugli.condition.entity;

import io.github.apace100.origins.component.OriginComponent;
import io.github.apace100.origins.power.CooldownPower;
import io.github.apace100.origins.power.Power;
import io.github.apace100.origins.power.VariableIntPower;
import io.github.apace100.origins.power.factory.condition.ConditionFactory;
import io.github.apace100.origins.util.Comparison;
import io.github.apace100.origins.util.SerializableData;
import io.github.apace100.origins.util.SerializableDataType;
import io.github.merchantpug.apugli.Apugli;
import io.github.merchantpug.apugli.util.ModComponents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

public class CompareResourceCondition {
    public static boolean condition(SerializableData.Instance data, LivingEntity entity) {
        if (!(entity instanceof PlayerEntity)) return false;
        PlayerEntity player = (PlayerEntity) entity;
        Comparison comparison = ((Comparison) data.get("comparison"));
        OriginComponent component = ModComponents.getOriginComponent(player);
        Power resource = component.getPower(data.get("resource"));
        Power compareTo = component.getPower(data.get("compare_to"));
        if (!(compareTo instanceof VariableIntPower || compareTo instanceof CooldownPower)) return false;
        if (resource instanceof VariableIntPower) {
            VariableIntPower vip = (VariableIntPower)resource;
            if (compareTo instanceof VariableIntPower) {
                return comparison.compare(vip.getValue(), ((VariableIntPower) compareTo).getValue());
            } else {
                return comparison.compare(vip.getValue(), ((CooldownPower) compareTo).getRemainingTicks());
            }
        } else if (resource instanceof CooldownPower) {
            CooldownPower cdp = (CooldownPower)resource;
            if (compareTo instanceof VariableIntPower) {
                return comparison.compare(cdp.getRemainingTicks(), ((VariableIntPower) compareTo).getValue());
            } else {
                return comparison.compare(cdp.getRemainingTicks(), ((CooldownPower) compareTo).getRemainingTicks());
            }
        }
        return false;
    }

    public static ConditionFactory<LivingEntity> getFactory() {
        return new ConditionFactory<>(Apugli.identifier("compare_resource"), new SerializableData()
                .add("resource", SerializableDataType.POWER_TYPE)
                .add("compare_to", SerializableDataType.POWER_TYPE)
                .add("comparison",SerializableDataType.COMPARISON, Comparison.GREATER_THAN_OR_EQUAL),
                CompareResourceCondition::condition
        );
    }
}
