package net.merchantpug.apugli.power;

import com.google.auto.service.AutoService;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.calio.data.SerializableData;
import net.merchantpug.apugli.power.factory.ModifyEnchantmentLevelPowerFactory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.Enchantment;

@AutoService(ModifyEnchantmentLevelPowerFactory.class)
public class ModifyEnchantmentLevelPower extends AbstractValueModifyingPower<ModifyEnchantmentLevelPower.Instance> implements ModifyEnchantmentLevelPowerFactory<ModifyEnchantmentLevelPower.Instance> {

    public ModifyEnchantmentLevelPower() {
        super("modify_enchantment_level", ModifyEnchantmentLevelPowerFactory.getSerializableData(),
            data -> (type, entity) -> new Instance(type, entity, data));
        allowCondition();
    }
    
    @Override
    public Class<Instance> getPowerClass() {
        return Instance.class;
    }

    @Override
    public boolean doesApply(Instance power, Entity entity, Enchantment enchantment) {
        return enchantment.equals(power.data.get("enchantment"));
    }

    public static class Instance extends AbstractValueModifyingPower.Instance {
        
        public Instance(PowerType<?> type, LivingEntity entity, SerializableData.Instance data) {
            super(type, entity, data);
        }
        
    }
    
}
