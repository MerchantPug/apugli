package com.github.merchantpug.apugli.power;

import com.github.merchantpug.apugli.Apugli;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class ActionOnDurabilityChange extends Power {
    private final Predicate<ItemStack> itemCondition;
    private final Consumer<Entity> increaseAction;
    private final Consumer<Entity> decreaseAction;
    private final Consumer<Entity> breakAction;

    public ActionOnDurabilityChange(PowerType<?> type, LivingEntity entity, Predicate<ItemStack> itemCondition, Consumer<Entity> increaseAction, Consumer<Entity> decreaseAction, Consumer<Entity> breakAction) {
        super(type, entity);
        this.itemCondition = itemCondition;
        this.increaseAction = increaseAction;
        this.decreaseAction = decreaseAction;
        this.breakAction = breakAction;
    }

    public boolean doesApply(ItemStack stack) {
        return this.itemCondition.test(stack);
    }

    public void executeIncreaseAction() {
        if (increaseAction == null) return;
        this.increaseAction.accept(entity);
    }

    public void executeDecreaseAction() {
        if (decreaseAction == null) return;
        this.decreaseAction.accept(entity);
    }

    public void executeBreakAction() {
        if (breakAction == null) return;
        this.breakAction.accept(entity);
    }

    public static PowerFactory<?> getFactory() {
        return new PowerFactory<ActionOnDurabilityChange>(
                Apugli.identifier("action_on_durability_change"),
                new SerializableData()
                        .add("item_condition", ApoliDataTypes.ITEM_CONDITION, null)
                        .add("increase_action", ApoliDataTypes.ENTITY_ACTION, null)
                        .add("decrease_action", ApoliDataTypes.ENTITY_ACTION, null)
                        .add("break_action", ApoliDataTypes.ENTITY_ACTION, null),
                data -> (type, entity) -> new ActionOnDurabilityChange(type, entity, data.isPresent("item_condition") ? data.get("item_condition") : c -> true, data.get("increase_action"), data.get("decrease_action"), data.get("break_action")))
                .allowCondition();
    }
}
