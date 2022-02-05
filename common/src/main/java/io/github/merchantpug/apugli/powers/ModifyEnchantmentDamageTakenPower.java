package io.github.merchantpug.apugli.powers;

import io.github.apace100.origins.power.PowerType;
import io.github.apace100.origins.power.ValueModifyingPower;
import io.github.apace100.origins.power.factory.PowerFactory;
import io.github.apace100.origins.power.factory.condition.ConditionFactory;
import io.github.apace100.origins.util.SerializableData;
import io.github.apace100.origins.util.SerializableDataType;
import io.github.merchantpug.apugli.Apugli;
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

public class ModifyEnchantmentDamageTakenPower extends ValueModifyingPower {
    public final Enchantment enchantment;
    public final float baseValue;
    private final Predicate<Pair<DamageSource, Float>> damageCondition;
    private final Consumer<Entity> attackerAction;
    private final Consumer<Entity> selfAction;

    public ModifyEnchantmentDamageTakenPower(PowerType<?> type, PlayerEntity player, Enchantment enchantment, float baseValue, Predicate<Pair<DamageSource, Float>> damageCondition, Consumer<Entity> attackerAction, Consumer<Entity> selfAction) {
        super(type, player);
        this.enchantment = enchantment;
        this.baseValue = baseValue;
        this.damageCondition = damageCondition;
        this.attackerAction = attackerAction;
        this.selfAction = selfAction;
    }

    public boolean doesApply(DamageSource source, float damageAmount) {
        return source.getAttacker() != null && source.getAttacker() instanceof LivingEntity && damageCondition.test(new Pair(source, damageAmount)) && EnchantmentHelper.getLevel(enchantment, ((LivingEntity)source.getAttacker()).getEquippedStack(EquipmentSlot.MAINHAND)) != 0;
    }

    public void executeActions(Entity attacker) {
        if (selfAction != null) {
            selfAction.accept(player);
        }
        if (attackerAction != null) {
            attackerAction.accept(attacker);
        }
    }


    public static PowerFactory<?> getFactory() {
        return new PowerFactory<>(Apugli.identifier("modify_enchantment_damage_taken"),
                new SerializableData()
                        .add("enchantment", SerializableDataType.ENCHANTMENT)
                        .add("base_value", SerializableDataType.FLOAT)
                        .add("damage_condition", SerializableDataType.DAMAGE_CONDITION, null)
                        .add("modifier", SerializableDataType.ATTRIBUTE_MODIFIER, null)
                        .add("modifiers", SerializableDataType.ATTRIBUTE_MODIFIERS, null)
                        .add("self_action", SerializableDataType.ENTITY_ACTION, null)
                        .add("attacker_action", SerializableDataType.ENTITY_ACTION, null),
                data ->
                        (type, player) -> {
                            ModifyEnchantmentDamageTakenPower power = new ModifyEnchantmentDamageTakenPower(type, player,
                                    data.get("enchantment"),
                                    data.getFloat("base_value"),
                                    data.isPresent("damage_condition") ? (ConditionFactory<Pair<DamageSource, Float>>.Instance)data.get("damage_condition") : dmg -> true,
                                    data.get("attacker_action"),
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
