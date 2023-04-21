package net.merchantpug.apugli.power.factory;

import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.merchantpug.apugli.platform.Services;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

import java.util.List;

public interface ModifyEnchantmentDamagePowerFactory<P> extends ValueModifyingPowerFactory<P> {

    static SerializableData getSerializableData() {
        return ValueModifyingPowerFactory.getSerializableData()
                .add("enchantment", SerializableDataTypes.ENCHANTMENT)
                .add("base_value", SerializableDataTypes.FLOAT)
                .add("damage_condition", Services.CONDITION.damageDataType(), null)
                .add("bientity_condition", Services.CONDITION.biEntityDataType(), null)
                .add("bientity_action", Services.ACTION.biEntityDataType(), null);
    }


    default boolean doesApply(P power, DamageSource source, float damageAmount, LivingEntity attacker, LivingEntity target) {
        SerializableData.Instance data = getDataFromPower(power);
        return Services.CONDITION.checkDamage(data, "damage_condition", source, damageAmount) && (Services.CONDITION.checkBiEntity(data, "bientity_condition", attacker, target)) && EnchantmentHelper.getItemEnchantmentLevel(data.get("enchantment"), attacker.getItemBySlot(EquipmentSlot.MAINHAND)) != 0;
    }

    default float applyModifiers(LivingEntity powerHolder, DamageSource source, float originalAmount, LivingEntity attacker, LivingEntity target) {
        float additionalValue = 0.0F;
        List<P> damageDealtPowers = Services.POWER.getPowers(powerHolder, this).stream().filter(p -> doesApply(p, source, originalAmount, attacker, target)).toList();

        for (P power : damageDealtPowers) {
            SerializableData.Instance data = getDataFromPower(power);
            additionalValue += data.getFloat("base_value");
            for (int i = 0; i < EnchantmentHelper.getItemEnchantmentLevel(data.get("enchantment"), ((LivingEntity) source.getEntity()).getItemBySlot(EquipmentSlot.MAINHAND)); ++i) {
                additionalValue = (float) Services.PLATFORM.applyModifiers(powerHolder, this, additionalValue);
                Services.ACTION.executeBiEntity(data, "bientity_action", attacker, target);
            }
        }
        return additionalValue;
    }

}
