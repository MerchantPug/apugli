package net.merchantpug.apugli.power;

import net.merchantpug.apugli.Apugli;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Pair;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class PreventBreedingPower extends Power {
    private final Predicate<Pair<Entity, Entity>> biEntityCondition;
    private final Consumer<Pair<Entity, Entity>> biEntityAction;
    public final boolean preventFollow;

    public static PowerFactory<?> getFactory() {
        return new PowerFactory<PreventBreedingPower>(Apugli.identifier("prevent_breeding"),
                new SerializableData()
                        .add("bientity_condition", ApoliDataTypes.BIENTITY_CONDITION, null)
                        .add("bientity_action", ApoliDataTypes.BIENTITY_ACTION, null)
                        .add("prevent_follow", SerializableDataTypes.BOOLEAN, true),
                data ->
                        (type, entity) ->
                                new PreventBreedingPower(type, entity, (ConditionFactory<Pair<Entity, Entity>>.Instance)data.get("bientity_condition"), (ActionFactory<Pair<Entity, Entity>>.Instance)data.get("bientity_action"), data.getBoolean("prevent_follow")))
                .allowCondition();
    }

    public PreventBreedingPower(PowerType<?> type, LivingEntity entity, Predicate<Pair<Entity, Entity>> biEntityCondition, Consumer<Pair<Entity, Entity>> biEntityAction, boolean preventFollow) {
        super(type, entity);
        this.biEntityCondition = biEntityCondition;
        this.biEntityAction = biEntityAction;
        this.preventFollow = preventFollow;
    }

    public boolean doesApply(Entity mobEntity) {
        return this.biEntityCondition == null || this.biEntityCondition.test(new Pair<>(entity, mobEntity));
    }

    public void executeAction(Entity mobEntity) {
        if (biEntityAction == null) return;
        this.biEntityAction.accept(new Pair<>(entity, mobEntity));
    }

    public boolean hasAction() {
        return biEntityAction != null;
    }
}
