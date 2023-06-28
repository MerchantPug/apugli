package net.merchantpug.apugli.power;

import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataType;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.merchantpug.apugli.platform.Services;
import net.merchantpug.apugli.power.factory.SimplePowerFactory;
import net.merchantpug.apugli.registry.power.ApugliPowers;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.mutable.Mutable;
import org.apache.commons.lang3.mutable.MutableObject;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class EdibleItemPower extends Power {
    private final Predicate<Tuple<Level, ItemStack>> predicate;
    private final FoodProperties foodComponent;
    private final EatAnimation useAction;
    private final ItemStack returnStack;
    private final SoundEvent sound;
    private final Consumer<Entity> entityActionWhenEaten;
    private final Consumer<Tuple<Level, Mutable<ItemStack>>> itemActionWhenEaten;

    public EdibleItemPower(PowerType<?> type, LivingEntity entity,
                           Predicate<Tuple<Level, ItemStack>> predicate,
                           FoodProperties foodComponent,
                           EatAnimation useAction,
                           ItemStack returnStack,
                           SoundEvent sound,
                           Consumer<Entity> entityActionWhenEaten,
                           Consumer<Tuple<Level, Mutable<ItemStack>>> itemActionWhenEaten) {
        super(type, entity);
        this.predicate = predicate;
        this.foodComponent = foodComponent;
        this.useAction = useAction;
        this.returnStack = returnStack;
        this.sound = sound;
        this.entityActionWhenEaten = entityActionWhenEaten;
        this.itemActionWhenEaten = itemActionWhenEaten;
    }

    public boolean doesApply(Level level, ItemStack stack) {
        return this.predicate.test(new Tuple<>(level, stack));
    }

    public FoodProperties getFoodComponent() {
        return foodComponent;
    }

    public EatAnimation getUseAction() {
        return useAction;
    }

    public ItemStack getReturnStack() {
        return returnStack;
    }

    public SoundEvent getSound() {
        return sound;
    }

    public static void executeEntityActions(LivingEntity entity, ItemStack stack) {
        Services.POWER.getPowers(entity, ApugliPowers.EDIBLE_ITEM.get()).stream().filter(p -> p.doesApply(entity.level, stack) && p.entityActionWhenEaten != null).forEach(p -> p.entityActionWhenEaten.accept(entity));
    }

    public static ItemStack executeItemActions(LivingEntity entity, ItemStack stack, ItemStack originalStack) {
        Mutable<ItemStack> mutable = new MutableObject<>(stack.copy());
        Services.POWER.getPowers(entity, ApugliPowers.EDIBLE_ITEM.get()).stream().filter(p -> p.doesApply(entity.level, originalStack) && p.itemActionWhenEaten != null).forEach(p -> p.itemActionWhenEaten.accept(new Tuple<>(entity.level, mutable)));
        return mutable.getValue();
    }

    public static class Factory extends SimplePowerFactory<EdibleItemPower> {

        public Factory() {
            super("edible_item",
                    new SerializableData()
                            .add("item_condition", Services.CONDITION.itemDataType())
                            .add("food_component", SerializableDataTypes.FOOD_COMPONENT)
                            .add("use_action", SerializableDataType.enumValue(EatAnimation.class), EatAnimation.EAT)
                            .add("return_stack", SerializableDataTypes.ITEM_STACK, null)
                            .add("sound", SerializableDataTypes.SOUND_EVENT, null)
                            .add("entity_action", Services.ACTION.entityDataType(), null)
                            .add("item_action", Services.ACTION.itemDataType(), null),
                    data -> (type, player) -> new EdibleItemPower(type, player,
                            Services.CONDITION.itemPredicate(data, "item_condition"),
                            data.get("food_component"),
                            data.get("use_action"),
                            data.get("return_stack"),
                            data.get("sound"),
                            Services.ACTION.entityConsumer(data, "entity_action"),
                            Services.ACTION.itemConsumer(data, "item_action")));
            allowCondition();
        }

        @Override
        public Class<EdibleItemPower> getPowerClass() {
            return EdibleItemPower.class;
        }

    }

    public enum EatAnimation {
        EAT, DRINK
    }

}
