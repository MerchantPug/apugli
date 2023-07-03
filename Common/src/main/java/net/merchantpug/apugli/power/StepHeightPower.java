package net.merchantpug.apugli.power;

import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.merchantpug.apugli.platform.Services;
import net.merchantpug.apugli.power.factory.SimplePowerFactory;
import net.merchantpug.apugli.registry.power.ApugliPowers;
import net.minecraft.world.entity.LivingEntity;

import java.util.Optional;

public class StepHeightPower extends Power {
    private final float lowerCorrectionRange;
    private final float upperCorrectionRange;
    private final boolean allowJumpAfter;

    public StepHeightPower(PowerType<?> powerType, LivingEntity entity, float lowerCorrectionRange, float upperCorrectionRange, boolean allowJumpAfter) {
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

    public float getLowerCorrectionRange() {
        return this.lowerCorrectionRange;
    }

    public float getUpperCorrectionRange() {
        return this.upperCorrectionRange;
    }

    public boolean shouldAllowJumpAfter() {
        return this.allowJumpAfter;
    }

    public static Optional<Float> getLowerCorrectionRange(LivingEntity living) {
        return Services.POWER.getPowers(living, ApugliPowers.STEP_HEIGHT.get())
                .stream()
                .filter(StepHeightPower::canCorrectLowerHeight)
                .map(StepHeightPower::getLowerCorrectionRange)
                .max(Float::compare);
    }

    public static Optional<Float> getUpperCorrectionRange(LivingEntity living) {
        return Services.POWER.getPowers(living, ApugliPowers.STEP_HEIGHT.get())
                .stream()
                .filter(StepHeightPower::canCorrectUpperHeight)
                .map(StepHeightPower::getUpperCorrectionRange)
                .max(Float::compare);
    }

    public static class Factory extends SimplePowerFactory<StepHeightPower> {

        public Factory() {
            super("step_height",
                    new SerializableData()
                            .add("lower_height", SerializableDataTypes.FLOAT, 0.0F)
                            .add("upper_height", SerializableDataTypes.FLOAT, 0.0F)
                            .add("allow_jump_after", SerializableDataTypes.BOOLEAN, false),
                    data -> (type, entity) -> new StepHeightPower(type, entity,
                            data.getFloat("lower_height"),
                            data.getFloat("upper_height"),
                            data.getBoolean("allow_jump_after")));
            allowCondition();
        }

        @Override
        public Class<StepHeightPower> getPowerClass() {
            return StepHeightPower.class;
        }

    }

}
