package net.merchantpug.apugli.power;

import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.calio.data.SerializableData;
import net.merchantpug.apugli.power.factory.SimplePowerFactory;
import net.minecraft.world.entity.LivingEntity;

public class SprintingPower extends Power {

    public SprintingPower(PowerType<?> type, LivingEntity entity) {
        super(type, entity);
    }

    public static class Factory extends SimplePowerFactory<SprintingPower> {

        public Factory() {
            super("sprinting",
                    new SerializableData(),
                    data -> SprintingPower::new);
            allowCondition();
        }

        @Override
        public Class<SprintingPower> getPowerClass() {
            return SprintingPower.class;
        }

    }

}
