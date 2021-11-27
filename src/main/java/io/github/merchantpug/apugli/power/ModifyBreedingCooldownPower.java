package io.github.merchantpug.apugli.power;

import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.ValueModifyingPower;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.merchantpug.apugli.Apugli;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.util.Pair;

import java.util.List;
import java.util.function.Predicate;

public class ModifyBreedingCooldownPower extends ValueModifyingPower {

    Predicate<Pair<Entity, Entity>> biEntitycondition;

    public static PowerFactory<?> getFactory() {
        return new PowerFactory<ModifyBreedingCooldownPower>(Apugli.identifier("modify_breeding_cooldown"),
                new SerializableData()
                        .add("modifier", SerializableDataTypes.ATTRIBUTE_MODIFIER, null)
                        .add("modifiers", SerializableDataTypes.ATTRIBUTE_MODIFIERS, null)
                        .add("bientity_condition", ApoliDataTypes.BIENTITY_CONDITION, null),
                data ->
                        (type, entity) -> {
                            ModifyBreedingCooldownPower power = new ModifyBreedingCooldownPower(type, entity, (ConditionFactory<Pair<Entity, Entity>>.Instance)data.get("bientity_condition"));
                            if(data.isPresent("modifier")) {
                                power.addModifier(data.getModifier("modifier"));
                            }
                            if(data.isPresent("modifiers")) {
                                ((List<EntityAttributeModifier>)data.get("modifiers")).forEach(power::addModifier);
                            }
                            return power;
                        })
                .allowCondition();
    }

    public boolean doesApply(Entity animal) {
        return biEntitycondition == null || biEntitycondition.test(new Pair<>(entity, animal));
    }

    public ModifyBreedingCooldownPower(PowerType<?> type, LivingEntity entity, Predicate<Pair<Entity, Entity>> biEntitycondition) {
        super(type, entity);
        this.biEntitycondition = biEntitycondition;
    }
}
