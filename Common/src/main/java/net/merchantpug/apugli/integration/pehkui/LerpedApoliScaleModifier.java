package net.merchantpug.apugli.integration.pehkui;

import it.unimi.dsi.fastutil.floats.Float2FloatFunction;
import net.merchantpug.apugli.Apugli;
import net.merchantpug.apugli.access.ScaleDataAccess;
import net.merchantpug.apugli.network.s2c.integration.pehkui.SyncScalePacket;
import net.merchantpug.apugli.network.s2c.integration.pehkui.UpdateLerpedScalePacket;
import net.merchantpug.apugli.platform.Services;
import net.merchantpug.apugli.registry.power.ApugliPowers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import virtuoel.pehkui.api.ScaleData;
import virtuoel.pehkui.api.ScaleRegistries;
import virtuoel.pehkui.api.ScaleType;

import java.util.List;
import java.util.Optional;

public class LerpedApoliScaleModifier<P> extends ApoliScaleModifier<P> {
    private int ticks = 0;
    private boolean hasReachedMaxDelta = false;
    private int maxTicks;
    private Optional<Float> previousScale;
    protected float cachedBaseMaxScale = 1.0F;
    boolean hasLoggedWarn = false;

    public LerpedApoliScaleModifier(P power, List<?> modifiers, ResourceLocation id, int maxTicks, Optional<Float> previousScale) {
        super(power, modifiers, id);
        this.maxTicks = maxTicks;
        this.previousScale = previousScale;
    }
    public void setTicks(int value) {
        if (!this.isMax(value))
            this.hasReachedMaxDelta = false;

        this.ticks = Mth.abs(value);
    }

    protected boolean isMax(int value) {
        return value >= this.maxTicks + 1;
    }

    public void setPreviousScale(float value) {
        this.previousScale = Optional.of(value);
    }

    public void removePreviousScale() {
        this.previousScale = Optional.empty();
    }

    @Override
    public void tick(LivingEntity living) {
        if (!living.level().isClientSide()) {
            boolean hasSentPacket = false;
            List<ResourceLocation> scaleTypeIds = ApugliPowers.MODIFY_SCALE.get().getScaleTypeCache(power, living).stream().toList();

            for (ResourceLocation scaleTypeId : scaleTypeIds) {
                ScaleType scaleType = ScaleRegistries.getEntry(ScaleRegistries.SCALE_TYPES, scaleTypeId);
                ScaleData scaleData = scaleType.getScaleData(living);

                float maxScale = Services.POWER.isActive(power, living) ? (float) Services.PLATFORM.applyModifiers(scaleData.getEntity(), modifiers, scaleData.getBaseScale()) : scaleData.getBaseScale();

                if (maxScale != this.cachedBaseMaxScale) {
                    if (!scaleData.getBaseValueModifiers().contains(this)) {
                        scaleData.getBaseValueModifiers().add(this);
                        if (!hasSentPacket) {
                            Services.PLATFORM.sendS2CTrackingAndSelf(new SyncScalePacket(living.getId(), scaleTypeIds, Services.POWER.getPowerId(this.power), ApugliPowers.MODIFY_SCALE.get().getModifiers(this.power, living), Optional.of(this.maxTicks), previousScale), living);
                        }
                    }
                    this.cachedBaseMaxScale = maxScale;
                    scaleData.onUpdate();
                }

                if ((this.isMax(this.ticks) && (!Services.POWER.isActive(power, living) || cachedBaseMaxScale == scaleData.getBaseScale()) && scaleData.getBaseValueModifiers().contains(this))) {
                    this.previousScale = Optional.empty();
                    ((ScaleDataAccess) scaleData).apugli$removeFromApoliScaleModifiers(this.getId());
                    scaleData.getBaseValueModifiers().remove(this);
                    if (!hasSentPacket) {
                        Services.PLATFORM.sendS2CTrackingAndSelf(new SyncScalePacket(living.getId(), scaleTypeIds, Services.POWER.getPowerId(this.power), false), living);
                        Services.PLATFORM.sendS2CTrackingAndSelf(new UpdateLerpedScalePacket(living.getId(), this.getId(), this.ticks, Optional.of(Optional.empty())), living);
                    }
                    scaleData.onUpdate();
                } else if (this.ticks <= this.maxTicks) {
                    this.setTicks(Math.max(this.ticks, 0) + 1);
                    if (Services.POWER.isActive(power, living) && !scaleData.getBaseValueModifiers().contains(this)) {
                        scaleData.getBaseValueModifiers().add(this);
                        if (!hasSentPacket) {
                            Services.PLATFORM.sendS2CTrackingAndSelf(new SyncScalePacket(living.getId(), scaleTypeIds, Services.POWER.getPowerId(this.power), ApugliPowers.MODIFY_SCALE.get().getModifiers(this.power, living), Optional.of(this.maxTicks), previousScale), living);
                        }
                    } else if (!hasSentPacket) {
                        Services.PLATFORM.sendS2CTrackingAndSelf(new UpdateLerpedScalePacket(living.getId(), this.getId(), this.ticks, Optional.of(previousScale)), living);
                    }
                    scaleData.onUpdate();
                }
            }
        }
    }

