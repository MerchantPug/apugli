package net.merchantpug.apugli.power;

import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.merchantpug.apugli.power.factory.SimplePowerFactory;
import net.minecraft.world.entity.LivingEntity;

public class SprintingPower extends Power {

    private final boolean requiresInput;

    public SprintingPower(PowerType<?> type, LivingEntity entity, boolean requiresInput) {
        super(type, entity);
        this.requiresInput = requiresInput;
    }

    public boolean requiresInput() {
        return requiresInput;
    }

    public static class Factory extends SimplePowerFactory<SprintingPower> {

        public Factory() {
            super("sprinting",
                    new SerializableData()
                            .add("requires_input", SerializableDataTypes.BOOLEAN, false),
                    data -> (power, living) -> new SprintingPower(power, living, data.getBoolean("requires_input")));
            allowCondition();
        }

        @Override
        public Class<SprintingPower> getPowerClass() {
            return SprintingPower.class;
        }

    }

}
