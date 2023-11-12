package net.merchantpug.apugli.power;

import net.merchantpug.apugli.power.factory.ActionOnTargetDeathPowerFactory;
import com.google.auto.service.AutoService;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.calio.data.SerializableData;
import net.minecraft.world.entity.LivingEntity;

@AutoService(ActionOnTargetDeathPowerFactory.class)
public class ActionOnTargetDeathPower extends AbstractCooldownPower<ActionOnTargetDeathPower.Instance> implements ActionOnTargetDeathPowerFactory<ActionOnTargetDeathPower.Instance> {
    
    public ActionOnTargetDeathPower() {
        super("action_on_target_death", ActionOnTargetDeathPowerFactory.getSerializableData(),
            data -> (type, entity) -> new ActionOnTargetDeathPower.Instance(type, entity, data));
        allowCondition();
    }
    
    @Override
    public Class<ActionOnTargetDeathPower.Instance> getPowerClass() {
        return Instance.class;
    }
    
    public static class Instance extends AbstractCooldownPower.Instance {
    
        public Instance(PowerType<?> type, LivingEntity entity, SerializableData.Instance data) {
            super(type, entity, data);
        }
        
    }
    
}
