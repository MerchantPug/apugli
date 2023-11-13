package net.merchantpug.apugli.integration.pehkui;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.floats.Float2FloatFunction;
import net.merchantpug.apugli.Apugli;
import net.merchantpug.apugli.network.c2s.integration.pehkui.ResetScaleCheckPacket;
import net.merchantpug.apugli.platform.Services;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import virtuoel.pehkui.api.ScaleData;
import virtuoel.pehkui.api.ScaleRegistries;
import virtuoel.pehkui.api.ScaleType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class LerpedApoliScaleModifier<P> extends ApoliScaleModifier<P> {
    private int ticks;
    private final int maxTicks;
    private float scaleCheckValue = Float.NaN;
    private float modifiedScaleCheckValue;
    protected final Map<ResourceLocation, Float> lowerBoundScales = Maps.newHashMap();
    protected final Map<ResourceLocation, Float> lowerBoundPreviousScales = Maps.newHashMap();
    private final Map<ResourceLocation, Map<Integer, Map<Float, Float>>> deltaReachedScales = Maps.newHashMap();
    private final Map<ResourceLocation, Map<Integer, Float>> reachedPreviousScales = Maps.newHashMap();
    private final Set<ResourceLocation> readyScales = Sets.newHashSet();
    private boolean shouldTickDown = true;
    private final ResourceLocation firstScaleType;

    public LerpedApoliScaleModifier(P power, List<?> modifiers, int maxTicks, Set<ResourceLocation> cachedScaleIds, int powerCount) {
        super(-256.0F - powerCount, power, modifiers, cachedScaleIds);
        this.maxTicks = maxTicks;
        this.ticks = -1;
        this.firstScaleType = cachedScaleIds.stream().findFirst().orElseThrow();
    }

    public int getTicks() {
        return this.ticks;
    }
    public int getClampedTicks() {
        return Mth.clamp(this.ticks, 0 , this.maxTicks);
    }

    public void setTicks(int value) {
        this.ticks = Mth.clamp(value, -1, this.maxTicks + 1);

        if (!this.isMax())
            this.deltaReachedScales.clear();
    }

    protected boolean isMax() {
        return this.ticks >= this.maxTicks + 1;
    }

    protected boolean isMin() {
        return this.ticks == -1;
    }

    public void setReady(ResourceLocation scaleType) {
        this.readyScales.add(scaleType);
    }

    public void invalidate() {
        this.scaleCheckValue = Float.NaN;
    }

    @Override
    public CompoundTag serialize(CompoundTag tag) {
        super.serialize(tag);
        tag.putInt("Ticks", this.getTicks());
        tag.putBoolean("ShouldTickDown", this.shouldTickDown);
        if (!this.lowerBoundScales.isEmpty()) {
            ListTag cachedMaxScaleTag = new ListTag();
            for (Map.Entry<ResourceLocation, Float> entry : this.lowerBoundScales.entrySet()) {
                CompoundTag entryTag = new CompoundTag();
                entryTag.putString("Type", entry.getKey().toString());
                entryTag.putFloat("Value", entry.getValue());
                cachedMaxScaleTag.add(entryTag);
            }
            tag.put("LowerBoundScales", cachedMaxScaleTag);
        }
        if (!this.lowerBoundPreviousScales.isEmpty()) {
            ListTag cachedPreviousMaxScaleTag = new ListTag();
            for (Map.Entry<ResourceLocation, Float> entry : this.lowerBoundPreviousScales.entrySet()) {
                CompoundTag entryTag = new CompoundTag();
                entryTag.putString("Type", entry.getKey().toString());
                entryTag.putFloat("Value", entry.getValue());
                cachedPreviousMaxScaleTag.add(entryTag);
            }
            tag.put("LowerBoundPreviousScales", cachedPreviousMaxScaleTag);
        }
        return tag;
    }

    @Override
    public void deserialize(CompoundTag tag) {
        super.deserialize(tag);
        this.setTicks(tag.getInt("Ticks"));
        this.shouldTickDown = tag.getBoolean("ShouldTickDown");
        if (tag.contains("LowerBoundScales", Tag.TAG_LIST)) {
            ListTag lowerBoundScalesTag = tag.getList("LowerBoundScales", Tag.TAG_COMPOUND);
            for (int i = 0; i < lowerBoundScalesTag.size(); ++i) {
                CompoundTag entryTag = lowerBoundScalesTag.getCompound(i);
                this.lowerBoundScales.put(new ResourceLocation(entryTag.getString("Type")), entryTag.getFloat("Value"));
            }
        }
        if (tag.contains("LowerBoundPreviousScales", Tag.TAG_LIST)) {
            ListTag lowerBoundPreviousScalesTag = tag.getList("LowerBoundPreviousScales", Tag.TAG_COMPOUND);
            for (int i = 0; i < lowerBoundPreviousScalesTag.size(); ++i) {
                CompoundTag entryTag = lowerBoundPreviousScalesTag.getCompound(i);
                this.lowerBoundPreviousScales.put(new ResourceLocation(entryTag.getString("Type")), entryTag.getFloat("Value"));
            }
        }
    }

    @Override
    public void tick(LivingEntity entity, boolean calledFromNbt) {
        boolean hasSentReadyPacket = false;
        boolean hasResetScale = false;

        boolean isActive = Services.POWER.isActive(power, entity);

        for (ResourceLocation scaleTypeId : this.cachedScaleIds) {
            ScaleType scaleType = ScaleRegistries.getEntry(ScaleRegistries.SCALE_TYPES, scaleTypeId);
            ScaleData scaleData = scaleType.getScaleData(entity);

            float modifiedScale = this.capturedModifiedScales.getOrDefault(scaleTypeId, scaleData.getBaseScale());
            float maxScale = (float) Services.PLATFORM.applyModifiers(scaleData.getEntity(), this.modifiers, modifiedScale);

            if (!entity.level().isClientSide() && !calledFromNbt && !hasResetScale && !this.readyScales.isEmpty() && this.capturedModifiedScales.containsKey(scaleTypeId) && (modifiedScale != this.modifiedScaleCheckValue || maxScale != this.scaleCheckValue)) {
                this.setTicks(Mth.clamp((int)((this.cachedMaxScales.getOrDefault(scaleTypeId, modifiedScale) / maxScale) * this.getClampedTicks()), 0, this.maxTicks));
                this.shouldTickDown = false;
                this.cachedScaleIds.forEach(id -> {
                    ScaleType otherScaleType = ScaleRegistries.getEntry(ScaleRegistries.SCALE_TYPES, id);
                    float currentScaleModifiedScale = this.capturedModifiedScales.getOrDefault(id, otherScaleType.getScaleData(entity).getBaseScale());
                    float currentScalePreviousModifiedScale = this.capturedPreviousModifiedScales.getOrDefault(id, otherScaleType.getScaleData(entity).getPrevBaseScale());
                    float newMax = (float) Services.PLATFORM.applyModifiers(entity, this.modifiers, currentScaleModifiedScale);
                    float newPreviousMax = (float) Services.PLATFORM.applyModifiers(entity, this.modifiers, currentScalePreviousModifiedScale);
                    float newLowerBound = this.modifiedScaleCheckValue != modifiedScale ? currentScaleModifiedScale : newMax > this.lowerBoundScales.get(id) ? this.lowerBoundScales.getOrDefault(id, currentScaleModifiedScale) : this.cachedMaxScales.getOrDefault(id, currentScaleModifiedScale);
                    float newLowerPreviousBound = this.modifiedScaleCheckValue != modifiedScale ? currentScalePreviousModifiedScale : newMax > this.lowerBoundScales.get(id) ? this.lowerBoundPreviousScales.getOrDefault(id, currentScalePreviousModifiedScale) : this.cachedPreviousMaxScales.getOrDefault(id, currentScalePreviousModifiedScale);
                    if (this.modifiedScaleCheckValue == modifiedScale && this.lowerBoundScales.containsKey(id) && newMax < newLowerBound) {
                        newLowerBound = newMax;
                        newLowerPreviousBound = newPreviousMax;
                        newMax = this.cachedMaxScales.getOrDefault(id, currentScaleModifiedScale);
                        newPreviousMax = this.cachedPreviousMaxScales.getOrDefault(id, currentScalePreviousModifiedScale);
                        this.shouldTickDown = true;
                    }
                    this.lowerBoundScales.put(id , newLowerBound);
                    this.lowerBoundPreviousScales.put(id, newLowerPreviousBound);
                    this.cachedMaxScales.put(id, newMax);
                    this.cachedPreviousMaxScales.put(id, newPreviousMax);
                });
                this.deltaReachedScales.clear();
                this.reachedPreviousScales.clear();
                this.modifiedScaleCheckValue = modifiedScale;
                this.scaleCheckValue = maxScale;
                Services.POWER.syncPower(entity, power);
                this.cachedScaleIds.forEach(id -> ScaleRegistries.getEntry(ScaleRegistries.SCALE_TYPES, id).getScaleData(entity).onUpdate());
                hasResetScale = true;
            }

            if (hasResetScale || calledFromNbt) continue;

            this.readyScales.add(scaleTypeId);
        }

        if (!this.readyScales.isEmpty() && !entity.level().isClientSide()) {
            if (this.ticks <= this.maxTicks && isActive && !shouldTickDown) {
                this.setTicks(this.getClampedTicks() + 1);
                Services.POWER.syncPower(entity, power);
                this.cachedScaleIds.forEach(id -> ScaleRegistries.getEntry(ScaleRegistries.SCALE_TYPES, id).getScaleData(entity).onUpdate());
            } else if (this.ticks >= 0 && (!isActive || shouldTickDown)) {
                this.setTicks(this.getClampedTicks() - 1);
                Services.POWER.syncPower(entity, power);
                this.cachedScaleIds.forEach(id -> ScaleRegistries.getEntry(ScaleRegistries.SCALE_TYPES, id).getScaleData(entity).onUpdate());
            }
        }
    }

    public float modifyScale(final ScaleData scaleData, final float modifiedScale, final float delta) {
        if (!(scaleData.getEntity() instanceof LivingEntity entity)) {
            if (!this.hasLoggedWarn)
                Apugli.LOG.warn("Attempted to use LerpedApoliScaleModifier on a non-living entity. This should not be possible.");
            this.hasLoggedWarn = true;
            return modifiedScale;
        }
        ResourceLocation scaleTypeId = getResourceLocationFromScaleData(scaleData);

        if (!capturedModifiedScales.containsKey(scaleTypeId) || this.capturedModifiedScales.getOrDefault(scaleTypeId, modifiedScale) != modifiedScale) {
            if (scaleTypeId == firstScaleType && entity.level().isClientSide())
                Services.PLATFORM.sendC2S(new ResetScaleCheckPacket(this.getId()));
            this.capturedModifiedScales.put(scaleTypeId, modifiedScale);
            this.deltaReachedScales.remove(scaleTypeId);
        }

        if (this.isMin()) {
            return lowerBoundScales.getOrDefault(scaleTypeId, modifiedScale);
        } else if (this.isMax()) {
            if (!this.cachedMaxScales.containsKey(scaleTypeId)) {
                return Services.POWER.isActive(power, entity) ? (float) Services.PLATFORM.applyModifiers(scaleData.getEntity(), this.modifiers, modifiedScale) : modifiedScale;
            }
            return this.cachedMaxScales.get(scaleTypeId);
        } else if (this.deltaReachedScales.containsKey(scaleTypeId) && this.deltaReachedScales.get(scaleTypeId).containsKey(this.ticks) && this.deltaReachedScales.get(scaleTypeId).get(this.ticks).containsKey(delta)) {
            return this.deltaReachedScales.get(scaleTypeId).get(this.ticks).get(delta);
        }

        float maxScale = !this.cachedMaxScales.containsKey(scaleTypeId) ? (float) Services.PLATFORM.applyModifiers(scaleData.getEntity(), this.modifiers, modifiedScale) : this.cachedMaxScales.get(scaleTypeId);

        double lerp = Mth.clampedLerp(0.0D, 1.0D, (double) this.getClampedTicks() / (double) this.maxTicks);
        float initialScale = Mth.clampedLerp(this.lowerBoundScales.getOrDefault(scaleTypeId, modifiedScale), maxScale, (float) lerp);

        Float2FloatFunction easing = Optional.ofNullable(scaleData.getEasing()).orElseGet(scaleData.getScaleType()::getDefaultEasing);

        float progress = (float) this.ticks + delta;
        int total = this.maxTicks;
        float range = maxScale - initialScale;
        float perTick = total == 0 ? 1.0F : (easing.apply(progress / total));

        float modified = (initialScale + (perTick * range));

        this.deltaReachedScales.computeIfAbsent(scaleTypeId, location -> new HashMap<>()).computeIfAbsent(this.getClampedTicks(), i -> new HashMap<>()).put(delta, modified);
        return modified;
    }

    public float modifyPrevScale(final ScaleData scaleData, final float modifiedScale) {
        if (!(scaleData.getEntity() instanceof LivingEntity entity)) {
            if (!this.hasLoggedWarn)
                Apugli.LOG.warn("Attempted to use LerpedApoliScaleModifier on a non-living entity. This should not be possible.");
            this.hasLoggedWarn = true;
            return modifiedScale;
        }
        ResourceLocation scaleTypeId = getResourceLocationFromScaleData(scaleData);

        if (!capturedPreviousModifiedScales.containsKey(scaleTypeId) || this.capturedPreviousModifiedScales.getOrDefault(scaleTypeId, modifiedScale) != modifiedScale) {
            if (scaleTypeId == firstScaleType && entity.level().isClientSide())
                Services.PLATFORM.sendC2S(new ResetScaleCheckPacket(this.getId()));
            this.capturedPreviousModifiedScales.put(scaleTypeId, modifiedScale);
            this.deltaReachedScales.remove(scaleTypeId);
        }

        if (this.isMin()) {
            return this.lowerBoundPreviousScales.getOrDefault(scaleTypeId, modifiedScale);
        } else if (this.isMax()) {
            if (!this.cachedPreviousMaxScales.containsKey(scaleTypeId)) {
                return Services.POWER.isActive(power, entity) ? (float) Services.PLATFORM.applyModifiers(scaleData.getEntity(), this.modifiers, modifiedScale) : modifiedScale;
            }
            return this.cachedPreviousMaxScales.get(scaleTypeId);
        } else if (this.reachedPreviousScales.containsKey(scaleTypeId) && this.reachedPreviousScales.get(scaleTypeId).containsKey(this.ticks)) {
            return this.reachedPreviousScales.get(scaleTypeId).get(this.ticks);
        }

        float maxScale = !this.cachedPreviousMaxScales.containsKey(scaleTypeId) ? (float) Services.PLATFORM.applyModifiers(scaleData.getEntity(), this.modifiers, modifiedScale) : this.cachedPreviousMaxScales.get(scaleTypeId);

        double lerp = Mth.clampedLerp(0.0D, 1.0D, ((double) this.getClampedTicks() / (double) this.maxTicks));
        float value = Mth.clampedLerp(this.lowerBoundPreviousScales.getOrDefault(scaleTypeId, modifiedScale), maxScale, (float) lerp);
        this.reachedPreviousScales.computeIfAbsent(scaleTypeId, location -> new HashMap<>()).put(this.ticks, value);
        return value;
    }
}
