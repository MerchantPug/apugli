package net.merchantpug.apugli.power;

import com.google.auto.service.AutoService;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import net.merchantpug.apugli.power.configuration.FabricValueModifyingConfiguration;
import net.merchantpug.apugli.power.factory.ModifyEnchantmentLevelPowerFactory;
import net.merchantpug.apugli.util.ComparableItemStack;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@AutoService(ModifyEnchantmentLevelPowerFactory.class)
public class ModifyEnchantmentLevelPower extends AbstractValueModifyingPower implements ModifyEnchantmentLevelPowerFactory<ConfiguredPower<FabricValueModifyingConfiguration, ?>> {
    private static final ConcurrentHashMap<UUID, ConcurrentHashMap<ComparableItemStack, ListTag>> ENTITY_ITEM_ENCHANTS = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<UUID, ConcurrentHashMap<ConfiguredPower<FabricValueModifyingConfiguration, ?>, Tuple<Integer, Boolean>>> POWER_MODIFIER_CACHE = new ConcurrentHashMap<>();

    public ModifyEnchantmentLevelPower() {
        super(ModifyEnchantmentLevelPowerFactory.getSerializableData().xmap(
                FabricValueModifyingConfiguration::new,
                FabricValueModifyingConfiguration::data
        ).codec());
        this.ticking();
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
    public void tick(ConfiguredPower<FabricValueModifyingConfiguration, ?> configuration, Entity entity) {
        ModifyEnchantmentLevelPowerFactory.super.tick(configuration, entity);
    }

    @Override
    public ConcurrentHashMap<UUID, ConcurrentHashMap<ComparableItemStack, ListTag>> getEntityItemEnchants() {
        return ENTITY_ITEM_ENCHANTS;
    }

    @Override
    public ConcurrentHashMap<UUID, ConcurrentHashMap<ConfiguredPower<FabricValueModifyingConfiguration, ?>, Tuple<Integer, Boolean>>> getPowerModifierCache() {
        return POWER_MODIFIER_CACHE;
    }

}
