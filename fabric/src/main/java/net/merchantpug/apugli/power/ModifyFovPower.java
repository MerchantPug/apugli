package net.merchantpug.apugli.power;

import com.google.auto.service.AutoService;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.calio.data.SerializableData;
import net.merchantpug.apugli.power.factory.ModifyFovPowerFactory;
import net.minecraft.world.entity.LivingEntity;

@AutoService(ModifyFovPowerFactory.class)
public class ModifyFovPower extends AbstractValueModifyingPower<ModifyFovPower.Instance> implements ModifyFovPowerFactory<ModifyFovPower.Instance> {
    private static double previousFovMultiplier = Double.NaN;
    private static double currentFovMultiplier = 0.0D;
    private static double partialTicks = 0.0D;
    private static double previousFovEffectScale;
    private static float previousChangeDivisor;
    private static boolean resetPreviousValue;
    private static boolean hadPowerPreviously;

    public ModifyFovPower() {
        super("modify_fov", ModifyFovPowerFactory.getSerializableData(),
                data -> (type, entity) -> new ModifyFovPower.Instance(type, entity, data));
        allowCondition();
    }

    @Override
    public Class<ModifyFovPower.Instance> getPowerClass() {
        return ModifyFovPower.Instance.class;
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

    public static class Instance extends AbstractValueModifyingPower.Instance {

        public Instance(PowerType<?> type, LivingEntity entity, SerializableData.Instance data) {
            super(type, entity, data);
        }

    }

}
