package net.merchantpug.apugli.power;

import com.google.auto.service.AutoService;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.calio.data.SerializableData;
import net.merchantpug.apugli.power.factory.ActionWhenProjectileHitPowerFactory;
import net.merchantpug.apugli.power.factory.ProjectileHitActionPowerFactory;
import net.minecraft.world.entity.LivingEntity;

@AutoService(ActionWhenProjectileHitPowerFactory.class)
public class ActionWhenProjectileHitPower extends AbstractCooldownPower<ActionWhenProjectileHitPower.Instance> implements ActionWhenProjectileHitPowerFactory<ActionWhenProjectileHitPower.Instance> {

    public ActionWhenProjectileHitPower() {
        super("action_when_projectile_hit", ProjectileHitActionPowerFactory.getSerializableData(),
            data -> (type, entity) -> new ActionWhenProjectileHitPower.Instance(type, entity, data));
        allowCondition();
    }
    
    @Override
    public Class<ActionWhenProjectileHitPower.Instance> getPowerClass() {
        return Instance.class;
    }
    
    public static class Instance extends AbstractCooldownPower.Instance {
    
        public Instance(PowerType<?> type, LivingEntity entity, SerializableData.Instance data) {
            super(type, entity, data);
        }
        
    }
    
}
