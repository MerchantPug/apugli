package net.merchantpug.apugli.power;

import net.merchantpug.apugli.Apugli;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;

import java.util.HashSet;
import java.util.List;

public class InstantEffectImmunityPower extends Power {

    protected final HashSet<StatusEffect> effects = new HashSet<>();
    private final boolean inverted;

    public static PowerFactory getFactory() {
        return new PowerFactory<>(Apugli.identifier("instant_effect_immunity"),
                new SerializableData()
                        .add("effect", SerializableDataTypes.STATUS_EFFECT, null)
                        .add("effects", SerializableDataTypes.STATUS_EFFECTS, null)
                        .add("inverted", SerializableDataTypes.BOOLEAN, false),
                data ->
                        (type, player) -> {
                            InstantEffectImmunityPower power = new InstantEffectImmunityPower(type, player, data.get("inverted"));
                            if(data.isPresent("effect")) {
                                power.addEffect(data.get("effect"));
                            }
                            if(data.isPresent("effects")) {
                                ((List<StatusEffect>)data.get("effects")).forEach(power::addEffect);
                            }
                            return power;
                        })
                .allowCondition();
    }

    public InstantEffectImmunityPower(PowerType<?> type, LivingEntity entity, boolean inverted) {
        super(type, entity);
        this.inverted = inverted;
    }

    public InstantEffectImmunityPower addEffect(StatusEffect effect) {
        effects.add(effect);
        return this;
    }

    public boolean doesApply(StatusEffectInstance instance) {
        return doesApply(instance.getEffectType());
    }

    public boolean doesApply(StatusEffect effect) {
        return inverted ^ effects.contains(effect);
    }
}
