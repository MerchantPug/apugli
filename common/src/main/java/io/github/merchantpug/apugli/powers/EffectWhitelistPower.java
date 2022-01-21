package io.github.merchantpug.apugli.powers;

import io.github.apace100.origins.power.Power;
import io.github.apace100.origins.power.PowerType;
import io.github.apace100.origins.power.factory.PowerFactory;
import io.github.apace100.origins.util.SerializableData;
import io.github.apace100.origins.util.SerializableDataType;
import io.github.merchantpug.apugli.Apugli;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;

import java.util.HashSet;
import java.util.List;

public class EffectWhitelistPower extends Power {

    protected final HashSet<StatusEffect> effects = new HashSet<>();

    public static PowerFactory<?> getFactory() {
        return new PowerFactory<>(Apugli.identifier("effect_whitelist"),
                new SerializableData()
                        .add("effect", SerializableDataType.STATUS_EFFECT, null)
                        .add("effects", SerializableDataType.STATUS_EFFECTS, null),
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

    public EffectWhitelistPower(PowerType<?> type, PlayerEntity player) {
        super(type, player);
    }

    public EffectWhitelistPower(PowerType<?> type, PlayerEntity player, StatusEffect effect) {
        super(type, player);
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
