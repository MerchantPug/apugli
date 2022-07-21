package com.github.merchantpug.apugli.powers;

import io.github.apace100.origins.power.Power;
import io.github.apace100.origins.power.PowerType;
import io.github.apace100.origins.power.factory.PowerFactory;
import io.github.apace100.origins.power.factory.action.ActionFactory;
import io.github.apace100.origins.power.factory.condition.ConditionFactory;
import io.github.apace100.origins.util.SerializableData;
import io.github.apace100.origins.util.SerializableDataType;
import com.github.merchantpug.apugli.Apugli;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class PreventBreedingPower extends Power {
    private final Predicate<LivingEntity> targetCondition;
    private final Consumer<Entity> targetAction;
    private final Consumer<Entity> selfAction;
    public final boolean preventFollow;

    public static PowerFactory<?> getFactory() {
        return new PowerFactory<PreventBreedingPower>(Apugli.identifier("prevent_breeding"),
                new SerializableData()
                        .add("target_condition", SerializableDataType.ENTITY_CONDITION, null)
                        .add("target_action", SerializableDataType.ENTITY_ACTION, null)
                        .add("self_action", SerializableDataType.ENTITY_ACTION, null)
                        .add("prevent_follow", SerializableDataType.BOOLEAN, true),
                data ->
                        (type, player) ->
                                new PreventBreedingPower(type, player, (ConditionFactory<LivingEntity>.Instance)data.get("target_condition"), (ActionFactory<Entity>.Instance)data.get("target_action"), (ActionFactory<Entity>.Instance)data.get("self_action"), data.getBoolean("prevent_follow")))
                .allowCondition();
    }

    public PreventBreedingPower(PowerType<?> type, PlayerEntity player, Predicate<LivingEntity> targetCondition, Consumer<Entity> targetAction, Consumer<Entity> selfAction, boolean preventFollow) {
        super(type, player);
        this.targetCondition = targetCondition;
        this.targetAction = targetAction;
        this.selfAction = selfAction;
        this.preventFollow = preventFollow;
    }

    public boolean doesApply(LivingEntity mobEntity) {
        return this.targetCondition == null || this.targetCondition.test(mobEntity);
    }

    public void executeAction(Entity mobEntity) {
        if (targetAction != null) {
            targetAction.accept(mobEntity);
        }
        if (selfAction != null) {
            selfAction.accept(player);
        }
    }

    public boolean hasActions() {
        return targetAction != null && selfAction != null;
    }
}
