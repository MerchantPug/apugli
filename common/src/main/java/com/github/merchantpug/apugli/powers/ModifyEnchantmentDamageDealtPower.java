package com.github.merchantpug.apugli.powers;

import io.github.apace100.origins.power.PowerType;
import io.github.apace100.origins.power.ValueModifyingPower;
import io.github.apace100.origins.power.factory.PowerFactory;
import io.github.apace100.origins.power.factory.condition.ConditionFactory;
import io.github.apace100.origins.util.SerializableData;
import io.github.apace100.origins.util.SerializableDataType;
import com.github.merchantpug.apugli.Apugli;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Pair;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class ModifyEnchantmentDamageDealtPower extends ValueModifyingPower {
    public final Enchantment enchantment;
    public final float baseValue;
    private final Predicate<Pair<DamageSource, Float>> damageCondition;
    private final Predicate<LivingEntity> targetCondition;
    private final Consumer<Entity> targetAction;
    private final Consumer<Entity> selfAction;

    public ModifyEnchantmentDamageDealtPower(PowerType<?> type, PlayerEntity player, Enchantment enchantment, float baseValue, Predicate<Pair<DamageSource, Float>> damageCondition, Predicate<LivingEntity> targetCondition, Consumer<Entity> targetAction, Consumer<Entity> selfAction) {
        super(type, player);
        this.enchantment = enchantment;
        this.baseValue = baseValue;
        this.damageCondition = damageCondition;
        this.targetCondition = targetCondition;
        this.targetAction = targetAction;
        this.selfAction = selfAction;
    }

    public boolean doesApply(DamageSource source, float damageAmount, LivingEntity target) {
        return damageCondition.test(new Pair(source, damageAmount)) && targetCondition.test(target) && EnchantmentHelper.getLevel(enchantment, player.getEquippedStack(EquipmentSlot.MAINHAND)) != 0;
    }

    public void executeActions(Entity target) {
        if (selfAction != null) {
            selfAction.accept(player);
        }
        if (targetAction != null) {
            targetAction.accept(target);
        }
    }


    public static PowerFactory<?> getFactory() {
        return new PowerFactory<>(Apugli.identifier("modify_enchantment_damage_dealt"),
                new SerializableData()
                        .add("enchantment", SerializableDataType.ENCHANTMENT)
                        .add("base_value", SerializableDataType.FLOAT)
                        .add("damage_condition", SerializableDataType.DAMAGE_CONDITION, null)
                        .add("target_condition", SerializableDataType.ENTITY_CONDITION, null)
                        .add("modifier", SerializableDataType.ATTRIBUTE_MODIFIER, null)
                        .add("modifiers", SerializableDataType.ATTRIBUTE_MODIFIERS, null)
                        .add("self_action", SerializableDataType.ENTITY_ACTION, null)
                        .add("target_action", SerializableDataType.ENTITY_ACTION, null),
                data ->
                        (type, player) -> {
                            ModifyEnchantmentDamageDealtPower power = new ModifyEnchantmentDamageDealtPower(type, player,
                                    data.get("enchantment"),
                                    data.getFloat("base_value"),
                                    data.isPresent("damage_condition") ? (ConditionFactory<Pair<DamageSource, Float>>.Instance)data.get("damage_condition") : dmg -> true,
                                    data.isPresent("target_condition") ? data.get("target_condition") : target -> true,
                                    data.get("target_action"),
                                    data.get("self_action"));
                            if(data.isPresent("modifier")) {
                                power.addModifier(data.getModifier("modifier"));
                            }
                            if(data.isPresent("modifiers")) {
                                ((List<EntityAttributeModifier>)data.get("modifiers")).forEach(power::addModifier);
                            }
                            return power;
                        })
                .allowCondition();
    }
}
