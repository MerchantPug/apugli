package com.github.merchantpug.apugli.power;

import the.great.migration.merchantpug.apugli.Apugli;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.ValueModifyingPower;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.apoli.util.modifier.Modifier;
import io.github.apace100.calio.data.SerializableData;

import java.util.List;
import java.util.function.Predicate;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class ModifyBreedingCooldownPower extends ValueModifyingPower {

    Predicate<Tuple<Entity, Entity>> biEntityCondition;

    public static PowerFactory<?> getFactory() {
        return new PowerFactory<ModifyBreedingCooldownPower>(Apugli.identifier("modify_breeding_cooldown"),
                new SerializableData()
                        .add("modifier", Modifier.DATA_TYPE, null)
                        .add("modifiers", Modifier.LIST_TYPE, null)
                        .add("bientity_condition", Services.CONDITION.biEntityDataType(), null),
                data ->
                        (type, entity) -> {
                            ModifyBreedingCooldownPower power = new ModifyBreedingCooldownPower(type, entity, (ConditionFactory<Tuple<Entity, Entity>>.Instance)data.get("bientity_condition"));
                            if(data.isPresent("modifier")) {
                                power.addModifier(data.get("modifier"));
                            }
                            if(data.isPresent("modifiers")) {
                                ((List<Modifier>)data.get("modifiers")).forEach(power::addModifier);
                            }
                            return power;
                        })
                .allowCondition();
    }

    public boolean doesApply(Entity animal) {
        return biEntityCondition == null || biEntityCondition.test(new Tuple<>(entity, animal));
    }

    public ModifyBreedingCooldownPower(PowerType<?> type, LivingEntity entity, Predicate<Tuple<Entity, Entity>> biEntitycondition) {
        super(type, entity);
        this.biEntityCondition = biEntitycondition;
    }
}
