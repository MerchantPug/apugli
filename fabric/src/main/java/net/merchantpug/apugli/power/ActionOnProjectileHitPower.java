package net.merchantpug.apugli.power;

import com.google.auto.service.AutoService;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.calio.data.SerializableData;
import net.merchantpug.apugli.power.factory.ActionOnProjectileHitPowerFactory;
import net.merchantpug.apugli.power.factory.ProjectileHitActionPowerFactory;
import net.minecraft.world.entity.LivingEntity;

@AutoService(ActionOnProjectileHitPowerFactory.class)
public class ActionOnProjectileHitPower extends AbstractCooldownPower<ActionOnProjectileHitPower.Instance> implements ActionOnProjectileHitPowerFactory<ActionOnProjectileHitPower.Instance> {

    public ActionOnProjectileHitPower() {
        super("action_on_projectile_hit", ProjectileHitActionPowerFactory.getSerializableData(),
            data -> (type, entity) -> new ActionOnProjectileHitPower.Instance(type, entity, data));
        allowCondition();
    }
    
    @Override
    public Class<ActionOnProjectileHitPower.Instance> getPowerClass() {
        return Instance.class;
    }
    
    public static class Instance extends AbstractCooldownPower.Instance {
    
        public Instance(PowerType<?> type, LivingEntity entity, SerializableData.Instance data) {
            super(type, entity, data);
        }
        
    }
    
}
