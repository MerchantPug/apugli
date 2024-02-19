package net.merchantpug.apugli.power;

import com.google.auto.service.AutoService;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.calio.data.SerializableData;
import net.merchantpug.apugli.power.factory.ModifyEnchantmentLevelPowerFactory;
import net.merchantpug.apugli.registry.power.ApugliPowers;
import net.merchantpug.apugli.util.ComparableItemStack;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.LivingEntity;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@AutoService(ModifyEnchantmentLevelPowerFactory.class)
public class ModifyEnchantmentLevelPower extends AbstractValueModifyingPower<ModifyEnchantmentLevelPower.Instance> implements ModifyEnchantmentLevelPowerFactory<ModifyEnchantmentLevelPower.Instance> {
        private static final ConcurrentHashMap<UUID, ConcurrentHashMap<ComparableItemStack, ListTag>> ENTITY_ITEM_ENCHANTS = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<UUID, ConcurrentHashMap<ModifyEnchantmentLevelPower.Instance, Tuple<Integer, Boolean>>> POWER_MODIFIER_CACHE = new ConcurrentHashMap<>();

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
    public ConcurrentHashMap<UUID, ConcurrentHashMap<ComparableItemStack, ListTag>> getEntityItemEnchants() {
        return ENTITY_ITEM_ENCHANTS;
    }

    @Override
    public ConcurrentHashMap<UUID, ConcurrentHashMap<Instance, Tuple<Integer, Boolean>>> getPowerModifierCache() {
        return POWER_MODIFIER_CACHE;
    }

    public static class Instance extends AbstractValueModifyingPower.Instance {

        public Instance(PowerType<?> type, LivingEntity entity, SerializableData.Instance data) {
            super(type, entity, data);
            this.setTicking();
        }

        @Override
        public void onAdded() {
            ApugliPowers.MODIFY_ENCHANTMENT_LEVEL.get().onAdded(this, entity);
        }

        @Override
        public void onRemoved() {
            ApugliPowers.MODIFY_ENCHANTMENT_LEVEL.get().onRemoved(this, entity);
        }

        @Override
        public void tick() {
            ApugliPowers.MODIFY_ENCHANTMENT_LEVEL.get().tick(this, entity);
        }

    }
    
}
