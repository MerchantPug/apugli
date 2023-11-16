package net.merchantpug.apugli.integration.pehkui;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class LerpedApoliScaleModifier<P> extends ApoliScaleModifier<P> {
    private int ticks;
    private final int maxTicks;
    protected final Optional<ResourceLocation> easing;
    protected final Map<ResourceLocation, Float> lowerBoundScales = Maps.newHashMap();
    protected final Map<ResourceLocation, Float> lowerBoundPreviousScales = Maps.newHashMap();
    private final Map<ResourceLocation, Map<Integer, Map<Float, Float>>> deltaReachedScales = Maps.newHashMap();
    private final Map<ResourceLocation, Map<Integer, Float>> reachedPreviousScales = Maps.newHashMap();
    private final Set<ResourceLocation> shouldUpdateModifiers = Sets.newHashSet();
    private final Set<ResourceLocation> shouldUpdatePreviousModifiers = Sets.newHashSet();
    private final Map<ResourceLocation, Boolean> previousActiveStates = Maps.newHashMap();
    private int tickTarget = 0;
    private boolean isTickingDown = false;
    private boolean shouldUpdateScales = false;

    public LerpedApoliScaleModifier(P power, List<?> modifiers, int maxTicks, Set<ResourceLocation> cachedScaleIds, int powerPriority, Optional<ResourceLocation> easing) {
        super(power, modifiers, cachedScaleIds, powerPriority);
        this.maxTicks = maxTicks;
        this.ticks = 0;
        this.easing = easing;
    }

    public int getTicks() {
        return this.ticks;
    }

    public int getMaxTicks() {
        return this.maxTicks;
    }

    public int getClampedTicks() {
        return Mth.clamp(this.ticks, 0 , this.maxTicks);
    }

    public void setTicks(int value) {
        this.ticks = Math.clamp(value, 0, this.getMaxTicks());
    }

    public void setTickTarget(int value) {
        this.tickTarget = Mth.clamp(value, 0, this.getMaxTicks());
    }

    protected boolean isMax() {
        return this.ticks >= this.getMaxTicks();
    }

    protected boolean isMin() {
        return this.ticks <= 0;
    }

    @Override
    public CompoundTag serialize(CompoundTag tag) {
        super.serialize(tag);
        tag.putInt("Ticks", this.getClampedTicks());
        tag.putInt("TickTarget", this.tickTarget);
        if (this.shouldUpdateScales)
            tag.putBoolean("ShouldUpdateScales", true);
        if (!this.lowerBoundScales.isEmpty()) {
            ListTag listTag = new ListTag();
            for (Map.Entry<ResourceLocation, Float> entry : this.lowerBoundScales.entrySet()) {
                CompoundTag entryTag = new CompoundTag();
                entryTag.putString("Type", entry.getKey().toString());
                entryTag.putFloat("Value", entry.getValue());
                listTag.add(entryTag);
            }
            tag.put("LowerBoundScales", listTag);
        }
        if (!this.lowerBoundPreviousScales.isEmpty()) {
            ListTag listTag = new ListTag();
            for (Map.Entry<ResourceLocation, Float> entry : this.lowerBoundPreviousScales.entrySet()) {
                CompoundTag entryTag = new CompoundTag();
                entryTag.putString("Type", entry.getKey().toString());
                entryTag.putFloat("Value", entry.getValue());
                listTag.add(entryTag);
            }
            tag.put("LowerBoundPreviousScales", listTag);
        }
        if (!this.checkMaxScale.isEmpty()) {
            ListTag listTag = new ListTag();
            for (Map.Entry<ResourceLocation, Float> entry : this.checkMaxScale.entrySet()) {
                CompoundTag entryTag = new CompoundTag();
                entryTag.putString("Type", entry.getKey().toString());
                entryTag.putFloat("Value", entry.getValue());
                listTag.add(entryTag);
            }
            tag.put("CheckMaxScales", listTag);
        }
        if (!this.checkModifiedScales.isEmpty()) {
            ListTag listTag = new ListTag();
            for (Map.Entry<ResourceLocation, Float> entry : this.checkModifiedScales.entrySet()) {
                CompoundTag entryTag = new CompoundTag();
                entryTag.putString("Type", entry.getKey().toString());
                entryTag.putFloat("Value", entry.getValue());
                listTag.add(entryTag);
            }
            tag.put("CheckModifiedScales", listTag);
        }
        if (!this.previousActiveStates.isEmpty()) {
            ListTag listTag = new ListTag();
            for (Map.Entry<ResourceLocation, Boolean> entry : this.previousActiveStates.entrySet()) {
                CompoundTag entryTag = new CompoundTag();
                entryTag.putString("Type", entry.getKey().toString());
                entryTag.putBoolean("Value", entry.getValue());
                listTag.add(entryTag);
            }
            tag.put("PreviousActiveStates", listTag);
        }
        return tag;
    }

    @Override
    public void deserialize(CompoundTag tag) {
        super.deserialize(tag);
        this.setTickTarget(tag.getInt("TickTarget"));
        this.setTicks(tag.getInt("Ticks"));

        if (tag.contains("ShouldUpdateScales", Tag.TAG_BYTE))
            this.shouldUpdateScales = tag.getBoolean("ShouldUpdateScales");

        this.lowerBoundScales.clear();
        if (tag.contains("LowerBoundScales", Tag.TAG_LIST)) {
            ListTag listTag = tag.getList("LowerBoundScales", Tag.TAG_COMPOUND);
            for (int i = 0; i < listTag.size(); ++i) {
                CompoundTag entryTag = listTag.getCompound(i);
                this.lowerBoundScales.put(new ResourceLocation(entryTag.getString("Type")), entryTag.getFloat("Value"));
            }
        }
        this.lowerBoundPreviousScales.clear();
        if (tag.contains("LowerBoundPreviousScales", Tag.TAG_LIST)) {
            ListTag listTag = tag.getList("LowerBoundPreviousScales", Tag.TAG_COMPOUND);
            for (int i = 0; i < listTag.size(); ++i) {
                CompoundTag entryTag = listTag.getCompound(i);
                this.lowerBoundPreviousScales.put(new ResourceLocation(entryTag.getString("Type")), entryTag.getFloat("Value"));
            }
        }
        this.checkMaxScale.clear();
        if (tag.contains("CheckMaxScales", Tag.TAG_LIST)) {
            ListTag listTag = tag.getList("CheckMaxScales", Tag.TAG_COMPOUND);
            for (int i = 0; i < listTag.size(); ++i) {
                CompoundTag entryTag = listTag.getCompound(i);
                this.checkMaxScale.put(new ResourceLocation(entryTag.getString("Type")), entryTag.getFloat("Value"));
            }
        }
        this.checkModifiedScales.clear();
        if (tag.contains("CheckModifiedScales", Tag.TAG_LIST)) {
            ListTag listTag = tag.getList("CheckModifiedScales", Tag.TAG_COMPOUND);
            for (int i = 0; i < listTag.size(); ++i) {
                CompoundTag entryTag = listTag.getCompound(i);
                this.checkModifiedScales.put(new ResourceLocation(entryTag.getString("Type")), entryTag.getFloat("Value"));
            }
        }
        this.previousActiveStates.clear();
        if (tag.contains("PreviousActiveStates", Tag.TAG_LIST)) {
            ListTag listTag = tag.getList("PreviousActiveStates", Tag.TAG_COMPOUND);
            for (int i = 0; i < listTag.size(); ++i) {
                CompoundTag entryTag = listTag.getCompound(i);
                this.previousActiveStates.put(new ResourceLocation(entryTag.getString("Type")), entryTag.getBoolean("Value"));
            }
        }
    }

    @Override
    public void tick(LivingEntity entity, boolean calledFromNbt) {
        boolean isActive = Services.POWER.isActive(power, entity);

        for (ResourceLocation typeId : this.cachedScaleIds) {
            ScaleData data = ScaleRegistries.getEntry(ScaleRegistries.SCALE_TYPES, typeId).getScaleData(entity);

            float value = (float)Services.PLATFORM.applyModifiers(data.getEntity(), this.modifiers, data.getBaseScale());
            if (!this.checkMaxScale.containsKey(typeId) || value != this.checkMaxScale.get(typeId) || !this.previousActiveStates.containsKey(typeId) || isActive != this.previousActiveStates.get(typeId)) {
                if (value != this.checkMaxScale.getOrDefault(typeId, value)) {
                    PehkuiUtil.sendToBack(this);
                }
                markForUpdating(this, typeId);
                this.previousActiveStates.put(typeId, isActive);
                this.checkMaxScale.put(typeId, value);
            }
        }

        if (this.ticks < this.getMaxTicks() && tickTarget > this.ticks) {
            this.setTicks(Mth.clamp(this.getClampedTicks() + 1, 0, this.tickTarget));
            this.shouldUpdateScales = true;
            this.isTickingDown = false;
        } else if (this.tickTarget < this.ticks) {
            this.setTicks(Mth.clamp(this.getClampedTicks() - 1, !isActive ? 0 : this.tickTarget, this.getMaxTicks()));
            this.shouldUpdateScales = true;
            this.isTickingDown = true;
        }

        if (this.shouldUpdateScales) {
            this.shouldUpdateScales = false;
            this.updateAllScales(entity);
        }
    }

    private static void markForUpdating(LerpedApoliScaleModifier<?> modifier, ResourceLocation typeId) {
        modifier.shouldUpdateModifiers.add(typeId);
        modifier.shouldUpdatePreviousModifiers.add(typeId);
        modifier.shouldUpdateScales = true;
    }

    public float modifyScale(final ScaleData scaleData, final float modifiedScale, final float delta) {
        if (!(scaleData.getEntity() instanceof LivingEntity entity)) {
            if (!this.hasLoggedWarn)
                Apugli.LOG.warn("Attempted to use LerpedApoliScaleModifier on a non-living entity. This should not be possible.");
            this.hasLoggedWarn = true;
            return modifiedScale;
        }
        ResourceLocation scaleTypeId = getResourceLocationFromScaleData(scaleData);

        if (this.shouldUpdateModifiers.contains(scaleTypeId) || !this.checkModifiedScales.containsKey(scaleTypeId) || this.checkModifiedScales.get(scaleTypeId) != modifiedScale) {
            boolean isActive = Services.POWER.isActive(power, entity);
            updateScaleCache(scaleTypeId, modifiedScale, !isActive ? modifiedScale : (float)Services.PLATFORM.applyModifiers(entity, this.modifiers, modifiedScale), scaleData.getBaseScale(), isActive);
            this.checkModifiedScales.put(scaleTypeId, modifiedScale);
            this.shouldUpdateModifiers.remove(scaleTypeId);
        }

        if (this.isMin()) {
            return this.lowerBoundScales.getOrDefault(scaleTypeId, modifiedScale);
        } else if (this.isMax()) {
            if (!this.cachedMaxScales.containsKey(scaleTypeId)) {
                return Services.POWER.isActive(power, entity) ? (float) Services.PLATFORM.applyModifiers(scaleData.getEntity(), this.modifiers, modifiedScale) : modifiedScale;
            }
            return this.cachedMaxScales.get(scaleTypeId);
        } else if (this.ticks == tickTarget && this.deltaReachedScales.containsKey(scaleTypeId) && this.deltaReachedScales.get(scaleTypeId).containsKey(this.ticks) && this.deltaReachedScales.get(scaleTypeId).get(this.ticks).containsKey(1.0F)) {
            return this.deltaReachedScales.get(scaleTypeId).get(this.ticks).get(1.0F);
        } else if (this.deltaReachedScales.containsKey(scaleTypeId) && this.deltaReachedScales.get(scaleTypeId).containsKey(this.ticks) && this.deltaReachedScales.get(scaleTypeId).get(this.ticks).containsKey(delta)) {
            return this.deltaReachedScales.get(scaleTypeId).get(this.ticks).get(delta);
        }

        float maxScale = !this.cachedMaxScales.containsKey(scaleTypeId) ? (float)Services.PLATFORM.applyModifiers(scaleData.getEntity(), this.modifiers, modifiedScale) : this.cachedMaxScales.get(scaleTypeId);

        Float2FloatFunction easing = this.easing.map(location -> {
            if (ScaleRegistries.SCALE_EASINGS.containsKey(location)) {
                return ScaleRegistries.getEntry(ScaleRegistries.SCALE_EASINGS, location);
            }
            Apugli.LOG.error("'easing' value '{}' for power '{}' is not a valid scale easing.", location, Services.POWER.getPowerId(this.power));
            return null;
        }).orElse(Optional.ofNullable(scaleData.getEasing()).orElseGet(scaleData.getScaleType()::getDefaultEasing));

        float progress = this.isTickingDown ? (float) this.ticks - delta : (float) this.ticks + delta;
        long total = this.maxTicks;
        float range = maxScale - this.lowerBoundScales.getOrDefault(scaleTypeId, modifiedScale);
        float perTick = total == 0 ? 1.0F : (easing.apply(progress / total));

        float modified = (this.lowerBoundScales.getOrDefault(scaleTypeId, modifiedScale) + (perTick * range));

        this.deltaReachedScales.computeIfAbsent(scaleTypeId, location -> new HashMap<>()).computeIfAbsent(this.getClampedTicks(), i -> new HashMap<>()).put(delta, modified);
        return modified;
    }

    private void updateScaleCache(ResourceLocation scaleTypeId, float modifiedScale, float targetScale, float baseScale, boolean isActive) {
        boolean boundsChanged = false;

        float previousLowerBoundScale = this.lowerBoundScales.getOrDefault(scaleTypeId, modifiedScale);
        Optional<Float> currentScale = this.isMin() ? Optional.of(this.lowerBoundScales.getOrDefault(scaleTypeId, modifiedScale)) : this.isMax() ? Optional.of(this.cachedMaxScales.getOrDefault(scaleTypeId, modifiedScale)) : this.deltaReachedScales.containsKey(scaleTypeId) && this.deltaReachedScales.get(scaleTypeId).containsKey(this.getTicks()) && this.deltaReachedScales.get(scaleTypeId).get(this.getTicks()).containsKey(1.0F) ? Optional.of(this.deltaReachedScales.get(scaleTypeId).get(this.getTicks()).get(1.0F)) : Optional.empty();

        this.deltaReachedScales.remove(scaleTypeId);

        float newMax = Math.max(modifiedScale, targetScale) > this.cachedMaxScales.getOrDefault(scaleTypeId, Math.max(modifiedScale, targetScale)) ? Math.max(modifiedScale, targetScale) : this.cachedMaxScales.getOrDefault(scaleTypeId, Math.max(modifiedScale, targetScale));
        float newLowerBound = Math.min(modifiedScale, targetScale) < this.lowerBoundScales.getOrDefault(scaleTypeId, Math.min(modifiedScale, targetScale)) ? Math.min(modifiedScale, targetScale) : this.lowerBoundScales.getOrDefault(scaleTypeId, Math.min(modifiedScale, targetScale));

        if (this.checkModifiedScales.getOrDefault(scaleTypeId, modifiedScale) != modifiedScale) {
            // FIXME: Changing modified scales when there are multiple powers.
        }

        if (newMax < newLowerBound) {
            float storedLowerBound = newLowerBound;
            newLowerBound = newMax;
            newMax = storedLowerBound;
        }

        if (!lowerBoundScales.containsKey(scaleTypeId) || newLowerBound < this.lowerBoundScales.get(scaleTypeId)) {
            this.lowerBoundScales.put(scaleTypeId, newLowerBound);
            boundsChanged = true;
        }
        if (!cachedMaxScales.containsKey(scaleTypeId) || newMax > this.cachedMaxScales.get(scaleTypeId)) {
            this.cachedMaxScales.put(scaleTypeId, newMax);
            boundsChanged = true;
        }

        float slope = this.getMaxTicks() / (this.cachedMaxScales.get(scaleTypeId) - this.lowerBoundScales.get(scaleTypeId));
        this.setTickTarget(!isActive ? 0 : Math.round(slope * (targetScale - this.lowerBoundScales.get(scaleTypeId))));

        if (boundsChanged && currentScale.isPresent())
            this.setTicks((int) (slope * (currentScale.get() - previousLowerBoundScale)));

        this.deltaReachedScales.computeIfAbsent(scaleTypeId, id -> new HashMap<>()).computeIfAbsent(this.tickTarget, id -> new HashMap<>()).put(1.0F, targetScale);

        this.shouldUpdateScales = true;
    }

    public float modifyPrevScale(final ScaleData scaleData, final float modifiedScale) {
        if (!(scaleData.getEntity() instanceof LivingEntity entity)) {
            if (!this.hasLoggedWarn)
                Apugli.LOG.warn("Attempted to use LerpedApoliScaleModifier on a non-living entity. This should not be possible.");
            this.hasLoggedWarn = true;
            return modifiedScale;
        }
        ResourceLocation scaleTypeId = getResourceLocationFromScaleData(scaleData);

        if (this.shouldUpdatePreviousModifiers.contains(scaleTypeId) || !checkPreviousModifiedScales.containsKey(scaleTypeId) || this.checkPreviousModifiedScales.get(scaleTypeId) != modifiedScale) {
            boolean isActive = Services.POWER.isActive(power, entity);
            updatePreviousScaleCache(scaleTypeId, modifiedScale, !isActive ? modifiedScale : (float)Services.PLATFORM.applyModifiers(entity, this.modifiers, modifiedScale), scaleData.getBaseScale(), isActive);
            this.checkPreviousModifiedScales.put(scaleTypeId, modifiedScale);
            this.shouldUpdatePreviousModifiers.remove(scaleTypeId);
        }

        if (this.isMin()) {
            return this.lowerBoundPreviousScales.getOrDefault(scaleTypeId, modifiedScale);
        } else if (this.isMax()) {
            if (!this.cachedPreviousMaxScales.containsKey(scaleTypeId)) {
                return Services.POWER.isActive(power, entity) ? (float)Services.PLATFORM.applyModifiers(scaleData.getEntity(), this.modifiers, modifiedScale) : modifiedScale;
            }
            return this.cachedPreviousMaxScales.get(scaleTypeId);
        } else if (this.reachedPreviousScales.containsKey(scaleTypeId) && this.reachedPreviousScales.get(scaleTypeId).containsKey(this.ticks)) {
            return this.reachedPreviousScales.get(scaleTypeId).get(this.ticks);
        }

        float maxScale = !this.cachedPreviousMaxScales.containsKey(scaleTypeId) ? (float)Services.PLATFORM.applyModifiers(scaleData.getEntity(), this.modifiers, modifiedScale) : this.cachedPreviousMaxScales.get(scaleTypeId);

        double lerp = Mth.clampedLerp(0.0D, 1.0D, ((double) this.getClampedTicks() / (double) this.getMaxTicks()));
        float value = Mth.clampedLerp(this.lowerBoundPreviousScales.getOrDefault(scaleTypeId, modifiedScale), maxScale, (float) lerp);
        this.reachedPreviousScales.computeIfAbsent(scaleTypeId, location -> new HashMap<>()).put(this.ticks, value);
        return value;
    }

    private void updatePreviousScaleCache(ResourceLocation scaleTypeId, float modifiedScale, float targetScale, float baseScale, boolean isActive) {
        boolean boundsChanged = false;

        Optional<Float> currentScale = this.isMin() ? Optional.of(this.lowerBoundPreviousScales.getOrDefault(scaleTypeId, modifiedScale)) : this.isMax() ? Optional.of(this.cachedPreviousMaxScales.getOrDefault(scaleTypeId, modifiedScale)) : this.reachedPreviousScales.containsKey(scaleTypeId) && this.reachedPreviousScales.get(scaleTypeId).containsKey(this.getTicks()) && this.reachedPreviousScales.get(scaleTypeId).containsKey(this.getTicks()) ? Optional.of(this.reachedPreviousScales.get(scaleTypeId).get(this.getTicks())) : Optional.empty();

        this.reachedPreviousScales.remove(scaleTypeId);

        float newMax = Math.max(modifiedScale, targetScale) > this.cachedPreviousMaxScales.getOrDefault(scaleTypeId, Math.max(modifiedScale, targetScale)) ? Math.max(modifiedScale, targetScale) : this.cachedPreviousMaxScales.getOrDefault(scaleTypeId, Math.max(modifiedScale, targetScale));
        float newLowerBound = Math.min(modifiedScale, targetScale) < this.lowerBoundPreviousScales.getOrDefault(scaleTypeId, Math.min(modifiedScale, targetScale)) ? Math.min(modifiedScale, targetScale) : this.lowerBoundPreviousScales.getOrDefault(scaleTypeId, Math.min(modifiedScale, targetScale));

        if (this.checkModifiedScales.getOrDefault(scaleTypeId, modifiedScale) != modifiedScale) {
            // FIXME: Changing modified scales when there are multiple powers.
        }

        if (newMax < newLowerBound) {
            float storedLowerBound = newLowerBound;
            newLowerBound = newMax;
            newMax = storedLowerBound;
        }

        if (!lowerBoundPreviousScales.containsKey(scaleTypeId) || newLowerBound < this.lowerBoundPreviousScales.get(scaleTypeId)) {
            this.lowerBoundPreviousScales.put(scaleTypeId, newLowerBound);
            boundsChanged = true;
        }
        if (!cachedPreviousMaxScales.containsKey(scaleTypeId) || newMax > this.cachedPreviousMaxScales.get(scaleTypeId)) {
            this.cachedPreviousMaxScales.put(scaleTypeId, newMax);
            boundsChanged = true;
        }

        float slope = this.getMaxTicks() / (this.cachedPreviousMaxScales.get(scaleTypeId) - this.lowerBoundPreviousScales.get(scaleTypeId));
        this.setTickTarget(!isActive ? 0 : Math.round(slope * (targetScale - this.lowerBoundPreviousScales.get(scaleTypeId))));

        if (boundsChanged && currentScale.isPresent())
            this.setTicks((int) (slope * (currentScale.get() - this.lowerBoundPreviousScales.getOrDefault(scaleTypeId, modifiedScale))));

        this.reachedPreviousScales.computeIfAbsent(scaleTypeId, id -> new HashMap<>()).put(this.tickTarget, targetScale);

        this.shouldUpdateScales = true;
    }
}
