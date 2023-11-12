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

public class StepHeightPower extends Power {
    private final double lowerCorrectionRange;
    private final double upperCorrectionRange;
    private final boolean allowJumpAfter;

    public StepHeightPower(PowerType<?> powerType, LivingEntity entity, double lowerCorrectionRange, double upperCorrectionRange, boolean allowJumpAfter) {
        super(powerType, entity);
        this.lowerCorrectionRange = lowerCorrectionRange;
        this.upperCorrectionRange = upperCorrectionRange;
        this.allowJumpAfter = allowJumpAfter;
    }

    public boolean canCorrectLowerHeight() {
        return this.lowerCorrectionRange > 0.0;
    }

    public boolean canCorrectUpperHeight() {
        return this.upperCorrectionRange > 0.0;
    }

    public double getLowerCorrectionRange() {
        return this.lowerCorrectionRange;
    }

    public double getUpperCorrectionRange() {
        return this.upperCorrectionRange;
    }

    public boolean shouldAllowJumpAfter() {
        return this.allowJumpAfter;
    }

    public static Optional<Double> getLowerCorrectionRange(LivingEntity living) {
        List<StepHeightPower> powerList = Services.POWER.getPowers(living, ApugliPowers.STEP_HEIGHT.get())
                .stream()
                .filter(StepHeightPower::canCorrectLowerHeight)
                .toList();
        if (!powerList.isEmpty()) {
            return powerList.stream().map(StepHeightPower::getLowerCorrectionRange)
                    .max(Double::compare);
        }
        return Optional.empty();
    }

    public static Optional<Double> getUpperCorrectionRange(LivingEntity living) {
        List<StepHeightPower> powerList = Services.POWER.getPowers(living, ApugliPowers.STEP_HEIGHT.get())
                .stream()
                .filter(StepHeightPower::canCorrectUpperHeight)
                .toList();
        if (!powerList.isEmpty()) {
            return powerList.stream().map(StepHeightPower::getUpperCorrectionRange)
                    .max(Double::compare);
        }
        return Optional.empty();
    }

    public static class Factory extends SimplePowerFactory<StepHeightPower> {

        public Factory() {
            super("step_height",
                    new SerializableData()
                            .add("lower_height", SerializableDataTypes.DOUBLE, 0.0)
                            .add("upper_height", SerializableDataTypes.DOUBLE, 0.0)
                            .add("allow_jump_after", SerializableDataTypes.BOOLEAN, false),
                    data -> (type, entity) -> new StepHeightPower(type, entity,
                            data.getDouble("lower_height"),
                            data.getDouble("upper_height"),
                            data.getBoolean("allow_jump_after")));
            allowCondition();
        }

        @Override
        public Class<StepHeightPower> getPowerClass() {
            return StepHeightPower.class;
        }

    }

}
