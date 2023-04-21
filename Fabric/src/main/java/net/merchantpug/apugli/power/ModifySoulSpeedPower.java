package net.merchantpug.apugli.power;

import com.google.auto.service.AutoService;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.calio.data.SerializableData;
import net.merchantpug.apugli.power.factory.ModifyBreedingCooldownPowerFactory;
import net.merchantpug.apugli.power.factory.ModifySoulSpeedPowerFactory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;

import java.util.List;
import java.util.function.Predicate;

@AutoService(ModifySoulSpeedPowerFactory.class)
public class ModifySoulSpeedPower extends AbstractValueModifyingPower<ModifySoulSpeedPower.Instance> implements ModifySoulSpeedPowerFactory<ModifySoulSpeedPower.Instance> {

    public ModifySoulSpeedPower() {
        super("modify_soul_speed", ModifySoulSpeedPowerFactory.getSerializableData(),
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
