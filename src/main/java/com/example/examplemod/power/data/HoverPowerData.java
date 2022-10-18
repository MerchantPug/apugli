package com.github.merchantpug.apugli.power.data;

import com.github.merchantpug.apugli.power.HoverPower;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.calio.data.SerializableData;
import net.minecraft.world.entity.LivingEntity;

import java.util.function.BiFunction;

public class HoverPowerData implements IPowerData<HoverPower> {
    @Override
    public BiFunction<PowerType<HoverPower>, LivingEntity, HoverPower> getPowerConstructor(SerializableData.Instance data) {
        return HoverPower::new;
    }
}