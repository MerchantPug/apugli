package net.merchantpug.apugli.power;

import com.google.auto.service.AutoService;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import net.merchantpug.apugli.power.configuration.FabricValueModifyingConfiguration;
import net.merchantpug.apugli.power.factory.ModifyEnchantmentLevelPowerFactory;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;

import java.util.concurrent.ConcurrentHashMap;

@AutoService(ModifyEnchantmentLevelPowerFactory.class)
public class ModifyEnchantmentLevelPower extends AbstractValueModifyingPower implements ModifyEnchantmentLevelPowerFactory<ConfiguredPower<FabricValueModifyingConfiguration, ?>> {
    private static final ConcurrentHashMap<String, ConcurrentHashMap<ListTag, ListTag>> ENTITY_ITEM_ENCHANTS = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, ConcurrentHashMap<ConfiguredPower<FabricValueModifyingConfiguration, ?>, Tuple<Integer, Boolean>>> POWER_MODIFIER_CACHE = new ConcurrentHashMap<>();

    public ModifyEnchantmentLevelPower() {
        super(ModifyEnchantmentLevelPowerFactory.getSerializableData().xmap(
                FabricValueModifyingConfiguration::new,
                FabricValueModifyingConfiguration::data
        ).codec());
    }

    @Override
    public void onAdded(ConfiguredPower<FabricValueModifyingConfiguration, ?> configuration, Entity entity) {
        ModifyEnchantmentLevelPowerFactory.super.onAdded(configuration, entity);
    }

    @Override
    public void onRemoved(ConfiguredPower<FabricValueModifyingConfiguration, ?> configuration, Entity entity) {
        ModifyEnchantmentLevelPowerFactory.super.onRemoved(configuration, entity);
    }

    @Override
    public ConcurrentHashMap<String, ConcurrentHashMap<ListTag, ListTag>> getEntityItemEnchants() {
        return ENTITY_ITEM_ENCHANTS;
    }

    @Override
    public ConcurrentHashMap<String, ConcurrentHashMap<ConfiguredPower<FabricValueModifyingConfiguration, ?>, Tuple<Integer, Boolean>>> getPowerModifierCache() {
        return POWER_MODIFIER_CACHE;
    }

}
