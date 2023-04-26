package net.merchantpug.apugli.power;

import com.google.auto.service.AutoService;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.calio.data.SerializableData;
import net.merchantpug.apugli.power.factory.ModifyBreedingCooldownPowerFactory;
import net.merchantpug.apugli.power.factory.ModifyEnchantmentDamageDealtPowerFactory;
import net.merchantpug.apugli.power.factory.ModifyEnchantmentDamagePowerFactory;
import net.merchantpug.apugli.power.factory.ModifyEnchantmentDamageTakenPowerFactory;
import net.minecraft.world.entity.LivingEntity;

@AutoService(ModifyEnchantmentDamageTakenPowerFactory.class)
public class ModifyEnchantmentDamageTakenPower extends AbstractValueModifyingPower<ModifyEnchantmentDamageTakenPower.Instance> implements ModifyEnchantmentDamageTakenPowerFactory<ModifyEnchantmentDamageTakenPower.Instance> {

    public ModifyEnchantmentDamageTakenPower() {
        super("modify_enchantment_damage_taken", ModifyEnchantmentDamagePowerFactory.getSerializableData(),
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