    public float modifyScale(final ScaleData scaleData, final float modifiedScale, final float delta) {
        if (!(scaleData.getEntity() instanceof LivingEntity living)) {
            if (!this.hasLoggedWarn)
                Apugli.LOG.warn("Attempted to use LerpedApoliScaleModifier on non-living entity. This should not be possible.");
            this.hasLoggedWarn = true;
            return modifiedScale;
        }
        float maxScale = Services.POWER.isActive(power, living) ? (float) Services.PLATFORM.applyModifiers(scaleData.getEntity(), modifiers, modifiedScale) : modifiedScale;
        float initialScale = Mth.clampedLerp(this.previousScale.orElse(modifiedScale), maxScale, Mth.clampedLerp(0.0F, 1.0F, (float) Mth.clamp(this.ticks, 0, this.maxTicks) / (float) this.maxTicks));

        if (maxScale != this.cachedMaxScale) {
            float previousScale = !Services.POWER.isActive(power, living) ? this.cachedMaxScale : Mth.clampedLerp(this.previousScale.orElse(modifiedScale), this.cachedMaxScale, Mth.clampedLerp(0.0F, 1.0F, (float) Mth.clamp(this.ticks, 0, this.maxTicks) / (float) this.maxTicks));
            this.setPreviousScale(previousScale);
            if (!Services.POWER.isActive(power, living))
                this.setTicks(this.maxTicks - this.ticks);
            else
                this.setTicks(0);
            this.cachedMaxScale = maxScale;
            this.hasReachedMaxDelta = false;
        }

        if (hasReachedMaxDelta) {
            return maxScale;
        }

        Float2FloatFunction easing = Optional.ofNullable(scaleData.getEasing()).orElseGet(scaleData.getScaleType()::getDefaultEasing);

        float progress = (float) this.ticks + delta;
        int total = this.maxTicks;
        float range = maxScale - initialScale;
        float perTick = total == 0 ? 1.0F : (easing.apply(progress / total));

        if (delta == 1.0F && isMax(this.ticks)) {
            this.hasReachedMaxDelta = true;
        }
        return initialScale + (perTick * range);
    }

    public float modifyPrevScale(final ScaleData scaleData, final float modifiedScale) {
        return Mth.clampedLerp(this.previousScale.orElse(modifiedScale), (float) Services.PLATFORM.applyModifiers(scaleData.getEntity(), modifiers, modifiedScale), Mth.clampedLerp(0.0F, 1.0F, (float) Mth.clamp(this.ticks, 0, this.maxTicks) / (float) this.maxTicks));
    }
}
