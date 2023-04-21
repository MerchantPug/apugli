package net.merchantpug.apugli.power;

import net.merchantpug.apugli.power.factory.TameHitActionPowerFactory;
import com.google.auto.service.AutoService;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.calio.data.SerializableData;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

@AutoService(TameHitActionPowerFactory.class)
public class ActionWhenTameHitPower extends AbstractCooldownPower<ActionWhenTameHitPower.Instance> implements TameHitActionPowerFactory<ActionWhenTameHitPower.Instance> {
    
    public ActionWhenTameHitPower() {
        super("action_when_tame_hit", TameHitActionPowerFactory.getSerializableData(),
            data -> (type, entity) -> new Instance(type, entity, data));
        allowCondition();
    }
    
    @Override
    @NotNull
    public Class<Instance> getPowerClass() {
        return Instance.class;
    }
    
    public static class Instance extends AbstractCooldownPower.Instance {
    
        public Instance(PowerType<?> type, LivingEntity entity, SerializableData.Instance data) {
            super(type, entity, data);
        }
        
    }
    
}
