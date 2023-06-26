package net.merchantpug.apugli.power;

import com.mojang.datafixers.util.Either;
import io.github.apace100.calio.data.SerializableDataTypes;
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

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class ActionOnDurabilityChangePower extends Power {
    @Nullable private final EquipmentSlot slot;
    @Nullable private final Predicate<Tuple<Level, ItemStack>> itemCondition;
    @Nullable private final Consumer<Entity> increaseAction;
    @Nullable private final Consumer<Tuple<Level, Mutable<ItemStack>>> itemIncreaseAction;
    @Nullable private final Consumer<Entity> decreaseAction;
    @Nullable private final Consumer<Tuple<Level, Mutable<ItemStack>>> itemDecreaseAction;
    @Nullable private final Consumer<Entity> breakAction;
    @Nullable private final Consumer<Tuple<Level, Mutable<ItemStack>>> itemBreakAction;

    Set<Either<EquipmentSlot, Integer>> operatedStacks = new HashSet<>();

    public ActionOnDurabilityChangePower(PowerType<?> type, LivingEntity entity,
                                         @Nullable EquipmentSlot slot,
                                         @Nullable Predicate<Tuple<Level, ItemStack>> itemCondition,
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
        this.setTicking();
    }

    @Override
    public void tick() {
        operatedStacks.clear();
    }

    @Override
    public void onRemoved() {
        operatedStacks.clear();
    }

    public boolean doesApply(ItemStack stack) {
        return (slot == null || ItemStack.matches(entity.getItemBySlot(slot), stack)) && (this.itemCondition == null || this.itemCondition.test(new Tuple<>(entity.level(), stack)));
    }

    private void executeAction(ItemStack stack,
                               Consumer<Entity> entityAction,
                               Consumer<Tuple<Level, Mutable<ItemStack>>> itemAction) {
        Optional<EquipmentSlot> equipmentSlot = Optional.empty();
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (ItemStack.matches(stack, entity.getItemBySlot(slot))) {
                equipmentSlot = Optional.of(slot);
                break;
            }
        }
        Optional<EquipmentSlot> finalEquipmentSlot = equipmentSlot;

        Optional<Integer> playerInventoryIndex = Optional.empty();
        if (equipmentSlot.isEmpty() && entity instanceof Player player) {
            for (int i = 0; i < player.getInventory().items.size(); ++i) {
                if (ItemStack.matches(stack, player.getInventory().items.get(i))) {
                    playerInventoryIndex = Optional.of(i);
                    break;
                }
            }
        }
        Optional<Integer> finalPlayerInventoryIndex = playerInventoryIndex;

        if (equipmentSlot.isEmpty() && playerInventoryIndex.isEmpty() || finalEquipmentSlot.isPresent() && operatedStacks.stream().anyMatch(either -> either.left().isPresent() && either.left().get().equals(finalEquipmentSlot.get())) || entity instanceof Player && finalPlayerInventoryIndex.isPresent() && operatedStacks.stream().anyMatch(either -> either.right().isPresent() && either.right().get() == finalPlayerInventoryIndex.get().intValue())) return;

        equipmentSlot.ifPresent(slot1 -> operatedStacks.add(Either.left(slot1)));
        playerInventoryIndex.ifPresent(index -> operatedStacks.add(Either.right(index)));

        if(entityAction != null) {
            entityAction.accept(entity);
        }

        if(itemAction != null) {
            Mutable<ItemStack> mutable = new MutableObject<>(stack.copy());

            itemAction.accept(new Tuple<>(entity.level(), mutable));

            if (equipmentSlot.isPresent()) {
                entity.setItemSlot(equipmentSlot.get(), mutable.getValue());
            } else if (entity instanceof Player player) {
                player.getInventory().items.set(playerInventoryIndex.get(), mutable.getValue());
            }
        }
    }

    public void executeIncreaseAction(ItemStack stack) {
        executeAction(stack, increaseAction, itemIncreaseAction);
    }

    public void executeDecreaseAction(ItemStack stack) {
        executeAction(stack, decreaseAction, itemDecreaseAction);
    }

    public void executeBreakAction(ItemStack stack) {
        executeAction(stack, breakAction, itemBreakAction);
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
