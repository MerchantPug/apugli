package io.github.merchantpug.apugli.power;

import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.ResourcePower;
import io.github.apace100.apoli.util.HudRender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;

import java.util.function.Consumer;

public class BunnyHopPower extends ResourcePower {
    public final double increasePerTick;
    public final int tickRate;

    public BunnyHopPower(PowerType<?> type, LivingEntity entity, HudRender hudRender, int startValue, int min, int max, Consumer<Entity> actionOnMin, Consumer<Entity> actionOnMax, double increasePerTick, int tickRate) {
        super(type, entity, hudRender, startValue, min, max, actionOnMin, actionOnMax);
        this.increasePerTick = increasePerTick;
        this.tickRate = tickRate;
    }
}
