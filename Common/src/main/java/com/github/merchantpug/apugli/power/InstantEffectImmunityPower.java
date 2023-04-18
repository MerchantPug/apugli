package net.merchantpug.apugli.power;

<<<<<<<< HEAD:src/main/java/net/merchantpug/apugli/power/InstantEffectImmunityPower.java
import net.merchantpug.apugli.Apugli;
========
import the.great.migration.merchantpug.apugli.Apugli;
>>>>>>>> pr/25:Common/src/main/java/com/github/merchantpug/apugli/power/InstantEffectImmunityPower.java
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import java.util.HashSet;
import java.util.List;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

public class InstantEffectImmunityPower extends Power {

    protected final HashSet<MobEffect> effects = new HashSet<>();
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
                                ((List<MobEffect>)data.get("effects")).forEach(power::addEffect);
                            }
                            return power;
                        })
                .allowCondition();
    }

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
}
