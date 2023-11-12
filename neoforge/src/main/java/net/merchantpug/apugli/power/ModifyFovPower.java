package net.merchantpug.apugli.power;

import com.google.auto.service.AutoService;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import net.merchantpug.apugli.power.configuration.FabricValueModifyingConfiguration;
import net.merchantpug.apugli.power.factory.ModifyFovPowerFactory;

@AutoService(ModifyFovPowerFactory.class)
public class ModifyFovPower extends AbstractValueModifyingPower implements ModifyFovPowerFactory<ConfiguredPower<FabricValueModifyingConfiguration, ?>> {
    private static double previousFovMultiplier = Double.NaN;
    private static double currentFovMultiplier = 0.0D;
    private static double partialTicks = 0.0D;
    private static double previousFovEffectScale;
    private static float previousChangeDivisor;
    private static boolean resetPreviousValue;
    private static boolean hadPowerPreviously;

    public ModifyFovPower() {
        super(ModifyFovPowerFactory.getSerializableData().xmap(
                FabricValueModifyingConfiguration::new,
                FabricValueModifyingConfiguration::data
        ).codec());
    }

    @Override
    public boolean hasResetPreviousValue() {
        return resetPreviousValue;
    }

    @Override
    public void setHasResetPreviousValue(boolean value) {
        resetPreviousValue = value;
    }

    @Override
    public boolean hadPowerPreviously() {
        return hadPowerPreviously;
    }

    @Override
    public void setHadPowerPreviously(boolean value) {
        hadPowerPreviously = value;
    }

    @Override
    public double getPreviousFovMultiplier() {
        return previousFovMultiplier;
    }

    @Override
    public void setPreviousFovMultiplier(double value) {
        previousFovMultiplier = value;
    }

    @Override
    public double getCurrentFovMultiplier() {
        return currentFovMultiplier;
    }

    @Override
    public void setCurrentFovMultiplier(double value) {
        currentFovMultiplier = value;
    }

    @Override
    public double getPartialTicks() {
        return partialTicks;
    }

    @Override
    public void setPartialTicks(double value) {
        partialTicks = value;
    }

    @Override
    public float getPreviousChangeDivisor() {
        return previousChangeDivisor;
    }

    @Override
    public void setPreviousChangeDivisor(float value) {
        previousChangeDivisor = value;
    }

    @Override
    public double getPreviousFovEffectScale() {
        return previousFovEffectScale;
    }

    @Override
    public void setPreviousFovEffectScale(double value) {
        previousFovEffectScale = value;
    }

}