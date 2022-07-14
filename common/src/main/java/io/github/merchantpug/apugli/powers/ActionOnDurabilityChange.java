package io.github.merchantpug.apugli.powers;

import io.github.apace100.origins.power.Power;
import io.github.apace100.origins.power.PowerType;
import io.github.apace100.origins.power.factory.PowerFactory;
import io.github.apace100.origins.util.SerializableData;
import io.github.apace100.origins.util.SerializableDataType;
import io.github.merchantpug.apugli.Apugli;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class ActionOnDurabilityChange extends Power {
    private final Predicate<ItemStack> itemCondition;
    private final Consumer<Entity> increaseAction;
    private final Consumer<Entity> decreaseAction;
    private final Consumer<Entity> breakAction;

    public ActionOnDurabilityChange(PowerType<?> type, PlayerEntity player, Predicate<ItemStack> itemCondition, Consumer<Entity> increaseAction, Consumer<Entity> decreaseAction, Consumer<Entity> breakAction) {
        super(type, player);
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
        this.increaseAction.accept(player);
    }

    public void executeDecreaseAction() {
        if (decreaseAction == null) return;
        this.decreaseAction.accept(player);
    }

    public void executeBreakAction() {
        if (breakAction == null) return;
        this.breakAction.accept(player);
    }

    public static PowerFactory<?> getFactory() {
        return new PowerFactory<ActionOnDurabilityChange>(
                Apugli.identifier("action_on_durability_change"),
                new SerializableData()
                        .add("slot", SerializableDataType.EQUIPMENT_SLOT)
                        .add("item_condition", SerializableDataType.ITEM_CONDITION, null)
                        .add("increase_action", SerializableDataType.ENTITY_ACTION, null)
                        .add("decrease_action", SerializableDataType.ENTITY_ACTION, null)
                        .add("break_action", SerializableDataType.ENTITY_ACTION, null),
                data -> (type, entity) -> new ActionOnDurabilityChange(type, entity, data.isPresent("item_condition") ? data.get("item_condition") : c -> true, data.get("increase_action"), data.get("decrease_action"), data.get("break_action")))
                .allowCondition();
    }
}
