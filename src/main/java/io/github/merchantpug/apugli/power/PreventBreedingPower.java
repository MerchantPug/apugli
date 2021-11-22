package io.github.merchantpug.apugli.power;

import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.merchantpug.apugli.Apugli;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Pair;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class PreventBreedingPower extends Power {
    private final Predicate<Pair<Entity, Entity>> biEntityCondition;
    private final Consumer<Pair<Entity, Entity>> biEntityAction;

    public static PowerFactory<?> getFactory() {
        return new PowerFactory<PreventBreedingPower>(Apugli.identifier("prevent_breeding"),
                new SerializableData()
                        .add("bientity_condition", ApoliDataTypes.BIENTITY_CONDITION, null)
                        .add("bientity_action", ApoliDataTypes.BIENTITY_ACTION, null),
                data ->
                        (type, entity) ->
                                new PreventBreedingPower(type, entity, (ConditionFactory<Pair<Entity, Entity>>.Instance)data.get("bientity_condition"), (ActionFactory<Pair<Entity, Entity>>.Instance)data.get("bientity_action")))
                .allowCondition();
    }

    public PreventBreedingPower(PowerType<?> type, LivingEntity entity, Predicate<Pair<Entity, Entity>> biEntityCondition, Consumer<Pair<Entity, Entity>> biEntityAction) {
        super(type, entity);
        this.biEntityCondition = biEntityCondition;
        this.biEntityAction = biEntityAction;
    }

    public boolean doesApply(Entity mobEntity) {
        return this.biEntityCondition == null || this.biEntityCondition.test(new Pair<>(entity, mobEntity));
    }

    public void executeAction(Entity mobEntity) {
        if (biEntityAction == null) return;
        this.biEntityAction.accept(new Pair<>(entity, mobEntity));
    }
}
