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
    private static final Map<Entity, Map<Instance, Set<ResourceLocation>>> TYPE_CACHE = new HashMap<>();
    private static final Map<Entity, Map<ResourceLocation, ApoliScaleModifier<Instance>>> MODIFIER_CACHE = new HashMap<>();

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
    public Set<ResourceLocation> getScaleTypeCache(Instance power, Entity entity) {
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
    public void removeScaleTypesFromCache(Instance power, Entity entity) {
        if (TYPE_CACHE.containsKey(entity)) {
            TYPE_CACHE.get(entity).remove(power);
            if (TYPE_CACHE.get(entity).isEmpty())
                TYPE_CACHE.remove(entity);
        }
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
