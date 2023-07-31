package net.merchantpug.apugli.power.factory;

import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.merchantpug.apugli.mixin.xplatform.client.accessor.GameRendererAccessor;
import net.merchantpug.apugli.platform.Services;
import net.merchantpug.apugli.registry.power.ApugliPowers;
import net.merchantpug.apugli.util.FOVUtil;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;

public interface ModifyFovPowerFactory<P> extends ValueModifyingPowerFactory<P> {

    static SerializableData getSerializableData() {
        return ValueModifyingPowerFactory.getSerializableData()
                .add("change_divisor", SerializableDataTypes.FLOAT, 1.0F);
    }

    default double getFov(double originalFov, Camera activeRenderInfo, LivingEntity living) {
        double d = Mth.lerp(Minecraft.getInstance().getFrameTime(), ((GameRendererAccessor)Minecraft.getInstance().gameRenderer).getOldFov(), ((GameRendererAccessor) Minecraft.getInstance().gameRenderer).getFov());
        if (Services.POWER.hasPower(living, this)) {
            if (!Minecraft.getInstance().isPaused()) {
                float changeDivisor = Services.POWER.getPowers(living, this).stream().map(p -> this.getDataFromPower(p).getFloat("change_divisor")).reduce((a, b) -> a * b).get();
                setPartialTicks(getPartialTicks() + (Minecraft.getInstance().getDeltaFrameTime() * (getCurrentFovMultiplier() / getPreviousFovMultiplier() + 1.0F) / changeDivisor));
                setPreviousChangeDivisor(changeDivisor);
            }
            if (!hasPreviousValue() || getPreviousFovEffectScale() != Minecraft.getInstance().options.fovEffectScale().get()) {
                double currentFov = Services.PLATFORM.applyModifiers(living, this, originalFov) / d / Minecraft.getInstance().options.fov().get();
                setPreviousFovMultiplier(originalFov / d / Minecraft.getInstance().options.fov().get());
                setCurrentFovMultiplier(currentFov);
                setPartialTicks(0.0F);
                setPreviousFovEffectScale(Minecraft.getInstance().options.fovEffectScale().get());
            } else if (getPartialTicks() >= 1.0F) {
                double currentFov = Services.PLATFORM.applyModifiers(living, this, originalFov) / d / Minecraft.getInstance().options.fov().get();
                setPreviousFovMultiplier(getCurrentFovMultiplier());
                setCurrentFovMultiplier(currentFov);
                setPartialTicks(0.0F);
            }
            double lerpedFovMultiplier = ApugliPowers.MODIFY_FOV.get().getLerpedFovMultiplier();
            setHadPowerPreviously(true);
            setHasResetPreviousValue(false);
            return FOVUtil.redoModifications(originalFov, activeRenderInfo, getPartialTicks()) * Mth.lerp(Minecraft.getInstance().options.fovEffectScale().get(), 1.0F, Mth.clamp(lerpedFovMultiplier, 0.1, 1.5));
        } else if (!Services.POWER.hasPower(living, this) && hadPowerPreviously()) {
            if (!Minecraft.getInstance().isPaused()) {
                setPartialTicks(getPartialTicks() + (Minecraft.getInstance().getDeltaFrameTime() * (1.0F / getPreviousFovMultiplier() + 1.0F) / getPreviousChangeDivisor()));
            }
            if (!hasResetPreviousValue()) {
                setPreviousFovMultiplier(getLerpedFovMultiplier());
                setPartialTicks(0.0F);
                setHasResetPreviousValue(true);
            }
            double lerpedFovMultiplier = Mth.lerp(Mth.clamp(getPartialTicks(), 0.0D, 1.0D), getPreviousFovMultiplier(), 1.0F);
            if (getPartialTicks() > 1.0F) {
                setHadPowerPreviously(false);
                setPreviousFovMultiplier(Double.NaN);
                setPartialTicks(0.0);
            }
            return FOVUtil.redoModifications(originalFov, activeRenderInfo, getPartialTicks()) * Mth.lerp(Minecraft.getInstance().options.fovEffectScale().get(), 1.0F, Mth.clamp(lerpedFovMultiplier, 0.1, 1.5));
        }
        return originalFov;
    }

    default boolean hasPreviousValue() {
        return !Double.isNaN(getPreviousFovMultiplier());
    }

    default double getLerpedFovMultiplier() {
        return Mth.lerp(Mth.clamp(getPartialTicks(), 0.0D, 1.0D), getPreviousFovMultiplier(), getCurrentFovMultiplier());
    }

    boolean hasResetPreviousValue();
    void setHasResetPreviousValue(boolean value);

    boolean hadPowerPreviously();
    void setHadPowerPreviously(boolean value);

    double getPreviousFovMultiplier();
    void setPreviousFovMultiplier(double value);

    double getCurrentFovMultiplier();
    void setCurrentFovMultiplier(double value);

    double getPartialTicks();
    void setPartialTicks(double value);

    float getPreviousChangeDivisor();
    void setPreviousChangeDivisor(float value);

    double getPreviousFovEffectScale();
    void setPreviousFovEffectScale(double value);

}
