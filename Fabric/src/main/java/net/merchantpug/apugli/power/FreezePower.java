package net.merchantpug.apugli.power;

import com.google.auto.service.AutoService;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.calio.data.SerializableData;
import net.merchantpug.apugli.power.factory.FreezePowerFactory;
import net.minecraft.world.entity.LivingEntity;

@AutoService(FreezePowerFactory.class)
public class FreezePower extends AbstractValueModifyingPower<FreezePower.Instance> implements FreezePowerFactory<FreezePower.Instance> {

    public FreezePower() {
        super("freeze", FreezePowerFactory.getSerializableData(),
                data -> (type, entity) -> new FreezePower.Instance(type, entity, data));
        allowCondition();
    }

    @Override
    public Class<FreezePower.Instance> getPowerClass() {
        return FreezePower.Instance.class;
    }

    public static class Instance extends AbstractValueModifyingPower.Instance {

        public Instance(PowerType<?> type, LivingEntity entity, SerializableData.Instance data) {
            super(type, entity, data);
        }

    }

}
