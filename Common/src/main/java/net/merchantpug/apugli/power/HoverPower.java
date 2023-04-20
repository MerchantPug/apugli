package net.merchantpug.apugli.power;

import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.merchantpug.apugli.power.factory.SimplePowerFactory;
import net.minecraft.world.entity.LivingEntity;

public class HoverPower extends Power {
    public HoverPower(PowerType<?> type, LivingEntity entity) {
        super(type, entity);
    }

    public static class Factory extends SimplePowerFactory<HoverPower> {

        public Factory() {
            super("hover",
                    new SerializableData(),
                    data -> HoverPower::new);

            allowCondition();
        }

        @Override
        public Class<HoverPower> getPowerClass() {
            return HoverPower.class;
        }

    }

}
