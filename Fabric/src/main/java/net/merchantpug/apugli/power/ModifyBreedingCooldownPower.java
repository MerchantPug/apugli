package net.merchantpug.apugli.power;

import com.google.auto.service.AutoService;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.calio.data.SerializableData;
import net.merchantpug.apugli.power.factory.ModifyBreedingCooldownPowerFactory;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.function.Predicate;

@AutoService(ModifyBreedingCooldownPowerFactory.class)
public class ModifyBreedingCooldownPower extends AbstractValueModifyingPower<ModifyBreedingCooldownPower.Instance> implements ModifyBreedingCooldownPowerFactory<ModifyBreedingCooldownPower.Instance> {

    public ModifyBreedingCooldownPower() {
        super("modify_breeding_cooldown", ModifyBreedingCooldownPowerFactory.getSerializableData(),
            data -> (type, entity) -> new Instance(type, entity, data));
        allowCondition();
    }
    
    @Override
    public Class<Instance> getPowerClass() {
        return Instance.class;
    }

    public static class Instance extends AbstractValueModifyingPower.Instance {
        
        public Instance(PowerType<?> type, LivingEntity entity, SerializableData.Instance data) {
            super(type, entity, data);
        }
        
    }
    
}
