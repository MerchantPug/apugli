package net.merchantpug.apugli.power;

import net.merchantpug.apugli.Apugli;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.UseAction;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class EdibleItemPower extends Power {
    public final Predicate<ItemStack> predicate;
    public final FoodComponent foodComponent;
    public final UseAction useAction;
    public final ItemStack returnStack;
    public final SoundEvent sound;
    private final Consumer<Entity> entityActionWhenEaten;

    public static PowerFactory<?> getFactory() {
        return new PowerFactory<EdibleItemPower>(Apugli.identifier("edible_item"),
                new SerializableData()
                        .add("item_condition", ApoliDataTypes.ITEM_CONDITION)
                        .add("food_component", SerializableDataTypes.FOOD_COMPONENT)
                        .add("use_action", SerializableDataTypes.USE_ACTION, null)
                        .add("return_stack", SerializableDataTypes.ITEM_STACK, null)
                        .add("sound", SerializableDataTypes.SOUND_EVENT, null)
                        .add("entity_action", ApoliDataTypes.ENTITY_ACTION, null),
                data ->
                        (type, player) -> new EdibleItemPower(type, player,
                                    data.get("item_condition"),
                                    data.get("food_component"),
                                    data.get("use_action"),
                                    data.get("return_stack"),
                                    data.get("sound"),
                                    data.get("entity_action")))
                .allowCondition();
    }

    public EdibleItemPower(PowerType<?> type, LivingEntity entity, Predicate<ItemStack> predicate, FoodComponent foodComponent, UseAction useAction, ItemStack returnStack, SoundEvent sound, Consumer<Entity> entityActionWhenEaten) {
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
}
