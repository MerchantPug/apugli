package net.merchantpug.apugli.power;

import com.google.auto.service.AutoService;
import com.google.common.collect.ImmutableSet;
import io.github.apace100.calio.data.SerializableData;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import net.merchantpug.apugli.integration.pehkui.ApoliScaleModifier;
import net.merchantpug.apugli.platform.Services;
import net.merchantpug.apugli.power.configuration.FabricValueModifyingConfiguration;
import net.merchantpug.apugli.power.factory.ModifyScalePowerFactory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@AutoService(ModifyScalePowerFactory.class)
public class ModifyScalePower extends AbstractValueModifyingPower implements ModifyScalePowerFactory<ConfiguredPower<FabricValueModifyingConfiguration, ?>> {

    private static final Set<ResourceLocation> EMPTY_SET = new HashSet<>();
    private static final Map<ConfiguredPower<FabricValueModifyingConfiguration, ?>, Set<ResourceLocation>> CACHE = new HashMap<>();
    private static final Map<ResourceLocation, ApoliScaleModifier> MODIFIER_CACHE = new HashMap<>();
    private static final Map<Entity, Map<ConfiguredPower<FabricValueModifyingConfiguration, ?>, Double>> VALUE_CACHE = new HashMap<>();

    public ModifyScalePower() {
        super(ModifyScalePowerFactory.getSerializableData().xmap(
                FabricValueModifyingConfiguration::new,
                FabricValueModifyingConfiguration::data
        ).codec());
        this.ticking(true);
    }

    @Override
    public void tick(ConfiguredPower<FabricValueModifyingConfiguration, ?> power, Entity entity) {
        if (entity instanceof LivingEntity living)
            ModifyScalePowerFactory.super.tick(power, living);
    }

    @Override
    public void onAdded(ConfiguredPower<FabricValueModifyingConfiguration, ?> power, Entity entity) {
        if (entity instanceof LivingEntity living)
            ModifyScalePowerFactory.super.onAdded(power, living);
    }

    @Override
    public void onRemoved(ConfiguredPower<FabricValueModifyingConfiguration, ?> power, Entity entity) {
        if (entity instanceof LivingEntity living)
            ModifyScalePowerFactory.super.onRemoved(power, living);
    }

    @Override
    @Nullable
    public ApoliScaleModifier getModifierFromCache(ResourceLocation id) {
        if (!MODIFIER_CACHE.containsKey(id)) {
            return null;
        }
        return MODIFIER_CACHE.get(id);
    }

    @Override
    public void addModifierToCache(ResourceLocation id, ApoliScaleModifier modifier) {
        MODIFIER_CACHE.put(id, modifier);
    }

    @Override
    public void clearModifiersFromCache() {
        MODIFIER_CACHE.clear();
    }

    @Override
    public Set<ResourceLocation> getScaleTypes(ConfiguredPower<FabricValueModifyingConfiguration, ?> power) {
        // Just a failsafe for if this power is somehow loaded without Pehkui.
        if (!Services.PLATFORM.isModLoaded("pehkui")) {
            return EMPTY_SET;
        }

        if (!CACHE.containsKey(power)) {
            SerializableData.Instance data = getDataFromPower(power);
            ImmutableSet.Builder<ResourceLocation> builder = ImmutableSet.builder();

            data.<ResourceLocation>ifPresent("scale_type", builder::add);
            data.<List<ResourceLocation>>ifPresent("scale_types", builder::addAll);

            CACHE.put(power, builder.build());
        }
        return CACHE.get(power);
    }

    @Override
    public void clearScaleTypeCache() {
        CACHE.clear();
    }

    @Override
    public double getCachedEntityScale(ConfiguredPower<FabricValueModifyingConfiguration, ?> power, Entity entity) {
        return VALUE_CACHE.getOrDefault(entity, new HashMap<>()).getOrDefault(power, 1.0D);
    }

    @Override
    public void setCachedEntityScale(ConfiguredPower<FabricValueModifyingConfiguration, ?> power, Entity entity, double value) {
        VALUE_CACHE.computeIfAbsent(entity, entity1 -> new HashMap<>()).put(power, value);
    }

    @Override
    public void removeCachedEntityScale(ConfiguredPower<FabricValueModifyingConfiguration, ?> power, Entity entity) {
        if (VALUE_CACHE.containsKey(entity)) {
            VALUE_CACHE.get(entity).remove(power);
            if (VALUE_CACHE.get(entity).isEmpty())
                VALUE_CACHE.remove(entity);
        }
    }

    @Override
    public Set<Entity> getEntitiesWithPower() {
        return VALUE_CACHE.keySet();
    }

    @Override
    public ResourceLocation getPowerId(ConfiguredPower<FabricValueModifyingConfiguration, ?> power) {
        return power.getRegistryName();
    }

}
