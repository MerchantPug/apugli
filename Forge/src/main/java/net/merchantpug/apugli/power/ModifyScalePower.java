package net.merchantpug.apugli.power;

import com.google.auto.service.AutoService;
import com.google.common.collect.ImmutableSet;
import io.github.apace100.calio.data.SerializableData;
import io.github.edwinmindcraft.apoli.api.component.IPowerContainer;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import net.merchantpug.apugli.integration.pehkui.ApoliScaleModifier;
import net.merchantpug.apugli.platform.Services;
import net.merchantpug.apugli.power.configuration.FabricValueModifyingConfiguration;
import net.merchantpug.apugli.power.factory.ModifyScalePowerFactory;
import net.merchantpug.apugli.registry.power.ApugliPowers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@AutoService(ModifyScalePowerFactory.class)
public class ModifyScalePower extends AbstractValueModifyingPower implements ModifyScalePowerFactory<ConfiguredPower<FabricValueModifyingConfiguration, ?>> {
    private static final Set<ResourceLocation> EMPTY_SET = new HashSet<>();
    private static final Map<Entity, Map<ConfiguredPower<FabricValueModifyingConfiguration, ?>, Set<ResourceLocation>>> TYPE_CACHE = new HashMap<>();
    private static final Map<Entity, Map<ResourceLocation, ApoliScaleModifier<ConfiguredPower<FabricValueModifyingConfiguration, ?>>>> MODIFIER_CACHE = new HashMap<>();

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
    public @Nullable ApoliScaleModifier getModifierFromCache(ResourceLocation id, Entity entity) {
        if (!MODIFIER_CACHE.containsKey(entity) || !MODIFIER_CACHE.get(entity).containsKey(id)) {
            return null;
        }
        return MODIFIER_CACHE.get(entity).get(id);
    }

    @Override
    public void addModifierToCache(ResourceLocation id, Entity entity, ApoliScaleModifier modifier) {
        MODIFIER_CACHE.computeIfAbsent(entity, entity1 -> new HashMap<>()).put(id, modifier);
    }

    @Override
    public void removeModifierFromCache(ResourceLocation id, Entity entity) {
        if (MODIFIER_CACHE.containsKey(entity)) {
            MODIFIER_CACHE.get(entity).remove(id);
            if (MODIFIER_CACHE.get(entity).isEmpty())
                MODIFIER_CACHE.remove(entity);
        }
    }

    @Override
    public Set<ResourceLocation> getScaleTypeCache(ConfiguredPower<FabricValueModifyingConfiguration, ?> power, Entity entity) {
        // Just a failsafe for if this power is somehow loaded without Pehkui.
        if (!Services.PLATFORM.isModLoaded("pehkui")) {
            return EMPTY_SET;
        }

        if (!TYPE_CACHE.containsKey(entity) || !TYPE_CACHE.get(entity).containsKey(power)) {
            SerializableData.Instance data = getDataFromPower(power);
            ImmutableSet.Builder<ResourceLocation> builder = ImmutableSet.builder();

            data.<ResourceLocation>ifPresent("scale_type", builder::add);
            data.<List<ResourceLocation>>ifPresent("scale_types", builder::addAll);

            TYPE_CACHE.computeIfAbsent(entity, e -> new HashMap<>()).put(power, builder.build());
        }
        return TYPE_CACHE.get(entity).get(power);
    }

    @Override
    public void removeScaleTypesFromCache(ConfiguredPower<FabricValueModifyingConfiguration, ?> power, Entity entity) {
        if (TYPE_CACHE.containsKey(entity)) {
            TYPE_CACHE.get(entity).remove(power);
            if (TYPE_CACHE.get(entity).isEmpty())
                TYPE_CACHE.remove(entity);
        }
    }

    @Override
    public ResourceLocation getPowerId(ConfiguredPower<FabricValueModifyingConfiguration, ?> power) {
        return power.getRegistryName();
    }

}
