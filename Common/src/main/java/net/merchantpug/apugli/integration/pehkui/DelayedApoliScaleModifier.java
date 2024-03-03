package net.merchantpug.apugli.integration.pehkui;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.floats.Float2FloatFunction;
import net.merchantpug.apugli.Apugli;
import net.merchantpug.apugli.platform.Services;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import org.joml.Math;
import virtuoel.pehkui.api.ScaleData;
import virtuoel.pehkui.api.ScaleRegistries;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class DelayedApoliScaleModifier<P> extends ApoliScaleModifier<P> {
    private int ticks;
    private final int baseMaxTicks;
    private int maxTicks;

    protected final List<?> delayModifiers;
    private Map<ResourceLocation, Double> previousResourceValues = new HashMap<>();
    private Map<Integer, Map<ResourceLocation, Double>> inBetweenResourceValues = new HashMap<>();
    private Map<ResourceLocation, Double> targetResourceValues = new HashMap<>();

    protected final Optional<ResourceLocation> easing;
    protected final Map<ResourceLocation, Float> cachedPreviousScales = new HashMap<>();
    protected final Map<ResourceLocation, Float> cachedPreviousPreviousScales = new HashMap<>();
    private final Map<ResourceLocation, Map<Integer, Map<Float, Float>>> reachedScales = new HashMap<>();
    private final Map<ResourceLocation, Map<Integer, Float>> reachedPreviousScales = new HashMap<>();
    private final ResourceLocation tickUpdateScaleTypeId;
    private float tickUpdatePrevious;
    private float tickUpdateTarget;

    public DelayedApoliScaleModifier(P power, LivingEntity entity, List<?> modifiers, List<?> delayModifiers, int maxTicks, Set<ResourceLocation> cachedScaleIds, int powerPriority, Optional<ResourceLocation> easing) {
        super(power, entity, modifiers, cachedScaleIds, powerPriority);
        this.delayModifiers = ImmutableList.copyOf(delayModifiers);
        this.baseMaxTicks = (int) Services.PLATFORM.applyModifiers(this.owner, this.delayModifiers, maxTicks);
        this.ticks = 0;
        this.maxTicks = maxTicks;
        this.easing = easing;
        this.tickUpdateScaleTypeId = this.getCachedScaleIds().stream().findFirst().orElseThrow();
        if (entity != null) {
            this.tickUpdateTarget = !Services.POWER.isActive(this.power, entity) ? PehkuiUtil.getScaleType(this.tickUpdateScaleTypeId).getScaleData(entity).getBaseScale() : (float) Services.PLATFORM.applyModifiers(entity, this.modifiers, PehkuiUtil.getScaleType(this.tickUpdateScaleTypeId).getScaleData(entity).getBaseScale());
            this.tickUpdatePrevious = PehkuiUtil.getScaleType(this.tickUpdateScaleTypeId).getScaleData(entity).getBaseScale();
        }
    }

    public int getMaxTicks() {
        return this.maxTicks;
    }

    public int getClampedTicks() {
        return Mth.clamp(this.ticks, 0, this.getMaxTicks());
    }

    public void setTicks(int value) {
        this.ticks = Mth.clamp(value, 0, this.getMaxTicks());
    }

    protected boolean isMax() {
        return this.ticks >= this.getMaxTicks();
    }

    protected boolean isMin() {
        return this.ticks <= 0;
    }

    @Override
    public CompoundTag serialize(CompoundTag tag) {
        tag.putInt("MaxTicks", this.getMaxTicks());
        tag.putInt("Ticks", this.getClampedTicks());
        super.serialize(tag);
        if (!this.cachedPreviousScales.isEmpty()) {
            ListTag cachedPreviousScalesTag = new ListTag();
            for (Map.Entry<ResourceLocation, Float> entry : this.cachedPreviousScales.entrySet()) {
                CompoundTag entryTag = new CompoundTag();
                entryTag.putString("Type", entry.getKey().toString());
                entryTag.putFloat("Value", entry.getValue());
                cachedPreviousScalesTag.add(entryTag);
            }
            tag.put("PreviousScales", cachedPreviousScalesTag);
        }
        if (!this.cachedPreviousPreviousScales.isEmpty()) {
            ListTag cachedPreviousPreviousScalesTag = new ListTag();
            for (Map.Entry<ResourceLocation, Float> entry : this.cachedPreviousPreviousScales.entrySet()) {
                CompoundTag entryTag = new CompoundTag();
                entryTag.putString("Type", entry.getKey().toString());
                entryTag.putFloat("Value", entry.getValue());
                cachedPreviousPreviousScalesTag.add(entryTag);
            }
            tag.put("PreviousPreviousScales", cachedPreviousPreviousScalesTag);
        }
        if (!this.targetResourceValues.isEmpty()) {
            ListTag targetResourceValuesTag = new ListTag();
            for (Map.Entry<ResourceLocation, Double> entry : this.targetResourceValues.entrySet()) {
                CompoundTag entryTag = new CompoundTag();
                entryTag.putString("Resource", entry.getKey().toString());
                entryTag.putDouble("Value", entry.getValue());
                targetResourceValuesTag.add(entryTag);
            }
            tag.put("TargetResourceValues", targetResourceValuesTag);
        }
        if (!this.previousResourceValues.isEmpty()) {
            ListTag previousResourceValuesTag = new ListTag();
            for (Map.Entry<ResourceLocation, Double> entry : this.previousResourceValues.entrySet()) {
                CompoundTag entryTag = new CompoundTag();
                entryTag.putString("Resource", entry.getKey().toString());
                entryTag.putDouble("Value", entry.getValue());
                previousResourceValuesTag.add(entryTag);
            }
            tag.put("PreviousResourceValues", previousResourceValuesTag);
        }
        if (!this.inBetweenResourceValues.isEmpty()) {
            ListTag previousResourceValuesTag = new ListTag();
            for (Map.Entry<Integer, Map<ResourceLocation, Double>> entry : this.inBetweenResourceValues.entrySet()) {
                if (entry.getValue().isEmpty()) {
                    continue;
                }
                CompoundTag entryTag = new CompoundTag();
                ListTag valuesTag = new ListTag();

                for (Map.Entry<ResourceLocation, Double> innerEntry : entry.getValue().entrySet()) {
                    CompoundTag innerEntryTag = new CompoundTag();
                    innerEntryTag.putString("Resource", innerEntry.getKey().toString());
                    innerEntryTag.putDouble("Value", innerEntry.getValue());
                    valuesTag.add(innerEntryTag);
                }

                entryTag.putString("Ticks", entry.getKey().toString());
                entryTag.put("Values", valuesTag);
                previousResourceValuesTag.add(entryTag);
            }
            tag.put("InBetweenResourceValues", previousResourceValuesTag);
        }
        return tag;
    }

    @Override
    public void deserialize(CompoundTag tag, boolean initialize) {
        this.maxTicks = tag.getInt("MaxTicks");
        this.setTicks(tag.getInt("Ticks"));
        super.deserialize(tag, false);
        this.cachedPreviousScales.clear();
        if (tag.contains("PreviousScales", Tag.TAG_LIST)) {
            ListTag listTag = tag.getList("PreviousScales", Tag.TAG_COMPOUND);
            for (int i = 0; i < listTag.size(); ++i) {
                CompoundTag entryTag = listTag.getCompound(i);
                this.cachedPreviousScales.put(new ResourceLocation(entryTag.getString("Type")), entryTag.getFloat("Value"));
            }
        }
        this.cachedPreviousPreviousScales.clear();
        if (tag.contains("PreviousPreviousScales", Tag.TAG_LIST)) {
            ListTag listTag = tag.getList("PreviousPreviousScales", Tag.TAG_COMPOUND);
            for (int i = 0; i < listTag.size(); ++i) {
                CompoundTag entryTag = listTag.getCompound(i);
                this.cachedPreviousPreviousScales.put(new ResourceLocation(entryTag.getString("Type")), entryTag.getFloat("Value"));
            }
        }
        this.targetResourceValues.clear();
        if (tag.contains("TargetResourceValues", Tag.TAG_LIST)) {
            ListTag listTag = tag.getList("TargetResourceValues", Tag.TAG_COMPOUND);
            for (int i = 0; i < listTag.size(); ++i) {
                CompoundTag entryTag = listTag.getCompound(i);
                this.targetResourceValues.put(new ResourceLocation(entryTag.getString("Resource")), entryTag.getDouble("Value"));
            }
        }
        this.previousResourceValues.clear();
        if (tag.contains("PreviousResourceValues", Tag.TAG_LIST)) {
            ListTag listTag = tag.getList("PreviousResourceValues", Tag.TAG_COMPOUND);
            for (int i = 0; i < listTag.size(); ++i) {
                CompoundTag entryTag = listTag.getCompound(i);
                this.previousResourceValues.put(new ResourceLocation(entryTag.getString("Resource")), entryTag.getDouble("Value"));
            }
        }
        this.inBetweenResourceValues.clear();
        if (tag.contains("InBetweenResourceValues", Tag.TAG_LIST)) {
            ListTag listTag = tag.getList("InBetweenResourceValues", Tag.TAG_COMPOUND);
            for (int i = 0; i < listTag.size(); ++i) {
                CompoundTag entryTag = listTag.getCompound(i);

                Map<ResourceLocation, Double> values = new HashMap<>();
                ListTag valuesTag = entryTag.getList("Values", Tag.TAG_COMPOUND);

                for (int j = 0; j < valuesTag.size(); ++j) {
                    CompoundTag innerTag = listTag.getCompound(j);
                    values.put(new ResourceLocation(entryTag.getString("Resource")), entryTag.getDouble("Value"));
                }
                this.inBetweenResourceValues.put(entryTag.getInt("Ticks"), values);
            }
        }
    }

    @Override
    protected void reset() {
        super.reset();
        this.cachedPreviousScales.clear();
        this.cachedPreviousPreviousScales.clear();
        this.reachedScales.clear();
        this.reachedPreviousScales.clear();
        this.targetResourceValues.clear();
        this.previousResourceValues.clear();
        this.inBetweenResourceValues.clear();
    }

    @Override
    public void tick(LivingEntity entity) {
        boolean updateMaxTicks = false;
        boolean isActive = Services.POWER.isActive(power, entity);

        for (ResourceLocation typeId : this.cachedScaleIds) {
            ScaleData data = ScaleRegistries.getEntry(ScaleRegistries.SCALE_TYPES, typeId).getScaleData(entity);

            float value = !isActive ? data.getBaseScale() : (float) Services.PLATFORM.applyModifiers(entity, this.modifiers, data.getBaseScale());

            if (!compareFloats(this.checkScales.getOrDefault(typeId, data.getBaseScale()), value)) {
                updateMaxTicks = true;

                this.checkScales.put(typeId, value);
                this.markForUpdating(typeId, false);
            }
        }

        if (updateMaxTicks) {
            Map<ResourceLocation, Double> newPrevious = this.targetResourceValues.isEmpty() ? Services.POWER.getClosestToBaseScale(entity, this.modifiers, 1.0F) : this.ticks == this.maxTicks ? this.targetResourceValues : this.inBetweenResourceValues.entrySet().stream().filter(entry -> entry.getKey() < this.ticks).max(Comparator.comparing(entry -> Mth.abs(entry.getKey() - this.ticks))).map(Map.Entry::getValue).orElse(new HashMap<>());

            this.targetResourceValues = Services.POWER.iterateThroughModifierForResources(entity, this.modifiers);
            this.inBetweenResourceValues = Services.POWER.getInBetweenResources(entity, this.modifiers, this.delayModifiers, this.baseMaxTicks, this.previousResourceValues);
            this.previousResourceValues = newPrevious;

            this.maxTicks = (int) Services.POWER.addAllInBetweensOfResourceModifiers(entity, this.modifiers, this.delayModifiers, this.baseMaxTicks, this.previousResourceValues);

            ScaleData data = PehkuiUtil.getScaleType(this.tickUpdateScaleTypeId).getScaleData(entity);

            float currentScale = this.isMin() ? this.tickUpdatePrevious : this.isMax() ? this.tickUpdateTarget : calculateScale(data, this.tickUpdateScaleTypeId, 1.0F, this.tickUpdateTarget, this.tickUpdatePrevious);
            this.tickUpdatePrevious = this.tickUpdateTarget;
            this.tickUpdateTarget = !isActive ? data.getBaseScale() : (float) Services.PLATFORM.applyModifiers(entity, this.modifiers, data.getBaseScale());

            float slope = ((float) this.getMaxTicks()) / (this.tickUpdateTarget - this.tickUpdatePrevious);
            this.setTicks(Math.round(slope * (currentScale - this.tickUpdatePrevious)));

            this.shouldUpdate = true;
            this.shouldUpdatePrevious = true;
            Services.POWER.syncPower(entity, this.power);
            this.updateOthers(entity);
            return;
        }

        int modifiedDelay = (int) Services.POWER.addAllInBetweensOfResourceModifiers(entity, this.modifiers, this.delayModifiers, this.baseMaxTicks, this.previousResourceValues);

        if (this.maxTicks != modifiedDelay) {
            int inbetween = modifiedDelay - this.maxTicks;
            this.ticks += inbetween;
            this.maxTicks = this.maxTicks + inbetween;
            Services.POWER.syncPower(entity, this.power);
        }

        if (this.getClampedTicks() < this.getMaxTicks()) {
            this.setTicks(this.getClampedTicks() + 1);
            this.shouldUpdate = true;
            this.shouldUpdatePrevious = true;

            Services.POWER.syncPower(entity, this.power);

            this.updateOthers(entity);
        }
    }

    @Override
    protected void markForUpdating(ResourceLocation typeId, boolean notOriginalCall) {
        super.markForUpdating(typeId, notOriginalCall);
        if (notOriginalCall) {
            this.cachedPreviousScales.clear();
            this.cachedPreviousPreviousScales.clear();
        }
    }

    @Override
    public float modifyScale(final ScaleData scaleData, final float modifiedScale, final float delta) {
        if (!(scaleData.getEntity() instanceof LivingEntity entity)) {
            logWarn();
            return modifiedScale;
        }

        ResourceLocation scaleTypeId = getResourceLocationFromScaleData(scaleData);
        boolean isActive = Services.POWER.isActive(power, entity);

        if (this.shouldUpdateModifiers.contains(scaleTypeId)) {
            float target = !isActive ? modifiedScale : (float) Services.PLATFORM.applyModifiers(entity, this.modifiers, modifiedScale);
            float previous = this.cachedTargetScales.getOrDefault(scaleTypeId, modifiedScale);

            this.cachedTargetScales.put(scaleTypeId, target);
            this.cachedPreviousScales.put(scaleTypeId, previous);

            this.shouldUpdateModifiers.remove(scaleTypeId);
            this.reachedScales.remove(scaleTypeId);
        }

        // Null-safe return.
        if (!this.cachedTargetScales.containsKey(scaleTypeId) || !this.cachedPreviousScales.containsKey(scaleTypeId)) {
            return modifiedScale;
        }

        if (this.isMax()) {
            this.reachedScales.remove(scaleTypeId);
            return this.cachedTargetScales.get(scaleTypeId);
        } else if (this.isMin()) {
            return this.cachedPreviousScales.get(scaleTypeId);
        } else if (this.reachedScales.containsKey(scaleTypeId) && this.reachedScales.get(scaleTypeId).containsKey(this.ticks) && this.reachedScales.get(scaleTypeId).get(this.ticks).keySet().stream().anyMatch(aFloat -> compareFloats(aFloat, 1.0F))) {
            return this.reachedScales.get(scaleTypeId).get(this.ticks).entrySet().stream().filter(entry -> compareFloats(entry.getKey(), 1.0F)).findFirst().get().getValue();
        } else if (this.reachedScales.containsKey(scaleTypeId) && this.reachedScales.get(scaleTypeId).containsKey(this.ticks) && this.reachedScales.get(scaleTypeId).get(this.ticks).keySet().stream().anyMatch(aFloat -> compareFloats(aFloat, delta))) {
            return this.reachedScales.get(scaleTypeId).get(this.ticks).entrySet().stream().filter(entry -> compareFloats(entry.getKey(), delta)).findFirst().get().getValue();
        }

        return calculateScale(scaleData, scaleTypeId, delta,
                this.cachedTargetScales.get(scaleTypeId),
                this.cachedPreviousScales.get(scaleTypeId));
    }

    private float calculateScale(ScaleData scaleData, ResourceLocation scaleTypeId, float delta, float targetScale, float previousScale) {
        Float2FloatFunction easing = this.easing.map(location -> {
            if (ScaleRegistries.SCALE_EASINGS.containsKey(location)) {
                return ScaleRegistries.getEntry(ScaleRegistries.SCALE_EASINGS, location);
            }
            Apugli.LOG.error("'easing' value '{}' for power '{}' is not a valid scale easing.", location, Services.POWER.getPowerId(this.power));
            return null;
        }).orElse(Optional.ofNullable(scaleData.getEasing()).orElseGet(scaleData.getScaleType()::getDefaultEasing));

        float progress = (float) this.getClampedTicks() + delta;
        int total = this.getMaxTicks();
        float range = targetScale - previousScale;
        float perTick = total == 0 ? 1.0F : (easing.apply(progress / total));

        float modified = (previousScale + (perTick * range));

        this.populateDeltaReachedScale(scaleTypeId, this.getClampedTicks(), delta, modified);

        return modified;
    }

    private void populateDeltaReachedScale(ResourceLocation scaleTypeId, int ticks, float delta, float modified) {
        if (!this.reachedScales.containsKey(scaleTypeId))
            this.reachedScales.put(scaleTypeId, new HashMap<>());

        if (!this.reachedScales.get(scaleTypeId).containsKey(ticks))
            this.reachedScales.get(scaleTypeId).put(ticks, new HashMap<>());

        if (this.reachedScales.containsKey(scaleTypeId) && this.reachedScales.getOrDefault(scaleTypeId, new HashMap<>()).containsKey(ticks))
            this.reachedScales.get(scaleTypeId).get(ticks).put(delta, modified);
    }

    @Override
    public float modifyPrevScale(final ScaleData scaleData, final float modifiedScale) {
        if (!(scaleData.getEntity() instanceof LivingEntity entity)) {
            logWarn();
            return modifiedScale;
        }

        ResourceLocation scaleTypeId = getResourceLocationFromScaleData(scaleData);
        boolean isActive = Services.POWER.isActive(power, entity);

        if (this.shouldUpdatePreviousModifiers.contains(scaleTypeId)) {
            float target = !isActive ? modifiedScale : (float) Services.PLATFORM.applyModifiers(entity, this.modifiers, modifiedScale);
            float previous = this.cachedPreviousTargetScales.getOrDefault(scaleTypeId, modifiedScale);

            this.cachedPreviousTargetScales.put(scaleTypeId, target);
            this.cachedPreviousPreviousScales.put(scaleTypeId, previous);

            this.shouldUpdatePreviousModifiers.remove(scaleTypeId);
            this.reachedPreviousScales.remove(scaleTypeId);
        }

        // Null-safe return.
        if (!this.cachedPreviousTargetScales.containsKey(scaleTypeId) || !this.cachedPreviousPreviousScales.containsKey(scaleTypeId)) {
            return modifiedScale;
        }

        if (this.isMax()) {
            return this.cachedPreviousTargetScales.get(scaleTypeId);
        } else if (this.isMin()) {
            return this.cachedPreviousPreviousScales.get(scaleTypeId);
        } else if (this.reachedPreviousScales.containsKey(scaleTypeId) && this.reachedPreviousScales.getOrDefault(scaleTypeId, new HashMap<>()).containsKey(this.ticks)) {
            return this.reachedPreviousScales.get(scaleTypeId).get(this.ticks);
        }

        return calculatePreviousScale(scaleTypeId);
    }

    private float calculatePreviousScale(ResourceLocation scaleTypeId) {
        float targetScale = this.cachedPreviousTargetScales.get(scaleTypeId);
        float previousScale = this.cachedPreviousPreviousScales.get(scaleTypeId);

        float modified = Mth.lerp((float) this.getClampedTicks() / this.getMaxTicks(), previousScale, targetScale);

        this.populateReachedPreviousScale(scaleTypeId, this.ticks, modified);

        return modified;
    }

    private void populateReachedPreviousScale(ResourceLocation scaleTypeId, int ticks, float modified) {
        if (!this.reachedPreviousScales.containsKey(scaleTypeId)) {
            this.reachedPreviousScales.put(scaleTypeId, new HashMap<>());
        }
        if (this.reachedPreviousScales.containsKey(scaleTypeId))
            this.reachedPreviousScales.get(scaleTypeId).put(ticks, modified);
    }

}
