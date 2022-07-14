package com.github.merchantpug.apugli.powers;

import io.github.apace100.origins.power.PowerType;
import io.github.apace100.origins.power.ValueModifyingPower;
import io.github.apace100.origins.power.factory.PowerFactory;
import io.github.apace100.origins.power.factory.condition.ConditionFactory;
import io.github.apace100.origins.util.SerializableData;
import io.github.apace100.origins.util.SerializableDataType;
import com.github.merchantpug.apugli.Apugli;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.player.PlayerEntity;

import java.util.List;
import java.util.function.Predicate;

public class ModifyBreedingCooldownPower extends ValueModifyingPower {
    private Predicate<LivingEntity> targetCondition;

    public static PowerFactory<?> getFactory() {
        return new PowerFactory<ModifyBreedingCooldownPower>(Apugli.identifier("modify_breeding_cooldown"),
                new SerializableData()
                        .add("modifier", SerializableDataType.ATTRIBUTE_MODIFIER, null)
                        .add("modifiers", SerializableDataType.ATTRIBUTE_MODIFIERS, null)
                        .add("target_condition", SerializableDataType.ENTITY_CONDITION, null),
                data ->
                        (type, player) -> {
                            ModifyBreedingCooldownPower power = new ModifyBreedingCooldownPower(type, player, (ConditionFactory<LivingEntity>.Instance)data.get("target_condition"));
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

    public boolean doesApply(LivingEntity mobEntity) {
        Apugli.LOGGER.info(targetCondition == null || this.targetCondition.test(mobEntity));
        return this.targetCondition == null || this.targetCondition.test(mobEntity);
    }

    public ModifyBreedingCooldownPower(PowerType<?> type, PlayerEntity player, Predicate<LivingEntity> targetCondition) {
        super(type, player);
        this.targetCondition = targetCondition;
    }
}
