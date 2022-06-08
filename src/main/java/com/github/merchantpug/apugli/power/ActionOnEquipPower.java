package com.github.merchantpug.apugli.power;

import com.github.merchantpug.apugli.Apugli;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

import java.util.HashMap;
import java.util.function.Consumer;
import java.util.function.Predicate;


public class ActionOnEquipPower extends Power {
    private final HashMap<EquipmentSlot, Predicate<ItemStack>> armorConditions;
    private final Consumer<Entity> armorAction;

    public ActionOnEquipPower(PowerType<?> type, LivingEntity entity, HashMap<EquipmentSlot, Predicate<ItemStack>> armorConditions, Consumer<Entity> entityAction) {
        super(type, entity);
        this.armorConditions = armorConditions;
        this.armorAction = entityAction;
    }

    public void fireAction(EquipmentSlot slot, ItemStack stack) {
        if (armorConditions == null) {
            armorAction.accept(this.entity);
            return;
        }
        if (armorConditions.get(slot) == null) return;

        if (armorConditions.get(slot).test(stack)) {
            armorAction.accept(this.entity);
        }
    }

    public static PowerFactory<?> getFactory() {
        return new PowerFactory<ActionOnEquipPower>(
                Apugli.identifier("action_on_equip"),
                new SerializableData()
                        .add("head", ApoliDataTypes.ITEM_CONDITION, null)
                        .add("chest", ApoliDataTypes.ITEM_CONDITION, null)
                        .add("legs", ApoliDataTypes.ITEM_CONDITION, null)
                        .add("feet", ApoliDataTypes.ITEM_CONDITION, null)
                        .add("offhand", ApoliDataTypes.ITEM_CONDITION, null)
                        .add("action", ApoliDataTypes.ENTITY_ACTION),
                data -> (type, player) -> {
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