package net.merchantpug.apugli.integration.pehkui;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.floats.Float2FloatFunction;
import net.merchantpug.apugli.Apugli;
import net.merchantpug.apugli.platform.Services;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import org.joml.Math;
import virtuoel.pehkui.api.ScaleData;
import virtuoel.pehkui.api.ScaleRegistries;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
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
    private Map<Integer, Map<ResourceLocation, Double>> inBetweenDelayValues = new HashMap<>();
    private Map<ResourceLocation, Double> targetResourceValues = new HashMap<>();
    private float progress;

    protected final Optional<ResourceLocation> easing;
    protected final Map<ResourceLocation, Float> cachedMinScales = new HashMap<>();
    protected final Map<ResourceLocation, Float> cachedPreviousMinScales = new HashMap<>();
    protected final Map<ResourceLocation, Float> checkMaxScales = new HashMap<>();
    protected final Map<ResourceLocation, Float> checkMinScales = new HashMap<>();
    private final Map<ResourceLocation, Map<Integer, Map<Float, Float>>> deltaReachedScales = new HashMap<>();
    private final Map<ResourceLocation, Map<Integer, Float>> reachedPreviousScales = new HashMap<>();
    private final List<Float> valueList = new ArrayList<>();
    private final List<Integer> timeBetweenScales = new ArrayList<>();
    private int tickTarget = 0;
    private final Set<ResourceLocation> minScalesToUpdate = new HashSet<>();
    private final Set<ResourceLocation> previousMinScalesToUpdate = new HashSet<>();
    private final Set<ResourceLocation> maxScalesToUpdate = new HashSet<>();
    private final Set<ResourceLocation> previousMaxScalesToUpdate = new HashSet<>();
    private final Set<ResourceLocation> tickSettingPrevention = new HashSet<>();
    private final Set<ResourceLocation> oppositesToUpdate = new HashSet<>();
    private final Set<ResourceLocation> previousOppositesToUpdate = new HashSet<>();
    private boolean shouldUpdateOthers = false;

    public DelayedApoliScaleModifier(P power, LivingEntity entity, List<?> modifiers, List<?> delayModifiers, int maxTicks, Set<ResourceLocation> cachedScaleIds, int powerPriority, Optional<ResourceLocation> easing) {
        super(power, entity, modifiers, cachedScaleIds, powerPriority);
        this.delayModifiers = ImmutableList.copyOf(delayModifiers);
        this.baseMaxTicks = (int) Services.PLATFORM.applyModifiers(this.owner, this.delayModifiers, maxTicks);
        this.ticks = 0;
        this.maxTicks = maxTicks;
        this.easing = easing;
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
        CompoundTag baseTag = super.serialize(tag);
        baseTag.putInt("TickTarget", this.tickTarget);
        baseTag.putInt("MaxTicks", this.getMaxTicks());
        baseTag.putInt("Ticks", this.getClampedTicks());
        if (!this.cachedMinScales.isEmpty()) {
            ListTag listTag = new ListTag();
            for (Map.Entry<ResourceLocation, Float> entry : this.cachedMinScales.entrySet()) {
                CompoundTag entryTag = new CompoundTag();
                entryTag.putString("Type", entry.getKey().toString());
                entryTag.putFloat("Value", entry.getValue());
                listTag.add(entryTag);
            }
            baseTag.put("MinScales", listTag);
        }
        if (!this.cachedMinScales.isEmpty()) {
            ListTag listTag = new ListTag();
            for (Map.Entry<ResourceLocation, Float> entry : this.cachedMinScales.entrySet()) {
                CompoundTag entryTag = new CompoundTag();
                entryTag.putString("Type", entry.getKey().toString());
                entryTag.putFloat("Value", entry.getValue());
                listTag.add(entryTag);
            }
            baseTag.put("MinScales", listTag);
        }
        if (!this.cachedPreviousMinScales.isEmpty()) {
            ListTag listTag = new ListTag();
            for (Map.Entry<ResourceLocation, Float> entry : this.cachedPreviousMinScales.entrySet()) {
                CompoundTag entryTag = new CompoundTag();
                entryTag.putString("Type", entry.getKey().toString());
                entryTag.putFloat("Value", entry.getValue());
                listTag.add(entryTag);
            }
            baseTag.put("PreviousMinScales", listTag);
        }
        if (!this.checkMaxScales.isEmpty()) {
            ListTag listTag = new ListTag();
            for (Map.Entry<ResourceLocation, Float> entry : this.checkMaxScales.entrySet()) {
                CompoundTag entryTag = new CompoundTag();
                entryTag.putString("Type", entry.getKey().toString());
                entryTag.putFloat("Value", entry.getValue());
                listTag.add(entryTag);
            }
            baseTag.put("CheckMaxScales", listTag);
        }
        if (!this.checkMinScales.isEmpty()) {
            ListTag listTag = new ListTag();
            for (Map.Entry<ResourceLocation, Float> entry : this.checkMinScales.entrySet()) {
                CompoundTag entryTag = new CompoundTag();
                entryTag.putString("Type", entry.getKey().toString());
                entryTag.putFloat("Value", entry.getValue());
                listTag.add(entryTag);
            }
            baseTag.put("CheckMinScales", listTag);
        }
        if (!this.checkScales.isEmpty()) {
            ListTag listTag = new ListTag();
            for (Map.Entry<ResourceLocation, Float> entry : this.checkScales.entrySet()) {
                CompoundTag entryTag = new CompoundTag();
                entryTag.putString("Type", entry.getKey().toString());
                entryTag.putFloat("Value", entry.getValue());
                listTag.add(entryTag);
            }
            baseTag.put("CheckScales", listTag);
        }
        if (!this.minScalesToUpdate.isEmpty()) {
            ListTag listTag = new ListTag();
            for (ResourceLocation entry : this.minScalesToUpdate) {
                listTag.add(StringTag.valueOf(entry.toString()));
            }
            tag.put("MinScalesToUpdate", listTag);
        }
        if (!this.previousMinScalesToUpdate.isEmpty()) {
            ListTag listTag = new ListTag();
            for (ResourceLocation entry : this.previousMinScalesToUpdate) {
                listTag.add(StringTag.valueOf(entry.toString()));
            }
            tag.put("PreviousMinScalesToUpdate", listTag);
        }
        if (!this.maxScalesToUpdate.isEmpty()) {
            ListTag listTag = new ListTag();
            for (ResourceLocation entry : this.maxScalesToUpdate) {
                listTag.add(StringTag.valueOf(entry.toString()));
            }
            tag.put("MaxScalesToUpdate", listTag);
        }
        if (!this.previousMaxScalesToUpdate.isEmpty()) {
            ListTag listTag = new ListTag();
            for (ResourceLocation entry : this.previousMaxScalesToUpdate) {
                listTag.add(StringTag.valueOf(entry.toString()));
            }
            tag.put("PreviousMaxScalesToUpdate", listTag);
        }
        if (!this.oppositesToUpdate.isEmpty()) {
            ListTag listTag = new ListTag();
            for (ResourceLocation entry : this.oppositesToUpdate) {
                listTag.add(StringTag.valueOf(entry.toString()));
            }
            tag.put("OppositesToUpdate", listTag);
        }
        if (!this.previousOppositesToUpdate.isEmpty()) {
            ListTag listTag = new ListTag();
            for (ResourceLocation entry : this.previousOppositesToUpdate) {
                listTag.add(StringTag.valueOf(entry.toString()));
            }
            tag.put("PreviousOppositesToUpdate", listTag);
        }
        tag.putFloat("Progress", this.progress);
        tag.putBoolean("ShouldUpdateOthers", this.shouldUpdateOthers);
        return baseTag;
    }

    @Override
    public void deserialize(CompoundTag tag, boolean initialize) {
        super.deserialize(tag, false);
        this.tickTarget = tag.getInt("TickTarget");
        this.maxTicks = tag.getInt("MaxTicks");
        this.setTicks(tag.getInt("Ticks"));
        this.cachedMinScales.clear();
        if (tag.contains("MinScales", Tag.TAG_LIST)) {
            ListTag listTag = tag.getList("MinScales", Tag.TAG_COMPOUND);
            for (int i = 0; i < listTag.size(); ++i) {
                CompoundTag entryTag = listTag.getCompound(i);
                this.cachedMinScales.put(new ResourceLocation(entryTag.getString("Type")), entryTag.getFloat("Value"));
            }
        }
        this.cachedPreviousMinScales.clear();
        if (tag.contains("PreviousMinScales", Tag.TAG_LIST)) {
            ListTag listTag = tag.getList("PreviousMinScales", Tag.TAG_COMPOUND);
            for (int i = 0; i < listTag.size(); ++i) {
                CompoundTag entryTag = listTag.getCompound(i);
                this.cachedPreviousMinScales.put(new ResourceLocation(entryTag.getString("Type")), entryTag.getFloat("Value"));
            }
        }
        this.checkMaxScales.clear();
        if (tag.contains("CheckMaxScales", Tag.TAG_LIST)) {
            ListTag listTag = tag.getList("CheckMaxScales", Tag.TAG_COMPOUND);
            for (int i = 0; i < listTag.size(); ++i) {
                CompoundTag entryTag = listTag.getCompound(i);
                this.checkMaxScales.put(new ResourceLocation(entryTag.getString("Type")), entryTag.getFloat("Value"));
            }
        }
        this.checkMinScales.clear();
        if (tag.contains("CheckMinScales", Tag.TAG_LIST)) {
            ListTag listTag = tag.getList("CheckMinScales", Tag.TAG_COMPOUND);
            for (int i = 0; i < listTag.size(); ++i) {
                CompoundTag entryTag = listTag.getCompound(i);
                this.checkMinScales.put(new ResourceLocation(entryTag.getString("Type")), entryTag.getFloat("Value"));
            }
        }
        this.checkScales.clear();
        if (tag.contains("CheckScales", Tag.TAG_LIST)) {
            ListTag listTag = tag.getList("CheckScales", Tag.TAG_COMPOUND);
            for (int i = 0; i < listTag.size(); ++i) {
                CompoundTag entryTag = listTag.getCompound(i);
                this.checkScales.put(new ResourceLocation(entryTag.getString("Type")), entryTag.getFloat("Value"));
            }
        }
        this.minScalesToUpdate.clear();
        if (tag.contains("MinScalesToUpdate", Tag.TAG_LIST)) {
            ListTag listTag = tag.getList("MinScalesToUpdate", Tag.TAG_STRING);
            for (int i = 0; i < listTag.size(); ++i) {
                this.minScalesToUpdate.add(new ResourceLocation(listTag.getString(i)));
            }
        }
        this.minScalesToUpdate.clear();
        if (tag.contains("MinScalesToUpdate", Tag.TAG_LIST)) {
            ListTag listTag = tag.getList("MinScalesToUpdate", Tag.TAG_STRING);
            for (int i = 0; i < listTag.size(); ++i) {
                this.minScalesToUpdate.add(new ResourceLocation(listTag.getString(i)));
            }
        }
        this.previousMinScalesToUpdate.clear();
        if (tag.contains("PreviousMinScalesToUpdate", Tag.TAG_LIST)) {
            ListTag listTag = tag.getList("PreviousMinScalesToUpdate", Tag.TAG_STRING);
            for (int i = 0; i < listTag.size(); ++i) {
                this.previousMinScalesToUpdate.add(new ResourceLocation(listTag.getString(i)));
            }
        }
        this.maxScalesToUpdate.clear();
        if (tag.contains("MaxScalesToUpdate", Tag.TAG_LIST)) {
            ListTag listTag = tag.getList("MaxScalesToUpdate", Tag.TAG_STRING);
            for (int i = 0; i < listTag.size(); ++i) {
                this.maxScalesToUpdate.add(new ResourceLocation(listTag.getString(i)));
            }
        }
        this.previousMaxScalesToUpdate.clear();
        if (tag.contains("PreviousMaxScalesToUpdate", Tag.TAG_LIST)) {
            ListTag listTag = tag.getList("PreviousMaxScalesToUpdate", Tag.TAG_STRING);
            for (int i = 0; i < listTag.size(); ++i) {
                this.previousMaxScalesToUpdate.add(new ResourceLocation(listTag.getString(i)));
            }
        }
        this.oppositesToUpdate.clear();
        if (tag.contains("OppositesToUpdate", Tag.TAG_LIST)) {
            ListTag listTag = tag.getList("OppositesToUpdate", Tag.TAG_STRING);
            for (int i = 0; i < listTag.size(); ++i) {
                this.oppositesToUpdate.add(new ResourceLocation(listTag.getString(i)));
            }
        }
        this.previousOppositesToUpdate.clear();
        if (tag.contains("PreviousOppositesToUpdate", Tag.TAG_LIST)) {
            ListTag listTag = tag.getList("PreviousOppositesToUpdate", Tag.TAG_STRING);
            for (int i = 0; i < listTag.size(); ++i) {
                this.previousOppositesToUpdate.add(new ResourceLocation(listTag.getString(i)));
            }
        }
        this.progress = tag.getFloat("Progress");
        this.shouldUpdateOthers = tag.getBoolean("ShouldUpdateOthers");
        this.initialized = initialize;
    }

    @Override
    public void tick(LivingEntity entity) {
        if (this.initialized) {
            boolean isActive = Services.POWER.isActive(power, entity);

            for (ResourceLocation typeId : this.cachedScaleIds) {
                ScaleData data = ScaleRegistries.getEntry(ScaleRegistries.SCALE_TYPES, typeId).getScaleData(entity);

                float value = !isActive ? data.getBaseScale() : (float) Services.PLATFORM.applyModifiers(entity, this.modifiers, data.getBaseScale());
                if (!this.checkScales.containsKey(typeId) || !compareFloats(this.checkScales.getOrDefault(typeId, data.getBaseScale()), value)) {
                    float min = !this.checkMinScales.containsKey(typeId) ? Math.min(value, data.getBaseScale()) : value;
                    float max = !this.checkMaxScales.containsKey(typeId) ? Math.max(value, data.getBaseScale()) : value;
                    if (!this.checkMinScales.containsKey(typeId) || value < this.checkScales.getOrDefault(typeId, value)) {
                        Map<ResourceLocation, Double> newPrevious = this.targetResourceValues.isEmpty() ? Services.POWER.getClosestToBaseScale(entity, this.modifiers, data.getBaseScale()) : this.ticks == this.tickTarget ? this.targetResourceValues : this.inBetweenDelayValues.entrySet().stream().filter(entry -> entry.getKey() <= ticks).map(Map.Entry::getValue).findFirst().orElse(Map.of());;
                        this.targetResourceValues = Services.POWER.iterateThroughModifierForResources(entity, this.modifiers);
                        this.previousResourceValues = newPrevious;
                        int modifiedDelay = (int) Services.POWER.addAllInBetweensOfResourceModifiers(entity, this.modifiers, this.delayModifiers, this.baseMaxTicks, this.previousResourceValues);
                        this.inBetweenDelayValues = Services.POWER.getResourcesForEachTickValue(entity, this.modifiers, this.baseMaxTicks, this.previousResourceValues);

                        if (this.valueList.stream().noneMatch(f -> compareFloats(f, min))) {
                            this.valueList.add(0, min);
                            this.timeBetweenScales.add(modifiedDelay);
                        }

                        this.maxTicks = modifiedDelay;
                        this.tickTarget = 0;

                        this.oppositesToUpdate.add(typeId);
                        this.previousOppositesToUpdate.add(typeId);
                        this.checkMinScales.put(typeId, min);
                        this.minScalesToUpdate.add(typeId);
                        this.previousMinScalesToUpdate.add(typeId);
                    }
                    if (!this.checkMaxScales.containsKey(typeId) || value > this.checkScales.getOrDefault(typeId, value)) {
                        Map<ResourceLocation, Double> newPrevious = this.targetResourceValues.isEmpty() ? Services.POWER.getClosestToBaseScale(entity, this.modifiers, data.getBaseScale()) : this.ticks == this.tickTarget ? this.targetResourceValues : this.inBetweenDelayValues.entrySet().stream().filter(entry -> entry.getKey() <= ticks).map(Map.Entry::getValue).findFirst().orElse(Map.of());;
                        this.targetResourceValues = Services.POWER.iterateThroughModifierForResources(entity, this.modifiers);
                        this.previousResourceValues = newPrevious;
                        int modifiedDelay = (int) Services.POWER.addAllInBetweensOfResourceModifiers(entity, this.modifiers, this.delayModifiers, this.baseMaxTicks, this.previousResourceValues);
                        this.inBetweenDelayValues = Services.POWER.getResourcesForEachTickValue(entity, this.modifiers, this.baseMaxTicks, this.previousResourceValues);

                        if (this.valueList.stream().noneMatch(f -> compareFloats(f, max))) {
                            this.valueList.add(max);
                            this.timeBetweenScales.add(modifiedDelay);
                        }

                        this.maxTicks = modifiedDelay;
                        this.tickTarget = this.getMaxTicks();

                        this.oppositesToUpdate.add(typeId);
                        this.previousOppositesToUpdate.add(typeId);
                        this.checkMaxScales.put(typeId, max);
                        this.minScalesToUpdate.remove(typeId);
                        this.maxScalesToUpdate.add(typeId);
                        this.previousMinScalesToUpdate.remove(typeId);
                        this.previousMaxScalesToUpdate.add(typeId);
                    }
                    this.checkScales.put(typeId, value);
                    this.markForUpdating(typeId);
                    Services.POWER.syncPower(entity, this.power);
                    updateScale(entity, PehkuiUtil.getScaleType(typeId), true);
                }
                if (valueList.stream().anyMatch(f -> compareFloats(value, f))) {
                    int index = -1;
                    for (int i = 0; i < valueList.size(); ++i) {
                        if (compareFloats(valueList.get(i), value)) {
                            index = i;
                        }
                    }
                    if (index == -1) return;

                    int modifiedDelay = (int) Services.POWER.addAllInBetweensOfResourceModifiers(entity, this.modifiers, this.delayModifiers, this.baseMaxTicks, this.previousResourceValues);

                    if (this.timeBetweenScales.get(index) != modifiedDelay) {
                        int previousDelay = this.timeBetweenScales.get(index);
                        int inbetween = modifiedDelay - previousDelay;
                        this.maxTicks = modifiedDelay;
                        this.tickTarget = Math.round(this.tickTarget + inbetween);
                        this.timeBetweenScales.add(index, modifiedDelay);
                        this.markForUpdating(typeId);
                        Services.POWER.syncPower(entity, this.power);
                        updateScale(entity, PehkuiUtil.getScaleType(typeId), true);
                    }
                }
            }

            if (this.getClampedTicks() < this.getMaxTicks() && this.tickTarget > this.getClampedTicks()) {
                this.setTicks(Mth.clamp(this.getClampedTicks() + 1, 0, this.tickTarget));
                this.shouldUpdate = true;
                this.shouldUpdatePrevious = true;
                this.shouldUpdateOthers = false;
                this.valueList.clear();
                this.timeBetweenScales.clear();
                Services.POWER.syncPower(entity, this.power);
            } else if (this.tickTarget < this.getClampedTicks()) {
                this.setTicks(Mth.clamp(this.getClampedTicks() - 1, this.tickTarget, this.getMaxTicks()));
                this.shouldUpdate = true;
                this.shouldUpdatePrevious = true;
                this.shouldUpdateOthers = false;
                this.valueList.clear();
                this.timeBetweenScales.clear();
                Services.POWER.syncPower(entity, this.power);
            }

            if (this.getClampedTicks() == this.tickTarget) {
                this.valueList.clear();
                this.timeBetweenScales.clear();
            }
        }
    }

    public void markForUpdating(ResourceLocation typeId) {
        this.shouldUpdateModifiers.add(typeId);
        this.shouldUpdatePreviousModifiers.add(typeId);

        this.tickSettingPrevention.addAll(this.getCachedScaleIds());

        this.shouldUpdate = true;
        this.shouldUpdatePrevious = true;
    }

    @Override
    public void scheduleForUpdate(LivingEntity entity, boolean updateModifiers) {
        if (updateModifiers) {
            this.shouldUpdateModifiers.addAll(this.getCachedScaleIds());
            this.shouldUpdatePreviousModifiers.addAll(this.getCachedScaleIds());
            this.tickSettingPrevention.addAll(this.getCachedScaleIds());
            if (this.tickTarget < this.ticks || this.tickTarget == this.ticks && this.isMin()) {
                minScalesToUpdate.addAll(this.getCachedScaleIds());
                previousMinScalesToUpdate.addAll(this.getCachedScaleIds());
            }
            if (this.tickTarget > this.ticks || this.tickTarget == this.ticks && this.isMax()) {
                maxScalesToUpdate.addAll(this.getCachedScaleIds());
                previousMaxScalesToUpdate.addAll(this.getCachedScaleIds());
            }
        }
    }

    @Override
    protected void reset() {
        super.reset();
        this.ticks = 0;
        this.maxTicks = 0;
        this.cachedMinScales.clear();
        this.cachedPreviousMinScales.clear();
        this.deltaReachedScales.clear();
        this.reachedPreviousScales.clear();
        this.shouldUpdateModifiers.clear();
        this.shouldUpdatePreviousModifiers.clear();
        this.checkMinScales.clear();
        this.checkMaxScales.clear();
        this.tickSettingPrevention.clear();
        this.targetResourceValues.clear();
        this.previousResourceValues.clear();
    }

    public float modifyScale(final ScaleData scaleData, final float modifiedScale, final float delta) {
        ResourceLocation scaleTypeId = getResourceLocationFromScaleData(scaleData);

        if (this.shouldUpdateModifiers.contains(scaleTypeId)) {
            float currentScale = this.isMin() ? this.cachedMinScales.getOrDefault(scaleTypeId, modifiedScale) : this.isMax() ? this.cachedMaxScales.getOrDefault(scaleTypeId, modifiedScale) : this.deltaReachedScales.containsKey(scaleTypeId) && this.deltaReachedScales.get(scaleTypeId).containsKey(this.ticks) && this.deltaReachedScales.get(scaleTypeId).get(this.ticks).keySet().stream().max(Float::compareTo).isPresent() ? this.deltaReachedScales.get(scaleTypeId).get(this.ticks).entrySet().stream().max(Map.Entry.comparingByKey()).map(Map.Entry::getValue).orElse(modifiedScale) : modifiedScale;
            this.deltaReachedScales.remove(scaleTypeId);
            boolean isActive = Services.POWER.isActive(power, (LivingEntity) scaleData.getEntity());
            float appliedScale = !isActive ? modifiedScale : (float) Services.PLATFORM.applyModifiers(scaleData.getEntity(), this.modifiers, modifiedScale);
            float previousOppositeScale =  this.maxScalesToUpdate.contains(scaleTypeId) && !this.minScalesToUpdate.contains(scaleTypeId) || !isActive ? this.cachedMinScales.getOrDefault(scaleTypeId, modifiedScale) : !this.maxScalesToUpdate.contains(scaleTypeId) && this.minScalesToUpdate.contains(scaleTypeId) ? this.cachedMaxScales.getOrDefault(scaleTypeId, modifiedScale) : modifiedScale;
            float min = this.minScalesToUpdate.contains(scaleTypeId) ? appliedScale : previousOppositeScale;
            float max = this.maxScalesToUpdate.contains(scaleTypeId) ? appliedScale : previousOppositeScale;
            if (!this.cachedMinScales.containsKey(scaleTypeId) || this.minScalesToUpdate.contains(scaleTypeId)) {
                if (this.notOriginalCall.contains(scaleTypeId)) {
                    this.tickTarget = 0;
                }

                if (!this.maxScalesToUpdate.contains(scaleTypeId) && this.minScalesToUpdate.contains(scaleTypeId) && this.oppositesToUpdate.contains(scaleTypeId)) {
                    this.cachedMaxScales.put(scaleTypeId, previousOppositeScale);
                    this.oppositesToUpdate.remove(scaleTypeId);
                }

                this.cachedMinScales.put(scaleTypeId, min);
                this.minScalesToUpdate.remove(scaleTypeId);
            }
            if (!this.cachedMaxScales.containsKey(scaleTypeId) || this.maxScalesToUpdate.contains(scaleTypeId)) {
                if (this.notOriginalCall.contains(scaleTypeId)) {
                    this.cachedMinScales.put(scaleTypeId, currentScale);
                    this.tickTarget = this.getMaxTicks();
                }


                if (this.maxScalesToUpdate.contains(scaleTypeId) && !this.minScalesToUpdate.contains(scaleTypeId) && this.oppositesToUpdate.contains(scaleTypeId)) {
                    this.cachedMinScales.put(scaleTypeId, previousOppositeScale);
                    this.oppositesToUpdate.remove(scaleTypeId);
                }

                this.cachedMaxScales.put(scaleTypeId, max);
                this.maxScalesToUpdate.remove(scaleTypeId);
            }
            this.oppositesToUpdate.remove(scaleTypeId);
            if (this.tickSettingPrevention.size() == this.getCachedScaleIds().size()) {
                if (this.initialized) {
                    float slope = ((float)this.getMaxTicks()) / (this.cachedMaxScales.get(scaleTypeId) - this.cachedMinScales.get(scaleTypeId));
                    this.ticks = Math.round(slope * (currentScale - this.cachedMinScales.get(scaleTypeId)));
                }
                this.tickSettingPrevention.clear();
            }
            this.populateDeltaReachedScale(scaleTypeId, this.tickTarget, 1.0F, appliedScale);
            this.updateOthers((LivingEntity) scaleData.getEntity(), !this.notOriginalCall.contains(scaleTypeId));
            this.shouldUpdateModifiers.remove(scaleTypeId);
        }

        if (shouldUpdate) {
            this.updateOthers((LivingEntity) scaleData.getEntity(), !this.notOriginalCall.contains(scaleTypeId));
            this.shouldUpdate = false;
        }

        if (this.isMax()) {
            this.notOriginalCall.remove(scaleTypeId);
            return this.cachedMaxScales.getOrDefault(scaleTypeId, (float) Services.PLATFORM.applyModifiers(scaleData.getEntity(), this.modifiers, modifiedScale));
        } else if (this.isMin()) {
            this.notOriginalCall.remove(scaleTypeId);
            return this.cachedMinScales.getOrDefault(scaleTypeId, modifiedScale);
        } else if (this.deltaReachedScales.containsKey(scaleTypeId) && this.deltaReachedScales.get(scaleTypeId).containsKey(this.ticks) && this.deltaReachedScales.get(scaleTypeId).get(this.ticks).keySet().stream().anyMatch(aFloat -> compareFloats(aFloat, 1.0F))) {
            this.notOriginalCall.remove(scaleTypeId);
            return this.deltaReachedScales.get(scaleTypeId).get(this.ticks).getOrDefault(1.0F, modifiedScale);
        } else if (this.deltaReachedScales.containsKey(scaleTypeId) && this.deltaReachedScales.get(scaleTypeId).containsKey(this.ticks) && this.deltaReachedScales.get(scaleTypeId).get(this.ticks).keySet().stream().anyMatch(aFloat -> compareFloats(aFloat, delta))) {
            this.notOriginalCall.remove(scaleTypeId);
            return this.deltaReachedScales.get(scaleTypeId).get(this.ticks).getOrDefault(delta, modifiedScale);
        }

        Float2FloatFunction easing = this.easing.map(location -> {
            if (ScaleRegistries.SCALE_EASINGS.containsKey(location)) {
                return ScaleRegistries.getEntry(ScaleRegistries.SCALE_EASINGS, location);
            }
            Apugli.LOG.error("'easing' value '{}' for power '{}' is not a valid scale easing.", location, Services.POWER.getPowerId(this.power));
            return null;
        }).orElse(Optional.ofNullable(scaleData.getEasing()).orElseGet(scaleData.getScaleType()::getDefaultEasing));

        float maxScale = this.cachedMaxScales.getOrDefault(scaleTypeId, (float) Services.PLATFORM.applyModifiers(scaleData.getEntity(), this.modifiers, modifiedScale));
        float minScale = this.cachedMinScales.getOrDefault(scaleTypeId, modifiedScale);

        float progress = (float) this.getClampedTicks() + delta;
        int total = this.getMaxTicks();
        float range = maxScale - minScale;
        float perTick = total == 0 ? 1.0F : (easing.apply(progress / total));

        float modified = (minScale + (perTick * range));

        this.populateDeltaReachedScale(scaleTypeId, this.getClampedTicks(), delta, modified);
        this.updateOthers((LivingEntity) scaleData.getEntity(), !this.notOriginalCall.contains(scaleTypeId));

        this.notOriginalCall.remove(scaleTypeId);

        return modified;
    }

    private void populateDeltaReachedScale(ResourceLocation scaleTypeId, int ticks, float delta, float modified) {
        if (!this.deltaReachedScales.containsKey(scaleTypeId))
            this.deltaReachedScales.put(scaleTypeId, new HashMap<>());

        if (!this.deltaReachedScales.get(scaleTypeId).containsKey(ticks))
            this.deltaReachedScales.get(scaleTypeId).put(ticks, new HashMap<>());

        if (this.deltaReachedScales.containsKey(scaleTypeId) && this.deltaReachedScales.getOrDefault(scaleTypeId, new HashMap<>()).containsKey(ticks))
            this.deltaReachedScales.get(scaleTypeId).get(ticks).put(delta, modified);
    }

    public float modifyPrevScale(final ScaleData scaleData, final float modifiedScale) {
        ResourceLocation scaleTypeId = getResourceLocationFromScaleData(scaleData);

        if (this.shouldUpdatePreviousModifiers.contains(scaleTypeId)) {
            float currentScale = this.isMin() ? this.cachedPreviousMinScales.getOrDefault(scaleTypeId, modifiedScale) : this.isMax() ? this.cachedPreviousMaxScales.getOrDefault(scaleTypeId, modifiedScale) : this.reachedPreviousScales.containsKey(scaleTypeId) && this.reachedPreviousScales.get(scaleTypeId).containsKey(this.ticks) ? this.reachedPreviousScales.get(scaleTypeId).get(ticks) : modifiedScale;
            this.reachedPreviousScales.remove(scaleTypeId);
            boolean isActive = Services.POWER.isActive(power, (LivingEntity) scaleData.getEntity());
            float previousOppositeScale =  this.previousMaxScalesToUpdate.contains(scaleTypeId) && !this.previousMinScalesToUpdate.contains(scaleTypeId) || !isActive ? this.cachedPreviousMinScales.getOrDefault(scaleTypeId, modifiedScale) : !this.previousMaxScalesToUpdate.contains(scaleTypeId) && this.previousMinScalesToUpdate.contains(scaleTypeId) ? this.cachedPreviousMaxScales.getOrDefault(scaleTypeId, modifiedScale) : modifiedScale;
            float appliedScale = !isActive ? modifiedScale : (float) Services.PLATFORM.applyModifiers(scaleData.getEntity(), this.modifiers, modifiedScale);
            float min = this.previousMinScalesToUpdate.contains(scaleTypeId) ? appliedScale : previousOppositeScale;
            float max = this.previousMaxScalesToUpdate.contains(scaleTypeId) ? appliedScale : previousOppositeScale;
            if (!this.cachedPreviousMinScales.containsKey(scaleTypeId) || this.previousMinScalesToUpdate.contains(scaleTypeId)) {
                if (this.notOriginalCallPrevious.contains(scaleTypeId)) {
                    this.tickTarget = 0;
                }

                if (!this.previousMaxScalesToUpdate.contains(scaleTypeId) && this.previousMinScalesToUpdate.contains(scaleTypeId) && this.previousOppositesToUpdate.contains(scaleTypeId)) {
                    this.cachedPreviousMaxScales.put(scaleTypeId, previousOppositeScale);
                    this.previousOppositesToUpdate.remove(scaleTypeId);
                }
                this.cachedPreviousMinScales.put(scaleTypeId, min);
                this.previousMinScalesToUpdate.remove(scaleTypeId);
            }
            if (!this.cachedPreviousMaxScales.containsKey(scaleTypeId) || this.previousMaxScalesToUpdate.contains(scaleTypeId)) {
                if (this.notOriginalCallPrevious.contains(scaleTypeId)) {
                    this.tickTarget = this.getMaxTicks();
                }

                if (this.previousMaxScalesToUpdate.contains(scaleTypeId) && !this.previousMinScalesToUpdate.contains(scaleTypeId) && this.previousOppositesToUpdate.contains(scaleTypeId)) {
                    this.cachedPreviousMinScales.put(scaleTypeId, previousOppositeScale);
                    this.previousOppositesToUpdate.remove(scaleTypeId);
                }
                this.cachedPreviousMaxScales.put(scaleTypeId, max);
                this.previousMaxScalesToUpdate.remove(scaleTypeId);
            }
            this.previousOppositesToUpdate.remove(scaleTypeId);
            if (this.tickSettingPrevention.size() == this.getCachedScaleIds().size()) {
                if (this.initialized) {
                    float slope = this.getMaxTicks() / (this.cachedPreviousMaxScales.get(scaleTypeId) - this.cachedPreviousMinScales.get(scaleTypeId));
                    this.setTicks(Math.round(slope * (currentScale - this.cachedPreviousMinScales.get(scaleTypeId))));
                }
                this.tickSettingPrevention.clear();
            }
            this.populateReachedPreviousScale(scaleTypeId, this.tickTarget, appliedScale);
            this.updateOthers((LivingEntity) scaleData.getEntity(), !this.notOriginalCallPrevious.contains(scaleTypeId));
            this.shouldUpdatePreviousModifiers.remove(scaleTypeId);
        }

        if (this.isMax()) {
            this.notOriginalCallPrevious.remove(scaleTypeId);
            return this.cachedPreviousMaxScales.getOrDefault(scaleTypeId, (float) Services.PLATFORM.applyModifiers(scaleData.getEntity(), this.modifiers, modifiedScale));
        } else if (this.isMin()) {
            this.notOriginalCallPrevious.remove(scaleTypeId);
            return this.cachedPreviousMinScales.getOrDefault(scaleTypeId, modifiedScale);
        } else if (this.reachedPreviousScales.containsKey(scaleTypeId) && this.reachedPreviousScales.getOrDefault(scaleTypeId, new HashMap<>()).containsKey(this.ticks)) {
            this.notOriginalCallPrevious.remove(scaleTypeId);
            return this.reachedPreviousScales.getOrDefault(scaleTypeId, new HashMap<>()).getOrDefault(this.ticks, modifiedScale);
        }

        float maxScale = this.cachedPreviousMaxScales.getOrDefault(scaleTypeId, (float) Services.PLATFORM.applyModifiers(scaleData.getEntity(), this.modifiers, modifiedScale));
        float minScale = this.cachedPreviousMinScales.getOrDefault(scaleTypeId, modifiedScale);

        float modified = Mth.lerp((float) this.getClampedTicks() / this.getMaxTicks(), minScale, maxScale);

        this.populateReachedPreviousScale(scaleTypeId, this.ticks, modified);
        this.updateOthers((LivingEntity) scaleData.getEntity(), !this.notOriginalCallPrevious.contains(scaleTypeId));

        this.notOriginalCallPrevious.remove(scaleTypeId);
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
