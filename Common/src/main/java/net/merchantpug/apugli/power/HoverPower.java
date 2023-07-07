package net.merchantpug.apugli.power;

import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.merchantpug.apugli.platform.Services;
import net.merchantpug.apugli.power.factory.SimplePowerFactory;
import net.merchantpug.apugli.registry.power.ApugliPowers;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;
import java.util.Optional;

public class HoverPower extends Power {
    private final double correctionRange;

    public HoverPower(PowerType<?> type, LivingEntity entity, double correctionRange) {
        super(type, entity);
        this.correctionRange = correctionRange;
    }

    public boolean canCorrectHeight() {
        return correctionRange > 0.0F;
    }

    public double getCorrectionRange() {
        return correctionRange;
    }

    public static Optional<Double> getCorrectionRange(LivingEntity living) {
        List<HoverPower> powerList = Services.POWER.getPowers(living, ApugliPowers.HOVER.get())
                .stream()
                .filter(HoverPower::canCorrectHeight)
                .toList();
        if (!powerList.isEmpty()) {
            return powerList.stream().map(HoverPower::getCorrectionRange)
                    .max(Double::compare);
        }
        return Optional.empty();
    }

    public static class Factory extends SimplePowerFactory<HoverPower> {

        public Factory() {
            super("hover",
                    new SerializableData()
                            .add("step_assist", SerializableDataTypes.DOUBLE, 0.0),
                    data -> (type, entity) -> new HoverPower(type, entity, data.getDouble("step_assist")));
            allowCondition();
        }

        @Override
        public Class<HoverPower> getPowerClass() {
            return HoverPower.class;
        }

    }

}
