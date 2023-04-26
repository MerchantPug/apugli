package net.merchantpug.apugli.power;

import com.google.auto.service.AutoService;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.calio.data.SerializableData;
import net.merchantpug.apugli.power.factory.ModifyEnchantmentLevelPowerFactory;
import net.merchantpug.apugli.registry.power.ApugliPowers;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.LivingEntity;
import org.apache.commons.lang3.tuple.Pair;

import java.util.concurrent.ConcurrentHashMap;

@AutoService(ModifyEnchantmentLevelPowerFactory.class)
public class ModifyEnchantmentLevelPower extends AbstractValueModifyingPower<ModifyEnchantmentLevelPower.Instance> implements ModifyEnchantmentLevelPowerFactory<ModifyEnchantmentLevelPower.Instance> {
    private static final ConcurrentHashMap<String, ConcurrentHashMap<ListTag, ListTag>> ENTITY_ITEM_ENCHANTS = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, ConcurrentHashMap<ListTag, ConcurrentHashMap<Instance, Pair<Integer, Boolean>>>> PREVIOUS_POWER_STATE = new ConcurrentHashMap<>();

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
    public ConcurrentHashMap<String, ConcurrentHashMap<ListTag, ListTag>> getEntityItemEnchants() {
        return ENTITY_ITEM_ENCHANTS;
    }

    @Override
    public ConcurrentHashMap<String, ConcurrentHashMap<ListTag, ConcurrentHashMap<Instance, Pair<Integer, Boolean>>>> getPreviousPowerState() {
        return PREVIOUS_POWER_STATE;
    }

    public static class Instance extends AbstractValueModifyingPower.Instance {

        public Instance(PowerType<?> type, LivingEntity entity, SerializableData.Instance data) {
            super(type, entity, data);
        }

        @Override
        public void onRemoved() {
            ApugliPowers.MODIFY_ENCHANTMENT_LEVEL.get().onRemoved(this, entity);
        }

    }
    
}
