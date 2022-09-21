package net.merchantpug.apugli.power;

import net.merchantpug.apugli.Apugli;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.ValueModifyingPower;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.apoli.util.modifier.Modifier;
import io.github.apace100.calio.data.SerializableData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Pair;

import java.util.List;
import java.util.function.Predicate;

public class ModifyBreedingCooldownPower extends ValueModifyingPower {

    Predicate<Pair<Entity, Entity>> biEntityCondition;

    public static PowerFactory<?> getFactory() {
        return new PowerFactory<ModifyBreedingCooldownPower>(Apugli.identifier("modify_breeding_cooldown"),
                new SerializableData()
                        .add("modifier", Modifier.DATA_TYPE, null)
                        .add("modifiers", Modifier.LIST_TYPE, null)
                        .add("bientity_condition", ApoliDataTypes.BIENTITY_CONDITION, null),
                data ->
                        (type, entity) -> {
                            ModifyBreedingCooldownPower power = new ModifyBreedingCooldownPower(type, entity, (ConditionFactory<Pair<Entity, Entity>>.Instance)data.get("bientity_condition"));
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
        return biEntityCondition == null || biEntityCondition.test(new Pair<>(entity, animal));
    }

    public ModifyBreedingCooldownPower(PowerType<?> type, LivingEntity entity, Predicate<Pair<Entity, Entity>> biEntitycondition) {
        super(type, entity);
        this.biEntityCondition = biEntitycondition;
    }
}
