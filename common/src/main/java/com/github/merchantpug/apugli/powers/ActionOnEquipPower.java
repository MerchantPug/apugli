package com.github.merchantpug.apugli.powers;

import io.github.apace100.origins.power.Power;
import io.github.apace100.origins.power.PowerType;
import io.github.apace100.origins.power.factory.PowerFactory;
import io.github.apace100.origins.util.SerializableData;
import io.github.apace100.origins.util.SerializableDataType;
import com.github.merchantpug.apugli.Apugli;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

import java.util.HashMap;
import java.util.function.Consumer;
import java.util.function.Predicate;


public class ActionOnEquipPower extends Power {
    private final HashMap<EquipmentSlot, Predicate<ItemStack>> armorConditions;
    private final Consumer<Entity> armorAction;

    public ActionOnEquipPower(PowerType<?> type, PlayerEntity player, HashMap<EquipmentSlot, Predicate<ItemStack>> armorConditions, Consumer<Entity> entityAction) {
        super(type, player);
        this.armorConditions = armorConditions;
        this.armorAction = entityAction;
    }

    public void fireAction(EquipmentSlot slot, ItemStack stack) {
        if (armorConditions == null) {
            armorAction.accept(this.player);
            return;
        }
        if (armorConditions.get(slot) == null) return;

        if (armorConditions.get(slot).test(stack)) {
            armorAction.accept(this.player);
        }
    }

    public static PowerFactory<?> getFactory() {
        return new PowerFactory<ActionOnEquipPower>(
                Apugli.identifier("action_on_equip"),
                new SerializableData()
                        .add("head", SerializableDataType.ITEM_CONDITION, null)
                        .add("chest", SerializableDataType.ITEM_CONDITION, null)
                        .add("legs", SerializableDataType.ITEM_CONDITION, null)
                        .add("feet", SerializableDataType.ITEM_CONDITION, null)
                        .add("offhand", SerializableDataType.ITEM_CONDITION, null)
                        .add("action", SerializableDataType.ENTITY_ACTION),
                data ->
                        (type, player) -> {
                    HashMap<EquipmentSlot, Predicate<ItemStack>> conditions = new HashMap<>();

                    if(data.isPresent("head")) {
                        conditions.put(EquipmentSlot.HEAD, (Predicate<ItemStack>) data.get("head"));
                    }
                    if(data.isPresent("chest")) {
                        conditions.put(EquipmentSlot.CHEST, (Predicate<ItemStack>) data.get("chest"));
                    }
                    if(data.isPresent("legs")) {
                        conditions.put(EquipmentSlot.LEGS, (Predicate<ItemStack>) data.get("legs"));
                    }
                    if(data.isPresent("feet")) {
                        conditions.put(EquipmentSlot.FEET, (Predicate<ItemStack>) data.get("feet"));
                    }
                    if(data.isPresent("offhand")) {
                        conditions.put(EquipmentSlot.OFFHAND, (Predicate<ItemStack>) data.get("offhand"));
                    }

                    return new ActionOnEquipPower(type, player, conditions, (Consumer<Entity>) data.get("action"));
                }
        ).allowCondition();
    }
}