package net.merchantpug.apugli.power;

<<<<<<<< HEAD:src/main/java/net/merchantpug/apugli/power/ModifyEnchantmentDamageTakenPower.java
import net.merchantpug.apugli.Apugli;
========
import the.great.migration.merchantpug.apugli.Apugli;
>>>>>>>> pr/25:Common/src/main/java/com/github/merchantpug/apugli/power/ModifyEnchantmentDamageTakenPower.java
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.ValueModifyingPower;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.apoli.util.modifier.Modifier;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
<<<<<<<< HEAD:src/main/java/net/merchantpug/apugli/power/ModifyEnchantmentDamageTakenPower.java
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.util.Pair;

========
>>>>>>>> pr/25:Common/src/main/java/com/github/merchantpug/apugli/power/ModifyEnchantmentDamageTakenPower.java
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.util.Tuple;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

public class ModifyEnchantmentDamageTakenPower extends ValueModifyingPower {
    public final Enchantment enchantment;
    public final float baseValue;
    private final Predicate<Tuple<DamageSource, Float>> damageCondition;
    private final Predicate<Tuple<Entity, Entity>> biEntityCondition;
    private Consumer<Tuple<Entity, Entity>> biEntityAction;

    public ModifyEnchantmentDamageTakenPower(PowerType<?> type, LivingEntity entity, Enchantment enchantment, float baseValue, Predicate<Tuple<DamageSource, Float>> damageCondition, Predicate<Tuple<Entity, Entity>> biEntityCondition) {
        super(type, entity);
        this.enchantment = enchantment;
        this.baseValue = baseValue;
        this.damageCondition = damageCondition;
        this.biEntityCondition = biEntityCondition;
    }

    public boolean doesApply(DamageSource source, float damageAmount) {
        return source.getEntity() != null && source.getEntity() instanceof LivingEntity && damageCondition.test(new Tuple(source, damageAmount)) && (biEntityCondition == null || biEntityCondition.test(new Tuple<>(source.getEntity(), entity))) && EnchantmentHelper.getItemEnchantmentLevel(enchantment, ((LivingEntity)source.getEntity()).getItemBySlot(EquipmentSlot.MAINHAND)) != 0;
    }

    public void setBiEntityAction(Consumer<Tuple<Entity, Entity>> biEntityAction) {
        this.biEntityAction = biEntityAction;
    }

    public void executeActions(Entity attacker) {
        if(biEntityAction != null) {
            biEntityAction.accept(new Tuple<>(attacker, entity));
        }
    }


    public static PowerFactory<?> getFactory() {
        return new PowerFactory<>(Apugli.identifier("modify_enchantment_damage_taken"),
                new SerializableData()
                        .add("enchantment", SerializableDataTypes.ENCHANTMENT)
                        .add("base_value", SerializableDataTypes.FLOAT)
                        .add("damage_condition", Services.CONDITION.damageDataType(), null)
                        .add("bientity_condition", Services.CONDITION.biEntityDataType(), null)
                        .add("modifier", Modifier.DATA_TYPE, null)
                        .add("modifiers", Modifier.LIST_TYPE, null)
                        .add("bientity_action", Services.ACTION.biEntityDataType(), null)
                        .add("self_action", Services.ACTION.entityDataType(), null)
                        .add("attacker_action", Services.ACTION.entityDataType(), null),
                data ->
                        (type, player) -> {
                            ModifyEnchantmentDamageTakenPower power = new ModifyEnchantmentDamageTakenPower(type, player,
                                    data.get("enchantment"),
                                    data.getFloat("base_value"),
                                    data.isPresent("damage_condition") ? (ConditionFactory<Tuple<DamageSource, Float>>.Instance)data.get("damage_condition") : dmg -> true,
                                    (ConditionFactory<Tuple<Entity, Entity>>.Instance)data.get("bientity_condition"));
                            if(data.isPresent("modifier")) {
                                power.addModifier(data.get("modifier"));
                            }
                            if(data.isPresent("modifiers")) {
                                ((List<Modifier>)data.get("modifiers")).forEach(power::addModifier);
                            }
                            if(data.isPresent("bientity_action")) {
                                power.setBiEntityAction((ActionFactory<Tuple<Entity, Entity>>.Instance)data.get("bientity_action"));
                            }
                            return power;
                        })
                .allowCondition();
    }
}
