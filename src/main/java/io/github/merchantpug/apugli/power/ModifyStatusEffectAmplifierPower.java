package io.github.merchantpug.apugli.power;

import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.ValueModifyingPower;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.merchantpug.apugli.Apugli;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.effect.StatusEffect;

public class ModifyStatusEffectAmplifierPower extends ValueModifyingPower {
    private final StatusEffect statusEffect;

    public ModifyStatusEffectAmplifierPower(PowerType<?> type, LivingEntity entity, StatusEffect statusEffect) {
        super(type, entity);
        this.statusEffect = statusEffect;
    }

    public boolean doesApply(StatusEffect statusEffect) {
        return statusEffect.equals(this.statusEffect);
    }

    public static PowerFactory<?> getFactory() {
        return new PowerFactory<ModifyStatusEffectAmplifierPower>(
                Apugli.identifier("modify_status_effect_amplifier"),
                new SerializableData()
                        .add("status_effect", SerializableDataTypes.STATUS_EFFECT)
                        .add("modifier", SerializableDataTypes.ATTRIBUTE_MODIFIER),
                data -> (type, player) -> {
                    ModifyStatusEffectAmplifierPower modifyStatusEffectAmplifierPower = new ModifyStatusEffectAmplifierPower(type, player, (StatusEffect) data.get("status_effect"));
                    modifyStatusEffectAmplifierPower.addModifier((EntityAttributeModifier) data.get("modifier"));
                    return modifyStatusEffectAmplifierPower;
                })
                .allowCondition();
    }
}