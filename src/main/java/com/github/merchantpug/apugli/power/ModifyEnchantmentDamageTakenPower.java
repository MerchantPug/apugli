package com.github.merchantpug.apugli.power;

import com.github.merchantpug.apugli.Apugli;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.ValueModifyingPower;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.apoli.util.modifier.Modifier;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.util.Pair;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class ModifyEnchantmentDamageTakenPower extends ValueModifyingPower {
    public final Enchantment enchantment;
    public final float baseValue;
    private final Predicate<Pair<DamageSource, Float>> damageCondition;
    private final Predicate<Pair<Entity, Entity>> biEntityCondition;
    private Consumer<Pair<Entity, Entity>> biEntityAction;

    public ModifyEnchantmentDamageTakenPower(PowerType<?> type, LivingEntity entity, Enchantment enchantment, float baseValue, Predicate<Pair<DamageSource, Float>> damageCondition, Predicate<Pair<Entity, Entity>> biEntityCondition) {
        super(type, entity);
        this.enchantment = enchantment;
        this.baseValue = baseValue;
        this.damageCondition = damageCondition;
        this.biEntityCondition = biEntityCondition;
    }

    public boolean doesApply(DamageSource source, float damageAmount) {
        return source.getAttacker() != null && source.getAttacker() instanceof LivingEntity && damageCondition.test(new Pair(source, damageAmount)) && (biEntityCondition == null || biEntityCondition.test(new Pair<>(source.getAttacker(), entity))) && EnchantmentHelper.getLevel(enchantment, ((LivingEntity)source.getAttacker()).getEquippedStack(EquipmentSlot.MAINHAND)) != 0;
    }

    public void setBiEntityAction(Consumer<Pair<Entity, Entity>> biEntityAction) {
        this.biEntityAction = biEntityAction;
    }

    public void executeActions(Entity attacker) {
        if (biEntityAction != null) {
            biEntityAction.accept(new Pair<>(attacker, entity));
        }
    }


    public static PowerFactory<?> getFactory() {
        return new PowerFactory<>(Apugli.identifier("modify_enchantment_damage_taken"),
                new SerializableData()
                        .add("enchantment", SerializableDataTypes.ENCHANTMENT)
                        .add("base_value", SerializableDataTypes.FLOAT)
                        .add("damage_condition", ApoliDataTypes.DAMAGE_CONDITION, null)
                        .add("bientity_condition", ApoliDataTypes.BIENTITY_CONDITION, null)
                        .add("modifier", Modifier.DATA_TYPE, null)
                        .add("modifiers", Modifier.LIST_TYPE, null)
                        .add("bientity_action", ApoliDataTypes.BIENTITY_ACTION, null)
                        .add("self_action", ApoliDataTypes.ENTITY_ACTION, null)
                        .add("attacker_action", ApoliDataTypes.ENTITY_ACTION, null),
                data ->
                        (type, player) -> {
                            ModifyEnchantmentDamageTakenPower power = new ModifyEnchantmentDamageTakenPower(type, player,
                                    data.get("enchantment"),
                                    data.getFloat("base_value"),
                                    data.isPresent("damage_condition") ? (ConditionFactory<Pair<DamageSource, Float>>.Instance)data.get("damage_condition") : dmg -> true,
                                    (ConditionFactory<Pair<Entity, Entity>>.Instance)data.get("bientity_condition"));
                            if(data.isPresent("modifier")) {
                                power.addModifier(data.get("modifier"));
                            }
                            if(data.isPresent("modifiers")) {
                                ((List<Modifier>)data.get("modifiers")).forEach(power::addModifier);
                            }
                            if(data.isPresent("bientity_action")) {
                                power.setBiEntityAction((ActionFactory<Pair<Entity, Entity>>.Instance)data.get("bientity_action"));
                            }
                            return power;
                        })
                .allowCondition();
    }
}
