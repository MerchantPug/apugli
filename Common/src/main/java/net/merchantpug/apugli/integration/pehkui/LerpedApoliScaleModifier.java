package net.merchantpug.apugli.integration.pehkui;

import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.floats.Float2FloatFunction;
import net.merchantpug.apugli.Apugli;
import net.merchantpug.apugli.access.ScaleDataAccess;
import net.merchantpug.apugli.network.s2c.integration.pehkui.MarkLerpedScaleReadyPacket;
import net.merchantpug.apugli.network.s2c.integration.pehkui.SyncScalePacket;
import net.merchantpug.apugli.platform.Services;
import net.merchantpug.apugli.registry.power.ApugliPowers;
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
    private final Set<ScaleType> maxDeltaReachedScales = Sets.newHashSet();
    private final int maxTicks;
    private Optional<Float> previousScale;
    private boolean hasLoggedWarn = false;
    private final Set<ScaleType> scalesToUpdate = Sets.newHashSet();
    private final Set<ScaleType> readyScales = Sets.newHashSet();

    public LerpedApoliScaleModifier(P power, List<?> modifiers, int maxTicks, Optional<Float> previousScale) {
        super(power, modifiers);
        this.maxTicks = maxTicks;
        this.previousScale = previousScale;
    }

    public void setTicks(int value) {
        if (!this.isMax(value))
            this.maxDeltaReachedScales.clear();

        this.ticks = Mth.clamp(value, 0, this.maxTicks + 1);
    }

    protected boolean isMax(int value) {
        return value >= this.maxTicks + 1;
    }

    public void setReady(ScaleType scaleType) {
        this.readyScales.add(scaleType);
    }

    public void setCachedMaxScale(ScaleType scaleType, float cachedMaxScale) {
        this.cachedMaxScales.put(scaleType, cachedMaxScale);
    }

    public int getTicks() {
        return this.ticks;
    }

    public Optional<Float> getPreviousScale() {
        return this.previousScale;
    }

    public void setPreviousScale(float value) {
        this.previousScale = Optional.of(value);
    }

    @Override
    public void tick(LivingEntity entity, boolean calledFromNbt) {
        boolean hasSentPacket = false;
        List<ResourceLocation> scaleTypeIds = ApugliPowers.MODIFY_SCALE.get().getCachedScaleIds(power, entity).stream().toList();

        for (ResourceLocation scaleTypeId : scaleTypeIds) {
            ScaleType scaleType = ScaleRegistries.getEntry(ScaleRegistries.SCALE_TYPES, scaleTypeId);
            ScaleData scaleData = scaleType.getScaleData(entity);

            if (this.scalesToUpdate.contains(scaleType)) {
                ((ScaleDataAccess) scaleData).apugli$addToApoliScaleModifiers(this.getId());
                scaleData.getBaseValueModifiers().add(this);
                if (!hasSentPacket && !entity.level().isClientSide()) {
                    Map<ResourceLocation, Float> mappedCachedScales = new HashMap<>();
                    for (Map.Entry<ScaleType, Float> entry : this.cachedMaxScales.entrySet()) {
                        mappedCachedScales.put(ScaleRegistries.getId(ScaleRegistries.SCALE_TYPES, entry.getKey()), entry.getValue());
                    }
                    Services.PLATFORM.sendS2CTrackingAndSelf(SyncScalePacket.addScaleToClient(entity.getId(), scaleTypeIds, this.getId()), entity);
                    Services.PLATFORM.sendS2CTrackingAndSelf(new MarkLerpedScaleReadyPacket(entity.getId(), this.getId(), mappedCachedScales), entity);
                    hasSentPacket = true;
                }
                scaleData.onUpdate();
                this.scalesToUpdate.remove(scaleType);
            }

            float maxScale = Services.POWER.isActive(power, entity) ? (float) Services.PLATFORM.applyModifiers(scaleData.getEntity(), modifiers, scaleData.getBaseScale()) : scaleData.getBaseScale();

            if (calledFromNbt) {
                this.cachedMaxScales.put(scaleType, maxScale);
                this.maxDeltaReachedScales.remove(scaleType);
                if (!entity.level().isClientSide) {
                    this.scalesToUpdate.add(scaleType);
                }
                this.readyScales.add(scaleType);
                continue;
            }

            if (this.readyScales.contains(scaleType) && (!this.cachedMaxScales.containsKey(scaleData.getScaleType()) || maxScale != this.cachedMaxScales.get(scaleType))) {
                ((ScaleDataAccess) scaleData).apugli$removeFromApoliScaleModifiers(this.getId());
                scaleData.getBaseValueModifiers().remove(this);
                ((ScaleDataAccess) scaleData).apugli$addToApoliScaleModifiers(this.getId());
                scaleData.getBaseValueModifiers().add(this);
                scaleData.onUpdate();
            }

            if ((this.isMax(this.ticks) && (!Services.POWER.isActive(power, entity) || this.cachedMaxScales.containsKey(scaleType) && this.cachedMaxScales.get(scaleType) == scaleData.getBaseScale()) && scaleData.getBaseValueModifiers().contains(this))) {
                this.previousScale = Optional.empty();
                ((ScaleDataAccess) scaleData).apugli$removeFromApoliScaleModifiers(this.getId());
                scaleData.getBaseValueModifiers().remove(this);
                if (!hasSentPacket && !entity.level().isClientSide()) {
                    Services.PLATFORM.sendS2CTrackingAndSelf(SyncScalePacket.removeScaleFromClient(entity.getId(), scaleTypeIds, this.getId()), entity);
                    Services.POWER.syncPower(entity, power);
                    hasSentPacket = true;
                }
                scaleData.onUpdate();
            } else if (this.ticks <= this.maxTicks) {
                this.setTicks(Math.max(this.ticks, 0) + 1);
                if (Services.POWER.isActive(power, entity) && !scaleData.getBaseValueModifiers().contains(this)) {
                    ((ScaleDataAccess) scaleData).apugli$removeFromApoliScaleModifiers(this.getId());
                    scaleData.getBaseValueModifiers().remove(this);
                    ((ScaleDataAccess) scaleData).apugli$addToApoliScaleModifiers(this.getId());
                    scaleData.getBaseValueModifiers().add(this);
                    if (!hasSentPacket && !entity.level().isClientSide()) {
                        Services.PLATFORM.sendS2CTrackingAndSelf(SyncScalePacket.addScaleToClient(entity.getId(), scaleTypeIds, this.getId()), entity);
                        Services.POWER.syncPower(entity, power);
                        hasSentPacket = true;
                    }
                } else if (!hasSentPacket && !entity.level().isClientSide()) {
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
        float maxScale = Services.POWER.isActive(power, entity) ? (float) Services.PLATFORM.applyModifiers(scaleData.getEntity(), modifiers, scaleData.getBaseScale()) : scaleData.getBaseScale();
        float initialScale = Mth.clampedLerp(this.previousScale.orElse(modifiedScale), maxScale, Mth.clampedLerp(0.0F, 1.0F, (float) Mth.clamp(this.ticks, 0, this.maxTicks) / (float) this.maxTicks));

        if (this.readyScales.contains(scaleData.getScaleType()) && (!this.cachedMaxScales.containsKey(scaleData.getScaleType()) || maxScale != this.cachedMaxScales.get(scaleData.getScaleType()))) {
            float previousScale = !Services.POWER.isActive(power, entity) ? this.cachedMaxScales.get(scaleData.getScaleType()) : Mth.clampedLerp(this.previousScale.orElse(modifiedScale), this.cachedMaxScales.get(scaleData.getScaleType()), Mth.clampedLerp(0.0F, 1.0F, (float) Mth.clamp(this.ticks, 0, this.maxTicks) / (float) this.maxTicks));
            this.setPreviousScale(previousScale);
            if (!Services.POWER.isActive(power, entity))
                this.setTicks(this.maxTicks - this.ticks);
            else
                this.setTicks(0);
            this.cachedMaxScales.put(scaleData.getScaleType(), maxScale);
            this.maxDeltaReachedScales.clear();
        }

        if (maxDeltaReachedScales.contains(scaleData.getScaleType())) {
            return maxScale;
        }

        Float2FloatFunction easing = Optional.ofNullable(scaleData.getEasing()).orElseGet(scaleData.getScaleType()::getDefaultEasing);

        float progress = (float) this.ticks + delta;
        int total = this.maxTicks;
        float range = maxScale - initialScale;
        float perTick = total == 0 ? 1.0F : (easing.apply(progress / total));

        if (delta == 1.0F && isMax(this.ticks)) {
            this.maxDeltaReachedScales.add(scaleData.getScaleType());
        }
        return initialScale + (perTick * range);
    }

    public float modifyPrevScale(final ScaleData scaleData, final float modifiedScale) {
        return Mth.clampedLerp(this.previousScale.orElse(modifiedScale), (float) Services.PLATFORM.applyModifiers(scaleData.getEntity(), modifiers, modifiedScale), Mth.clampedLerp(0.0F, 1.0F, (float) Mth.clamp(this.ticks, 0, this.maxTicks) / (float) this.maxTicks));
    }
}
