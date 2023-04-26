package net.merchantpug.apugli.power;

import com.google.auto.service.AutoService;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.calio.data.SerializableData;
import net.merchantpug.apugli.power.factory.ActionOnHarmPowerFactory;
import net.merchantpug.apugli.power.factory.ActionWhenHarmedPowerFactory;
import net.merchantpug.apugli.power.factory.HarmActionPowerFactory;
import net.merchantpug.apugli.power.factory.TameHitActionPowerFactory;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

@AutoService(ActionOnHarmPowerFactory.class)
public class ActionOnHarmPower extends AbstractCooldownPower<ActionOnHarmPower.Instance> implements ActionOnHarmPowerFactory<ActionOnHarmPower.Instance> {

    public ActionOnHarmPower() {
        super("action_on_harm", HarmActionPowerFactory.getSerializableData(),
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
