package net.merchantpug.apugli.power;

import net.merchantpug.apugli.platform.Services;
import net.merchantpug.apugli.power.factory.SimplePowerFactory;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.calio.data.SerializableData;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;
import java.util.function.Consumer;
import java.util.function.Predicate;


public class ActionOnEquipPower extends Power {
    private final EnumMap<EquipmentSlot, Predicate<Tuple<Level, ItemStack>>> armorConditions;
    private final Consumer<Entity> entityAction;

    public ActionOnEquipPower(PowerType<?> type, LivingEntity entity,
                              EnumMap<EquipmentSlot, Predicate<Tuple<Level, ItemStack>>> armorConditions,
                              Consumer<Entity> entityAction) {
        super(type, entity);
        this.armorConditions = armorConditions;
        this.entityAction = entityAction;
    }

    public void executeAction(EquipmentSlot slot, ItemStack stack) {
        if(!armorConditions.containsKey(slot) || armorConditions.get(slot).test(new Tuple<>(entity.level(), stack))) {
            entityAction.accept(this.entity);
        }
    }
    
    public static class Factory extends SimplePowerFactory<ActionOnEquipPower> {
    
        public Factory() {
            super("action_on_equip",
                new SerializableData()
                    .add("head", Services.CONDITION.itemDataType(), null)
                    .add("chest", Services.CONDITION.itemDataType(), null)
                    .add("legs", Services.CONDITION.itemDataType(), null)
                    .add("feet", Services.CONDITION.itemDataType(), null)
                    .add("offhand", Services.CONDITION.itemDataType(), null)
                    .add("action", Services.ACTION.entityDataType()),
                data -> (type, player) -> {
                    EnumMap<EquipmentSlot, Predicate<Tuple<Level, ItemStack>>> conditions = new EnumMap<>(EquipmentSlot.class);
                    if(data.isPresent("head")) {
                        conditions.put(EquipmentSlot.HEAD, Services.CONDITION.itemPredicate(data, "head"));
                    }
                    if(data.isPresent("chest")) {
                        conditions.put(EquipmentSlot.CHEST, Services.CONDITION.itemPredicate(data, "chest"));
                    }
                    if(data.isPresent("legs")) {
                        conditions.put(EquipmentSlot.LEGS, Services.CONDITION.itemPredicate(data, "legs"));
                    }
                    if(data.isPresent("feet")) {
                        conditions.put(EquipmentSlot.FEET, Services.CONDITION.itemPredicate(data, "feet"));
                    }
                    if(data.isPresent("offhand")) {
                        conditions.put(EquipmentSlot.OFFHAND, Services.CONDITION.itemPredicate(data, "offhand"));
                    }
                    return new ActionOnEquipPower(type, player, conditions, Services.ACTION.entityConsumer(data, "action"));
                });
            allowCondition();
        }
        
        @Override
        @NotNull
        public Class<ActionOnEquipPower> getPowerClass() {
            return ActionOnEquipPower.class;
        }
        
    }

}