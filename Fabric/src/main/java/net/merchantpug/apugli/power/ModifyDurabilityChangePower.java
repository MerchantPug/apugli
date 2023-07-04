package net.merchantpug.apugli.power;

import com.google.auto.service.AutoService;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.calio.data.SerializableData;
import net.merchantpug.apugli.power.factory.ModifyDurabilityChangePowerFactory;
import net.minecraft.world.entity.LivingEntity;

@AutoService(ModifyDurabilityChangePowerFactory.class)
public class ModifyDurabilityChangePower extends AbstractValueModifyingPower<ModifyDurabilityChangePower.Instance> implements ModifyDurabilityChangePowerFactory<ModifyDurabilityChangePower.Instance> {

    public ModifyDurabilityChangePower() {
        super("modify_durability_change", ModifyDurabilityChangePowerFactory.getSerializableData(),
                data -> (type, entity) -> new ModifyDurabilityChangePower.Instance(type, entity, data));
        allowCondition();
    }

    @Override
    public Class<ModifyDurabilityChangePower.Instance> getPowerClass() {
        return ModifyDurabilityChangePower.Instance.class;
    }

    public static class Instance extends AbstractValueModifyingPower.Instance {

        public Instance(PowerType<?> type, LivingEntity entity, SerializableData.Instance data) {
            super(type, entity, data);
        }

    }

}
