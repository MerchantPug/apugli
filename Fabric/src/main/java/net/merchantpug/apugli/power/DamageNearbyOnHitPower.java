package net.merchantpug.apugli.power;

import com.google.auto.service.AutoService;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.calio.data.SerializableData;
import net.merchantpug.apugli.power.factory.DamageNearbyOnHitPowerFactory;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

@AutoService(DamageNearbyOnHitPowerFactory.class)
public class DamageNearbyOnHitPower extends AbstractCooldownPower<DamageNearbyOnHitPower.Instance> implements DamageNearbyOnHitPowerFactory<DamageNearbyOnHitPower.Instance> {

    public DamageNearbyOnHitPower() {
        super("damage_nearby_on_hit", DamageNearbyOnHitPowerFactory.getSerializableData(),
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
