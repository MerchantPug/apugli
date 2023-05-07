package net.merchantpug.apugli.power;

import io.github.apace100.calio.data.SerializableDataTypes;
import net.merchantpug.apugli.access.ItemStackAccess;
import net.merchantpug.apugli.platform.Services;
import net.merchantpug.apugli.power.factory.SimplePowerFactory;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.calio.data.SerializableData;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.mutable.Mutable;
import org.apache.commons.lang3.mutable.MutableObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class ActionOnDurabilityChangePower extends Power {
    @Nullable private final EquipmentSlot slot;
    @Nullable private final Predicate<ItemStack> itemCondition;
    @Nullable private final Consumer<Entity> increaseAction;
    @Nullable private final Consumer<Tuple<Level, Mutable<ItemStack>>> itemIncreaseAction;
    @Nullable private final Consumer<Entity> decreaseAction;
    @Nullable private final Consumer<Tuple<Level, Mutable<ItemStack>>> itemDecreaseAction;
    @Nullable private final Consumer<Entity> breakAction;
    @Nullable private final Consumer<Tuple<Level, Mutable<ItemStack>>> itemBreakAction;

    public ActionOnDurabilityChangePower(PowerType<?> type, LivingEntity entity,
                                         @Nullable EquipmentSlot slot,
                                         @Nullable Predicate<ItemStack> itemCondition,
                                         @Nullable Consumer<Entity> increaseAction,
                                         @Nullable Consumer<Tuple<Level, Mutable<ItemStack>>> itemIncreaseAction,
                                         @Nullable Consumer<Entity> decreaseAction,
                                         @Nullable Consumer<Tuple<Level, Mutable<ItemStack>>> itemDecreaseAction,
                                         @Nullable Consumer<Entity> breakAction,
                                         @Nullable Consumer<Tuple<Level, Mutable<ItemStack>>> itemBreakAction) {
        super(type, entity);
        this.slot = slot;
        this.itemCondition = itemCondition;
        this.increaseAction = increaseAction;
        this.itemIncreaseAction = itemIncreaseAction;
        this.decreaseAction = decreaseAction;
        this.itemDecreaseAction = itemDecreaseAction;
        this.breakAction = breakAction;
        this.itemBreakAction = itemBreakAction;
    }

    public boolean doesApply(ItemStack stack) {
        return (slot == null || entity.getItemBySlot(slot).equals(stack)) && (this.itemCondition == null || this.itemCondition.test(stack));
    }

    public void executeIncreaseAction(ItemStack stack) {
        Mutable<ItemStack> mutable = new MutableObject<>(stack);
        if(increaseAction != null) {
            this.increaseAction.accept(entity);
        }
        if(itemIncreaseAction != null) {
            this.itemIncreaseAction.accept(new Tuple<>(entity.level, mutable));
        }
        boolean succeededCheck = false;
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (entity.getItemBySlot(slot).equals(stack)) {
                entity.setItemSlot(slot, mutable.getValue());
                succeededCheck = true;
                break;
            }
        }
        if (!succeededCheck && entity instanceof Player player) {
            for (int i = 0; i < player.getInventory().items.size(); ++i) {
                if (player.getInventory().items.get(i).equals(stack)) {
                    player.getInventory().items.set(i, mutable.getValue());
                }
            }
        }
    }

    public void executeDecreaseAction(ItemStack stack) {
        Mutable<ItemStack> mutable = new MutableObject<>(stack);
        if (decreaseAction != null) {
            this.decreaseAction.accept(entity);
        }
        if (itemDecreaseAction != null) {
            this.itemDecreaseAction.accept(new Tuple<>(entity.level, mutable));
        }
        boolean succeededCheck = false;
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (entity.getItemBySlot(slot).equals(stack)) {
                entity.setItemSlot(slot, mutable.getValue());
                succeededCheck = true;
                break;
            }
        }
        if (!succeededCheck && entity instanceof Player player) {
            for (int i = 0; i < player.getInventory().items.size(); ++i) {
                if (player.getInventory().items.get(i).equals(stack)) {
                    player.getInventory().items.set(i, mutable.getValue());
                }
            }
        }
    }

    public void executeBreakAction(ItemStack stack) {
        Mutable<ItemStack> mutable = new MutableObject<>(stack);
        if(breakAction != null) {
            this.breakAction.accept(entity);
        }
        if(itemBreakAction != null) {
            this.itemBreakAction.accept(new Tuple<>(entity.level, mutable));
        }
        boolean succeededCheck = false;
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (entity.getItemBySlot(slot).equals(stack)) {
                entity.setItemSlot(slot, mutable.getValue());
                succeededCheck = true;
                break;
            }
        }
        if (!succeededCheck && entity instanceof Player player) {
            for (int i = 0; i < player.getInventory().items.size(); ++i) {
                if (player.getInventory().items.get(i).equals(stack)) {
                    player.getInventory().items.set(i, mutable.getValue());
                }
            }
        }
    }
    
    public static class Factory extends SimplePowerFactory<ActionOnDurabilityChangePower> {
    
        public Factory() {
            super("action_on_durability_change",
                new SerializableData()
                        .add("slot", SerializableDataTypes.EQUIPMENT_SLOT, null)
                        .add("item_condition", Services.CONDITION.itemDataType(), null)
                        .add("increase_action", Services.ACTION.entityDataType(), null)
                        .add("increase_item_action", Services.ACTION.itemDataType(), null)
                        .add("decrease_action", Services.ACTION.entityDataType(), null)
                        .add("decrease_item_action", Services.ACTION.itemDataType(), null)
                        .add("break_action", Services.ACTION.entityDataType(), null)
                        .add("break_item_action", Services.ACTION.itemDataType(), null),
                data -> (type, entity) -> new ActionOnDurabilityChangePower(type, entity,
                        data.get("slot"),
                        Services.CONDITION.itemPredicate(data, "item_condition"),
                        Services.ACTION.entityConsumer(data, "increase_action"),
                        Services.ACTION.itemConsumer(data, "increase_item_action"),
                        Services.ACTION.entityConsumer(data, "decrease_action"),
                        Services.ACTION.itemConsumer(data, "decrease_item_action"),
                        Services.ACTION.entityConsumer(data, "break_action"),
                        Services.ACTION.itemConsumer(data, "break_item_action")
                ));
            allowCondition();
        }
    
        @Override
        public @NotNull Class<ActionOnDurabilityChangePower> getPowerClass() {
            return ActionOnDurabilityChangePower.class;
        }
        
    }

}
