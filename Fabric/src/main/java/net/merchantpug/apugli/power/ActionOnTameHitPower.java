package net.merchantpug.apugli.power;

import com.google.auto.service.AutoService;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.calio.data.SerializableData;
import net.merchantpug.apugli.power.factory.ActionOnTameHitPowerFactory;
import net.merchantpug.apugli.power.factory.TameHitActionPowerFactory;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

@AutoService(ActionOnTameHitPowerFactory.class)
public class ActionOnTameHitPower extends AbstractCooldownPower<ActionOnTameHitPower.Instance> implements ActionOnTameHitPowerFactory<ActionOnTameHitPower.Instance> {

    public ActionOnTameHitPower() {
        super("action_on_tame_hit", TameHitActionPowerFactory.getSerializableData(),
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
