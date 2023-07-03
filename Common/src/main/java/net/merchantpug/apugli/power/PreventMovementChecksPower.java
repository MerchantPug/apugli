package net.merchantpug.apugli.power;

import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.calio.data.SerializableData;
import net.merchantpug.apugli.power.factory.SimplePowerFactory;
import net.minecraft.world.entity.LivingEntity;

public class PreventMovementChecksPower extends Power {

    public PreventMovementChecksPower(PowerType<?> type, LivingEntity entity) {
        super(type, entity);
    }

    public static class Factory extends SimplePowerFactory<PreventMovementChecksPower> {

        public Factory() {
            super("prevent_movement_checks",
                    new SerializableData(),
                    data -> PreventMovementChecksPower::new);
            allowCondition();
        }

        @Override
        public Class<PreventMovementChecksPower> getPowerClass() {
            return PreventMovementChecksPower.class;
        }

    }

}
