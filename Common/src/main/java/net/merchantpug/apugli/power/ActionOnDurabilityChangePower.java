package net.merchantpug.apugli.power;

import net.merchantpug.apugli.platform.Services;
import net.merchantpug.apugli.power.factory.SimplePowerFactory;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.calio.data.SerializableData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class ActionOnDurabilityChangePower extends Power {
    @Nullable private final Predicate<ItemStack> itemCondition;
    @Nullable private final Consumer<Entity> increaseAction;
    @Nullable private final Consumer<Entity> decreaseAction;
    @Nullable private final Consumer<Entity> breakAction;

    public ActionOnDurabilityChangePower(PowerType<?> type, LivingEntity entity,
                                         @Nullable Predicate<ItemStack> itemCondition,
                                         @Nullable Consumer<Entity> increaseAction,
                                         @Nullable Consumer<Entity> decreaseAction,
                                         @Nullable Consumer<Entity> breakAction) {
        super(type, entity);
        this.itemCondition = itemCondition;
        this.increaseAction = increaseAction;
        this.decreaseAction = decreaseAction;
        this.breakAction = breakAction;
    }

    public boolean doesApply(ItemStack stack) {
        return this.itemCondition == null || this.itemCondition.test(stack);
    }

    public void executeIncreaseAction() {
        if(increaseAction == null) return;
        this.increaseAction.accept(entity);
    }

    public void executeDecreaseAction() {
        if(decreaseAction == null) return;
        this.decreaseAction.accept(entity);
    }

    public void executeBreakAction() {
        if(breakAction == null) return;
        this.breakAction.accept(entity);
    }
    
    public static class Factory extends SimplePowerFactory<ActionOnDurabilityChangePower> {
    
        public Factory() {
            super("action_on_durability_change",
                new SerializableData()
                    .add("item_condition", Services.CONDITION.itemDataType(), null)
                    .add("increase_action", Services.ACTION.entityDataType(), null)
                    .add("decrease_action", Services.ACTION.entityDataType(), null)
                    .add("break_action", Services.ACTION.entityDataType(), null),
                data -> (type, entity) -> new ActionOnDurabilityChangePower(type, entity,
                    Services.CONDITION.itemPredicate(data, "item_condition"),
                    Services.ACTION.entityConsumer(data, "increase_action"),
                    Services.ACTION.entityConsumer(data, "decrease_action"),
                    Services.ACTION.entityConsumer(data, "break_action")
                ));
            allowCondition();
        }
    
        @Override
        public @NotNull Class<ActionOnDurabilityChangePower> getPowerClass() {
            return ActionOnDurabilityChangePower.class;
        }
        
    }

}
