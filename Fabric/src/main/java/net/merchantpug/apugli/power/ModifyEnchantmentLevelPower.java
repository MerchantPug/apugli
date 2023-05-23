package net.merchantpug.apugli.power;

import com.google.auto.service.AutoService;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.calio.data.SerializableData;
import net.merchantpug.apugli.power.factory.ModifyEnchantmentLevelPowerFactory;
import net.merchantpug.apugli.registry.power.ApugliPowers;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.concurrent.ConcurrentHashMap;

@AutoService(ModifyEnchantmentLevelPowerFactory.class)
public class ModifyEnchantmentLevelPower extends AbstractValueModifyingPower<ModifyEnchantmentLevelPower.Instance> implements ModifyEnchantmentLevelPowerFactory<ModifyEnchantmentLevelPower.Instance> {
    private static final ConcurrentHashMap<String, ConcurrentHashMap<ItemStack, ListTag>> ENTITY_ITEM_ENCHANTS = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, ConcurrentHashMap<ModifyEnchantmentLevelPower.Instance, Tuple<Integer, Boolean>>> POWER_MODIFIER_CACHE = new ConcurrentHashMap<>();

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
    public ConcurrentHashMap<String, ConcurrentHashMap<ItemStack, ListTag>> getEntityItemEnchants() {
        return ENTITY_ITEM_ENCHANTS;
    }

    @Override
    public ConcurrentHashMap<String, ConcurrentHashMap<Instance, Tuple<Integer, Boolean>>> getPowerModifierCache() {
        return POWER_MODIFIER_CACHE;
    }

    public static class Instance extends AbstractValueModifyingPower.Instance {

        public Instance(PowerType<?> type, LivingEntity entity, SerializableData.Instance data) {
            super(type, entity, data);
        }

        @Override
        public void onAdded() {
            ApugliPowers.MODIFY_ENCHANTMENT_LEVEL.get().onAdded(this, entity);
        }

        @Override
        public void onRemoved() {
            ApugliPowers.MODIFY_ENCHANTMENT_LEVEL.get().onRemoved(this, entity);
        }

    }
    
}
