package net.merchantpug.apugli.power;

import com.google.auto.service.AutoService;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.calio.data.SerializableData;
import net.merchantpug.apugli.power.factory.ModifyEnchantmentDamageDealtPowerFactory;
import net.merchantpug.apugli.power.factory.ModifyEnchantmentDamagePowerFactory;
import net.minecraft.world.entity.LivingEntity;

@AutoService(ModifyEnchantmentDamageDealtPowerFactory.class)
public class ModifyEnchantmentDamageDealtPower extends AbstractValueModifyingPower<ModifyEnchantmentDamageDealtPower.Instance> implements ModifyEnchantmentDamageDealtPowerFactory<ModifyEnchantmentDamageDealtPower.Instance> {

    public ModifyEnchantmentDamageDealtPower() {
        super("modify_enchantment_damage_dealt", ModifyEnchantmentDamagePowerFactory.getSerializableData(),
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
