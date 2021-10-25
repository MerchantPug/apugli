package io.github.merchantpug.apugli.power;

import io.github.apace100.apoli.Apoli;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.merchantpug.apugli.Apugli;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;

import java.util.HashSet;
import java.util.List;

public class EffectWhitelistPower extends Power {

    protected final HashSet<StatusEffect> effects = new HashSet<>();

    public static PowerFactory<?> getFactory() {
        return new PowerFactory<>(Apugli.identifier("effect_whitelist"),
                new SerializableData()
                        .add("effect", SerializableDataTypes.STATUS_EFFECT, null)
                        .add("effects", SerializableDataTypes.STATUS_EFFECTS, null),
                data ->
                        (type, player) -> {
                            EffectWhitelistPower power = new EffectWhitelistPower(type, player);
                            if(data.isPresent("effect")) {
                                power.addEffect((StatusEffect)data.get("effect"));
                            }
                            if(data.isPresent("effects")) {
                                ((List<StatusEffect>)data.get("effects")).forEach(power::addEffect);
                            }
                            return power;
                        })
                .allowCondition();
    }

    public EffectWhitelistPower(PowerType<?> type, LivingEntity entity) {
        super(type, entity);
    }
    public EffectWhitelistPower(PowerType<?> type, LivingEntity entity, StatusEffect effect) {
        super(type, entity);
        addEffect(effect);
    }

    public EffectWhitelistPower addEffect(StatusEffect effect) {
        effects.add(effect);
        return this;
    }

    public boolean doesApply(StatusEffectInstance instance) {
        return doesApply(instance.getEffectType());
    }

    public boolean doesApply(StatusEffect effect) {
        return effects.contains(effect);
    }
}
