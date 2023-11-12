package net.merchantpug.apugli.power;

import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import java.util.HashSet;
import java.util.List;

import net.merchantpug.apugli.power.factory.SimplePowerFactory;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

public class InstantEffectImmunityPower extends Power {
    protected final HashSet<MobEffect> effects = new HashSet<>();
    private final boolean inverted;

    public InstantEffectImmunityPower(PowerType<?> type, LivingEntity entity, boolean inverted) {
        super(type, entity);
        this.inverted = inverted;
    }

    public InstantEffectImmunityPower addEffect(MobEffect effect) {
        effects.add(effect);
        return this;
    }

    public boolean doesApply(MobEffectInstance instance) {
        return doesApply(instance.getEffect());
    }

    public boolean doesApply(MobEffect effect) {
        return inverted ^ effects.contains(effect);
    }

    public static class Factory extends SimplePowerFactory<InstantEffectImmunityPower> {

        public Factory() {
            super("instant_effect_immunity",
                    new SerializableData()
                            .add("effect", SerializableDataTypes.STATUS_EFFECT, null)
                            .add("effects", SerializableDataTypes.STATUS_EFFECTS, null)
                            .add("inverted", SerializableDataTypes.BOOLEAN, false),
                    data -> (type, player) -> {
                        InstantEffectImmunityPower power = new InstantEffectImmunityPower(type, player, data.get("inverted"));
                        if(data.isPresent("effect")) {
                            power.addEffect(data.get("effect"));
                        }
                        if(data.isPresent("effects")) {
                            ((List<MobEffect>)data.get("effects")).forEach(power::addEffect);
                        }
                        return power;
                    });
            allowCondition();
        }

        @Override
        public Class<InstantEffectImmunityPower> getPowerClass() {
            return InstantEffectImmunityPower.class;
        }

    }

}
