package net.merchantpug.apugli.power;

import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.calio.data.SerializableData;
import net.merchantpug.apugli.power.factory.SimplePowerFactory;
import net.minecraft.world.entity.LivingEntity;

public class PreventBeeAngerPower extends Power {

    public PreventBeeAngerPower(PowerType<?> type, LivingEntity entity) {
        super(type, entity);
    }

    public static class Factory extends SimplePowerFactory<PreventBeeAngerPower> {

        public Factory() {
            super("prevent_bee_anger",
                    new SerializableData(),
                    data -> PreventBeeAngerPower::new);

            allowCondition();
        }

        @Override
        public Class<PreventBeeAngerPower> getPowerClass() {
            return PreventBeeAngerPower.class;
        }

    }

}
