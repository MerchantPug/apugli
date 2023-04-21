package net.merchantpug.apugli.power;

import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.calio.data.SerializableData;
import net.merchantpug.apugli.power.factory.SimplePowerFactory;
import net.minecraft.world.entity.LivingEntity;

public class InvertInstantEffectsPower extends Power {

    public InvertInstantEffectsPower(PowerType<?> type, LivingEntity entity) {
        super(type, entity);
    }

    public static class Factory extends SimplePowerFactory<InvertInstantEffectsPower> {

        public Factory() {
            super("invert_instant_effects",
                    new SerializableData(),
                    data -> InvertInstantEffectsPower::new);

            allowCondition();
        }

        @Override
        public Class<InvertInstantEffectsPower> getPowerClass() {
            return InvertInstantEffectsPower.class;
        }

    }

}
