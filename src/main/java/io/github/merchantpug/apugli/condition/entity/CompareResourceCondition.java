package io.github.merchantpug.apugli.condition.entity;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.CooldownPower;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.VariableIntPower;
import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.apoli.util.Comparison;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataType;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.merchantpug.apugli.Apugli;
import io.github.merchantpug.apugli.access.ParticleAccess;
import io.github.merchantpug.apugli.mixin.client.ClientPlayerEntityAccessor;
import io.github.merchantpug.apugli.mixin.client.ParticleManagerAccessor;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.entity.Entity;
import net.minecraft.particle.ParticleEffect;

import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

public class CompareResourceCondition {
    public static boolean condition(SerializableData.Instance data, Entity entity) {
        Comparison comparison = ((Comparison) data.get("comparison"));
        PowerHolderComponent component = PowerHolderComponent.KEY.get(entity);
        Power resource = component.getPower(data.get("resource"));
        Power compareTo = component.getPower(data.get("compare_to"));
        if (!(compareTo instanceof VariableIntPower || compareTo instanceof CooldownPower)) return false;
        if (resource instanceof VariableIntPower vip) {
            if (compareTo instanceof VariableIntPower) {
                return comparison.compare(vip.getValue(), ((VariableIntPower) compareTo).getValue());
            } else {
                return comparison.compare(vip.getValue(), ((CooldownPower) compareTo).getRemainingTicks());
            }
        } else if (resource instanceof CooldownPower cdp) {
            if (compareTo instanceof VariableIntPower) {
                return comparison.compare(cdp.getRemainingTicks(), ((VariableIntPower) compareTo).getValue());
            } else {
                return comparison.compare(cdp.getRemainingTicks(), ((CooldownPower) compareTo).getRemainingTicks());
            }
        }
        return false;
    }

    public static ConditionFactory<Entity> getFactory() {
        return new ConditionFactory<>(Apugli.identifier("compare_resource"), new SerializableData()
                .add("resource", ApoliDataTypes.POWER_TYPE)
                .add("compare_to", ApoliDataTypes.POWER_TYPE)
                .add("comparison",ApoliDataTypes.COMPARISON, Comparison.GREATER_THAN_OR_EQUAL),
                CompareResourceCondition::condition
        );
    }
}
