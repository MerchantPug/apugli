package net.merchantpug.apugli.power;

import com.google.auto.service.AutoService;
import com.google.common.collect.ImmutableSet;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.calio.data.SerializableData;
import net.merchantpug.apugli.integration.pehkui.ApoliScaleModifier;
import net.merchantpug.apugli.platform.Services;
import net.merchantpug.apugli.power.factory.ModifyScalePowerFactory;
import net.merchantpug.apugli.registry.power.ApugliPowers;
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
public class ModifyScalePower extends AbstractValueModifyingPower<ModifyScalePower.Instance> implements ModifyScalePowerFactory<ModifyScalePower.Instance> {

    private static final Set<ResourceLocation> EMPTY_SET = new HashSet<>();
    private static final Map<Instance, Set<ResourceLocation>> CACHE = new HashMap<>();
    private static final Map<ResourceLocation, ApoliScaleModifier> MODIFIER_CACHE = new HashMap<>();
    private static final Map<Entity, Map<Instance, Double>> VALUE_CACHE = new HashMap<>();

    public ModifyScalePower() {
        super("modify_scale", ModifyScalePowerFactory.getSerializableData(),
            data -> (type, entity) -> new Instance(type, entity, data));
        allowCondition();
    }
    
    @Override
    public Class<Instance> getPowerClass() {
        return Instance.class;
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
    public Set<ResourceLocation> getScaleTypes(Instance power) {
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
    public double getCachedEntityScale(Instance power, Entity entity) {
        return VALUE_CACHE.getOrDefault(entity, new HashMap<>()).getOrDefault(power, 1.0D);
    }

    @Override
    public void setCachedEntityScale(Instance power, Entity entity, double value) {
        VALUE_CACHE.computeIfAbsent(entity, entity1 -> new HashMap<>()).put(power, value);
    }

    @Override
    public void removeCachedEntityScale(Instance power, Entity entity) {
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
    public ResourceLocation getPowerId(Instance power) {
        return power.getType().getIdentifier();
    }

    public static class Instance extends AbstractValueModifyingPower.Instance {

        public Instance(PowerType<?> type, LivingEntity entity, SerializableData.Instance data) {
            super(type, entity, data);
            setTicking(true);
        }

        @Override
        public void tick() {
            ApugliPowers.MODIFY_SCALE.get().tick(this, this.entity);
        }

        public void onAdded() {
            ApugliPowers.MODIFY_SCALE.get().onAdded(this, this.entity);
        }

        @Override
        public void onRemoved() {
            ApugliPowers.MODIFY_SCALE.get().onRemoved(this, this.entity);
        }
    }
    
}
