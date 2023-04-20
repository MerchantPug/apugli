package net.merchantpug.apugli.power;

import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.merchantpug.apugli.platform.Services;
import net.merchantpug.apugli.power.factory.SimplePowerFactory;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class EdibleItemPower extends Power {
    public final Predicate<ItemStack> predicate;
    public final FoodProperties foodComponent;
    public final UseAnim useAction;
    public final ItemStack returnStack;
    public final SoundEvent sound;
    private final Consumer<Entity> entityActionWhenEaten;

    public EdibleItemPower(PowerType<?> type, LivingEntity entity, Predicate<ItemStack> predicate, FoodProperties foodComponent, UseAnim useAction, ItemStack returnStack, SoundEvent sound, Consumer<Entity> entityActionWhenEaten) {
        super(type, entity);
        this.predicate = predicate;
        this.foodComponent = foodComponent;
        this.useAction = useAction;
        this.returnStack = returnStack;
        this.sound = sound;
        this.entityActionWhenEaten = entityActionWhenEaten;
    }

    public boolean doesApply(ItemStack stack) {
        return this.predicate.test(stack);
    }

    public void eat() {
        if(entityActionWhenEaten != null) {
            entityActionWhenEaten.accept(entity);
        }
    }

    public static class Factory extends SimplePowerFactory<EdibleItemPower> {

        public Factory() {
            super("edible_item",
                    new SerializableData()
                            .add("item_condition", Services.CONDITION.itemDataType())
                            .add("food_component", SerializableDataTypes.FOOD_COMPONENT)
                            .add("use_action", SerializableDataTypes.USE_ACTION, null)
                            .add("return_stack", SerializableDataTypes.ITEM_STACK, null)
                            .add("sound", SerializableDataTypes.SOUND_EVENT, null)
                            .add("entity_action", Services.ACTION.entityDataType(), null),
                    data -> (type, player) -> new EdibleItemPower(type, player,
                            Services.CONDITION.itemPredicate(data, "item_condition"),
                            data.get("food_component"),
                            data.get("use_action"),
                            data.get("return_stack"),
                            data.get("sound"),
                            Services.ACTION.entityConsumer(data, "entity_action")));
            allowCondition();
        }

        @Override
        public Class<EdibleItemPower> getPowerClass() {
            return EdibleItemPower.class;
        }

    }
}
