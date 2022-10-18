package com.github.merchantpug.apugli.power;

import com.github.merchantpug.apugli.power.factory.ActionWhenTameHitPowerFactory;
import com.google.auto.service.AutoService;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.calio.data.SerializableData;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

@AutoService(ActionWhenTameHitPowerFactory.class)
public class ActionWhenTameHitPower extends AbstractCooldownPower<ActionWhenTameHitPower.Instance> implements ActionWhenTameHitPowerFactory<ActionWhenTameHitPower.Instance> {
    
    public ActionWhenTameHitPower() {
        super("action_when_tame_hit", ActionWhenTameHitPowerFactory.getSerializableData(),
            data -> (type, entity) -> new Instance(type, entity, data));
        allowCondition();
    }
    
    @Override
    @NotNull
    public Class<Instance> getPowerClass() {
        return Instance.class;
    }
    
    @Override
    public SerializableData.Instance getDataFromPower(Instance power) {
        return power.data;
    }
    
    public static class Instance extends AbstractCooldownPower.Instance {
        private final SerializableData.Instance data;
    
        public Instance(PowerType<?> type, LivingEntity entity, SerializableData.Instance data) {
            super(type, entity, data);
            this.data = data;
        }
        
    }
    
}
