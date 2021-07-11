package io.github.merchantpug.apugli.power;

import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.merchantpug.nibbles.ItemStackFoodComponentAPI;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.ItemStack;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

public class EdibleItemStackPower extends Power {
    private final Predicate<ItemStack> predicate;
    private final int hunger;
    private final float saturationModifier;
    private final boolean meat;
    private final boolean alwaysEdible;
    private final boolean snack;
    protected final List<StatusEffectInstance> effects = new LinkedList<>();
    private final int tickRate;

    public EdibleItemStackPower(PowerType<?> type, LivingEntity entity, Predicate<ItemStack> predicate, int hunger, float saturationModifier, boolean meat, boolean alwaysEdible, boolean snack, int tickRate) {
        super(type, entity);
        this.predicate = predicate;
        this.hunger = hunger;
        this.saturationModifier = saturationModifier;
        this.meat = meat;
        this.alwaysEdible = alwaysEdible;
        this.snack = snack;
        this.tickRate = tickRate;
        this.setTicking(true);
    }

    public EdibleItemStackPower addEffect(StatusEffect effect) {
        return addEffect(effect, 80);
    }

    public EdibleItemStackPower addEffect(StatusEffect effect, int lingerDuration) {
        return addEffect(effect, lingerDuration, 0);
    }

    public EdibleItemStackPower addEffect(StatusEffect effect, int lingerDuration, int amplifier) {
        return addEffect(new StatusEffectInstance(effect, lingerDuration, amplifier));
    }

    public EdibleItemStackPower addEffect(StatusEffectInstance instance) {
        effects.add(instance);
        return this;
    }

    private FoodComponent generatedFoodComponent() {
        FoodComponent.Builder builder = new FoodComponent.Builder();
        builder.hunger(this.hunger);
        builder.saturationModifier(this.saturationModifier);
        if (this.meat) {
            builder.meat();
        }
        if (this.alwaysEdible) {
            builder.alwaysEdible();
        }
        if (this.snack) {
            builder.snack();
        }
        if (!this.effects.isEmpty()) {
            this.effects.forEach(e -> {
                builder.statusEffect(e, 1.0F);
            });
        }
        return builder.build();
    }

    public void tempTick() {
        // This will be moved to the tick method as soon as Apace manages the powers on the client as well as the server
        if (entity.age % tickRate == 0) {
            ItemStack mainhandStack = entity.getEquippedStack(EquipmentSlot.MAINHAND);
            if (mainhandStack != ItemStack.EMPTY) {
                if (this.isActive()) {
                    if (this.predicate.test(mainhandStack)) {
                        ItemStackFoodComponentAPI.addFoodComponent(mainhandStack, this.generatedFoodComponent());
                    }
                } else {
                    if (this.predicate.test(mainhandStack)) {
                        ItemStackFoodComponentAPI.removeFoodComponent(mainhandStack);
                    }
                }
            }
            ItemStack offhandStack = entity.getEquippedStack(EquipmentSlot.OFFHAND);
            if (offhandStack != ItemStack.EMPTY) {
                if (this.isActive()) {
                    if (this.predicate.test(offhandStack)) {
                        ItemStackFoodComponentAPI.addFoodComponent(offhandStack, this.generatedFoodComponent());
                    }
                } else {
                    if (this.predicate.test(offhandStack)) {
                        ItemStackFoodComponentAPI.removeFoodComponent(offhandStack);
                    }
                }
            }
        }
    }


    // This will changed to the onRemoved method as soon as Apace manages the powers on the client as well as the server
    /* public void tempOnRemoved() {
        if (entity instanceof PlayerEntity) {
            for (int i = 0; i < ((PlayerEntityAccessor) entity).getInventory().main.size(); i++) {
                ItemStack itemStack = ((PlayerEntityAccessor) entity).getInventory().main.get(i);
                if (predicate.test(itemStack)) {
                    ItemStackFoodComponentAPI.removeFoodComponent(itemStack);
                }
            }
            for (int i = 0; i < ((PlayerEntityAccessor) entity).getInventory().armor.size(); i++) {
                ItemStack armorStack = ((PlayerEntityAccessor) entity).getInventory().getArmorStack(i);
                if (predicate.test(armorStack)) {
                    ItemStackFoodComponentAPI.removeFoodComponent(armorStack);
                }
            }
            ItemStack offHandStack = entity.getEquippedStack(EquipmentSlot.OFFHAND);
            if (predicate.test(offHandStack)) {
                ItemStackFoodComponentAPI.removeFoodComponent(offHandStack);
            }
        }
    } */
}
