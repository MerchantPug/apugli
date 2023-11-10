package net.merchantpug.apugli.integration.pehkui;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.floats.Float2FloatFunction;
import net.merchantpug.apugli.Apugli;
import net.merchantpug.apugli.access.ScaleDataAccess;
import net.merchantpug.apugli.network.s2c.integration.pehkui.MarkLerpedScaleReadyPacket;
import net.merchantpug.apugli.network.s2c.integration.pehkui.SyncScalePacket;
import net.merchantpug.apugli.platform.Services;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
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
    private int ticks = 0;
    private int previousTicks = 0;
    private final int maxTicks;
    private boolean shouldUpdatePreviousScale = false;
    private final Map<ResourceLocation, Float> capturedModifiedScales = Maps.newHashMap();
    private final Map<ResourceLocation, Float> capturedDeltas = Maps.newHashMap();
    private final Map<ResourceLocation, Float> capturedPreviousModifiedScales = Maps.newHashMap();
    private final Set<ResourceLocation> cachedScaleIds;
    private final Map<ResourceLocation, Map<Integer, Map<Float, Float>>> deltaReachedScales = Maps.newHashMap();
    private final Map<ResourceLocation, Map<Integer, Float>> reachedPreviousScales = Maps.newHashMap();
    private final Map<ResourceLocation, Float> previousScales = Maps.newHashMap();
    private final Map<ResourceLocation, Float> previousPreviousScales = Maps.newHashMap();
    private final Map<ResourceLocation, Float> activePreviousScales = Maps.newHashMap();
    private final Map<ResourceLocation, Float> activePreviousPreviousScales = Maps.newHashMap();
    private final Set<ResourceLocation> readyScales = Sets.newHashSet();
    private final Map<ResourceLocation, Boolean> previousConditionStates = Maps.newHashMap();
    private Set<ResourceLocation> cachesToClear = Sets.newHashSet();
    private boolean hasLoggedWarn = false;

    public LerpedApoliScaleModifier(P power, List<?> modifiers, int maxTicks, Set<ResourceLocation> cachedScaleIds, boolean conditionState) {
        super(power, modifiers);
        this.maxTicks = maxTicks;
        this.cachedScaleIds = cachedScaleIds;
        for (ResourceLocation id : cachedScaleIds) {
            previousConditionStates.put(id, conditionState);
        }
    }

    public Set<ResourceLocation> getCachedScaleIds() {
        return this.cachedScaleIds;
    }

    public int getTicks() {
        return this.ticks;
    }

    public void setTicks(int value) {
        this.ticks = Mth.clamp(value, 0, this.maxTicks + 1);

        if (!this.isMax())
            this.deltaReachedScales.clear();
    }

    public void setPreviousTicks() {
        this.previousTicks = this.ticks;
    }

    protected boolean isMax() {
        return this.ticks >= this.maxTicks + 1;
    }

    public void setReady(ResourceLocation scaleType) {
        this.readyScales.add(scaleType);
    }


    @Override
    public CompoundTag serialize(CompoundTag tag) {
        tag.putInt("Ticks", this.getTicks());
        if (!cachesToClear.isEmpty()) {
            ListTag previousScales = new ListTag();
            for (ResourceLocation id : cachesToClear)
                previousScales.add(StringTag.valueOf(id.toString()));
            tag.put("CachesToClear", previousScales);
        }
        if (this.shouldUpdatePreviousScale) {
            ListTag previousScales = new ListTag();
            for (Map.Entry<ResourceLocation, Float> entry : this.previousScales.entrySet()) {
                CompoundTag entryTag = new CompoundTag();
                entryTag.putString("Type", entry.getKey().toString());
                entryTag.putFloat("Value", entry.getValue());
                if (this.activePreviousScales.containsKey(entry.getKey()))
                    entryTag.putFloat("ActiveValue", this.activePreviousScales.get(entry.getKey()));
                previousScales.add(entryTag);
            }
            tag.put("PreviousScales", previousScales);
            ListTag previousPreviousScales = new ListTag();
            for (Map.Entry<ResourceLocation, Float> entry : this.previousPreviousScales.entrySet()) {
                CompoundTag entryTag = new CompoundTag();
                entryTag.putString("Type", entry.getKey().toString());
                entryTag.putFloat("Value", entry.getValue());
                if (this.activePreviousPreviousScales.containsKey(entry.getKey()))
                    entryTag.putFloat("ActiveValue", this.activePreviousPreviousScales.get(entry.getKey()));
                previousPreviousScales.add(entryTag);
            }
            tag.put("PreviousPreviousScales", previousPreviousScales);
        }

        return tag;
    }

    @Override
    public void deserialize(CompoundTag tag) {
        int ticks = tag.getInt("Ticks");
        this.setTicks(ticks);
        this.setPreviousTicks();
        if (tag.contains("CachesToClear", Tag.TAG_LIST)) {
            ListTag list = tag.getList("CachesToClear", Tag.TAG_STRING);
            for (int i = 0; i < list.size(); ++i) {
                ResourceLocation scaleType = ResourceLocation.of(list.getString(i), ':');
                cachesToClear.add(scaleType);
            }
        }
        if (tag.contains("PreviousScales", Tag.TAG_LIST)) {
            ListTag previousScalesTag = tag.getList("PreviousScales", Tag.TAG_COMPOUND);
            for (int i = 0; i < previousScalesTag.size(); ++i) {
                CompoundTag previousScaleTag = previousScalesTag.getCompound(i);
                ResourceLocation scaleType = ResourceLocation.of(previousScaleTag.getString("Type"), ':');
                this.previousScales.put(scaleType, previousScaleTag.getFloat("Value"));
                if (previousScaleTag.contains("ActiveValue", Tag.TAG_FLOAT)) {
                    this.activePreviousScales.put(scaleType, previousScaleTag.getFloat("ActiveValue"));
                }
            }
        }
        if (tag.contains("PreviousPreviousScales", Tag.TAG_LIST)) {
            ListTag previousScalesTag = tag.getList("PreviousPreviousScales", Tag.TAG_COMPOUND);
            for (int i = 0; i < previousScalesTag.size(); ++i) {
                CompoundTag previousScaleTag = previousScalesTag.getCompound(i);
                ResourceLocation scaleType = ResourceLocation.of(previousScaleTag.getString("Type"), ':');
                this.previousPreviousScales.put(scaleType, previousScaleTag.getFloat("Value"));
                if (previousScaleTag.contains("ActiveValue", Tag.TAG_FLOAT)) {
                    this.activePreviousPreviousScales.put(scaleType, previousScaleTag.getFloat("ActiveValue"));
                }
            }
        }
    }

    @Override
    public void tick(LivingEntity entity, boolean calledFromNbt) {
        boolean hasSentPacket = false;
        boolean hasResetScale = false;

        for (ResourceLocation scaleTypeId : this.cachedScaleIds) {
            ScaleType scaleType = ScaleRegistries.getEntry(ScaleRegistries.SCALE_TYPES, scaleTypeId);
            ScaleData scaleData = scaleType.getScaleData(entity);
            if (!this.deltaReachedScales.containsKey(scaleTypeId)) {
                this.deltaReachedScales.put(scaleTypeId, new HashMap<>());
            }

            if (this.activePreviousScales.containsKey(scaleTypeId) && this.isMax() && !Services.POWER.isActive(power, entity)) {
                this.activePreviousScales.remove(scaleTypeId);
            }

            float modifiedScale = this.capturedModifiedScales.getOrDefault(scaleTypeId, scaleData.getBaseScale());
            float maxScale = Services.POWER.isActive(power, entity) ? (float) Services.PLATFORM.applyModifiers(scaleData.getEntity(), this.modifiers, modifiedScale) : modifiedScale;

            if (!entity.level().isClientSide() && !this.cachedMaxScales.containsKey(scaleTypeId)) {
                this.cachedMaxScales.put(scaleTypeId, maxScale);
            }

            if (!entity.level().isClientSide() && this.readyScales.contains(scaleTypeId) && maxScale != this.cachedMaxScales.get(scaleTypeId)) {
                boolean condition = Services.POWER.isActive(power, entity);
                if (!hasResetScale) {
                    this.cachedScaleIds.forEach(id -> {
                        float previousScale = !condition ? this.cachedMaxScales.get(scaleTypeId) : this.activePreviousScales.getOrDefault(scaleTypeId, this.deltaReachedScales.containsKey(scaleTypeId) && this.deltaReachedScales.get(scaleTypeId).containsKey(Mth.clamp(this.ticks, 0, this.maxTicks)) ? this.deltaReachedScales.get(scaleTypeId).get(Mth.clamp(this.ticks, 0, this.maxTicks)).get(this.capturedDeltas.getOrDefault(scaleTypeId, 1.0F)) : modifiedScale);
                        float previousPreviousScale = !condition ? this.cachedPreviousMaxScales.get(scaleTypeId) : this.activePreviousPreviousScales.getOrDefault(scaleTypeId, this.reachedPreviousScales.containsKey(scaleTypeId) ? this.reachedPreviousScales.get(scaleTypeId).get(Mth.clamp(this.ticks, 0, this.maxTicks)) : capturedPreviousModifiedScales.getOrDefault(scaleTypeId, scaleData.getBaseScale()));

                        this.previousScales.put(id, previousScale);
                        this.previousPreviousScales.put(id, previousPreviousScale);
                        this.cachesToClear.add(id);
                    });
                    this.setTicks(0);
                    this.shouldUpdatePreviousScale = true;
                    Services.POWER.syncPower(entity, power);
                    hasResetScale = true;
                }
                if (condition != this.previousConditionStates.getOrDefault(scaleTypeId, false) || this.activePreviousScales.containsKey(scaleTypeId)) {
                    this.activePreviousScales.put(scaleTypeId, this.previousScales.getOrDefault(scaleTypeId, modifiedScale));
                    this.activePreviousPreviousScales.put(scaleTypeId, this.previousPreviousScales.getOrDefault(scaleTypeId, modifiedScale));
                    this.previousConditionStates.put(scaleTypeId, condition);
                }
                this.cachedMaxScales.put(scaleTypeId, maxScale);
            }

            if (cachesToClear.contains(scaleTypeId)) {
                this.deltaReachedScales.clear();
                this.reachedPreviousScales.clear();
                this.cachesToClear.remove(scaleTypeId);
                scaleData.onUpdate();
            }

            if (!scaleData.getBaseValueModifiers().contains(this)) {
                ((ScaleDataAccess) scaleData).apugli$addToApoliScaleModifiers(this.getId());
                scaleData.getBaseValueModifiers().add(this);
                if (!hasSentPacket && !entity.level().isClientSide()) {
                    Services.PLATFORM.sendS2CTrackingAndSelf(SyncScalePacket.addScaleToClient(entity.getId(), this.cachedScaleIds.stream().toList(), this.getId()), entity);
                    hasSentPacket = true;
                }
            }

            if (hasResetScale || calledFromNbt) continue;

            if (!entity.level().isClientSide() && !this.readyScales.contains(scaleTypeId)) {
                this.readyScales.add(scaleTypeId);
                Services.PLATFORM.sendS2CTrackingAndSelf(new MarkLerpedScaleReadyPacket(entity.getId(), this.getId()), entity);
            }

            if (this.ticks <= this.maxTicks) {
                if (!hasSentPacket && !entity.level().isClientSide()) {
                    this.setTicks(Math.max(this.ticks, 0) + 1);
                    Services.PLATFORM.sendS2CTrackingAndSelf(SyncScalePacket.addScaleToClient(entity.getId(), cachedScaleIds.stream().toList(), this.getId()), entity);
                    Services.POWER.syncPower(entity, power);
                    hasSentPacket = true;
                }
                scaleData.onUpdate();
            }
        }
    }

    public float modifyScale(final ScaleData scaleData, final float modifiedScale, final float delta) {
        if (!(scaleData.getEntity() instanceof LivingEntity entity)) {
            if (!this.hasLoggedWarn)
                Apugli.LOG.warn("Attempted to use LerpedApoliScaleModifier on non-entity entity. This should not be possible.");
            this.hasLoggedWarn = true;
            return modifiedScale;
        }
        ResourceLocation scaleTypeId = getResourceLocationFromScaleData(scaleData);

        capturedDeltas.put(scaleTypeId, delta);
        if (this.ticks != this.previousTicks) {
            this.capturedModifiedScales.put(scaleTypeId, modifiedScale);
            this.previousTicks = this.ticks;
        }

        if (this.isMax() && this.deltaReachedScales.containsKey(scaleTypeId) && this.deltaReachedScales.get(scaleTypeId).containsKey(this.maxTicks) && this.deltaReachedScales.get(scaleTypeId).get(this.maxTicks).containsKey(1.0F)) {
            return this.deltaReachedScales.get(scaleTypeId).get(this.maxTicks).get(1.0F);
        } else if (this.deltaReachedScales.containsKey(scaleTypeId) && this.deltaReachedScales.get(scaleTypeId).containsKey(this.ticks) && this.deltaReachedScales.get(scaleTypeId).get(this.ticks).containsKey(delta)) {
            return this.deltaReachedScales.get(scaleTypeId).get(this.ticks).get(delta);
        }

        float maxScale = Services.POWER.isActive(power, entity) ? (float) Services.PLATFORM.applyModifiers(scaleData.getEntity(), modifiers, modifiedScale) : modifiedScale;
        float initialScale = Mth.clampedLerp(this.previousScales.getOrDefault(scaleTypeId, modifiedScale), maxScale, Mth.clampedLerp(0.0F, 1.0F, (float) Mth.clamp(this.ticks, 0, this.maxTicks) / (float) this.maxTicks));

        Float2FloatFunction easing = Optional.ofNullable(scaleData.getEasing()).orElseGet(scaleData.getScaleType()::getDefaultEasing);

        float progress = (float) this.ticks + delta;
        int total = this.maxTicks;
        float range = maxScale - initialScale;
        float perTick = total == 0 ? 1.0F : (easing.apply(progress / total));

        float modified = (initialScale + (perTick * range));

        this.deltaReachedScales.computeIfAbsent(scaleTypeId, location -> new HashMap<>()).computeIfAbsent(Mth.clamp(this.ticks, 0, this.maxTicks), i -> new HashMap<>()).put(delta, modified);
        return modified;
    }

    public float modifyPrevScale(final ScaleData scaleData, final float modifiedScale) {
        ResourceLocation id = getResourceLocationFromScaleData(scaleData);

        this.capturedPreviousModifiedScales.put(id, modifiedScale);

        if (this.reachedPreviousScales.containsKey(id) && this.reachedPreviousScales.get(id).containsKey(this.ticks)) {
            return this.reachedPreviousScales.get(id).get(this.ticks);
        }

        float value = (float) Mth.clampedLerp(this.previousPreviousScales.getOrDefault(getResourceLocationFromScaleData(scaleData), modifiedScale), (float) Services.PLATFORM.applyModifiers(scaleData.getEntity(), modifiers, modifiedScale), Mth.clampedLerp(0.0F, 1.0F, Mth.clamp(this.ticks, 0, this.maxTicks) / (double) this.maxTicks));
        this.reachedPreviousScales.computeIfAbsent(id, location -> new HashMap<>()).put(this.ticks, value);
        return value;
    }
}
