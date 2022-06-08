package com.github.merchantpug.apugli.power;

import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.ResourcePower;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.apoli.util.HudRender;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import com.github.merchantpug.apugli.Apugli;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;

import java.util.function.Consumer;

public class BunnyHopPower extends ResourcePower {
    public final double increasePerTick;
    public final int tickRate;

    public static PowerFactory<?> getFactory() {
        return new PowerFactory<BunnyHopPower>(Apugli.identifier("bunny_hop"),
                new SerializableData()
                        .add("min", SerializableDataTypes.INT)
                        .add("max", SerializableDataTypes.INT)
                        .addFunctionedDefault("start_value", SerializableDataTypes.INT, data -> data.getInt("min"))
                        .add("hud_render", ApoliDataTypes.HUD_RENDER, HudRender.DONT_RENDER)
                        .add("min_action", ApoliDataTypes.ENTITY_ACTION, null)
                        .add("max_action", ApoliDataTypes.ENTITY_ACTION, null)
                        .add("increase_per_tick", SerializableDataTypes.DOUBLE, 0.000375)
                        .add("tick_rate", SerializableDataTypes.INT, 10),
                data ->
                        (type, player) ->
                                new BunnyHopPower(type, player,
                                        (HudRender)data.get("hud_render"),
                                        data.getInt("start_value"),
                                        data.getInt("min"),
                                        data.getInt("max"),
                                        (ActionFactory<Entity>.Instance)data.get("min_action"),
                                        (ActionFactory<Entity>.Instance)data.get("max_action"),
                                        data.getDouble("increase_per_tick"),
                                        data.getInt("tick_rate")))
                .allowCondition();
    }

    public BunnyHopPower(PowerType<?> type, LivingEntity entity, HudRender hudRender, int startValue, int min, int max, Consumer<Entity> actionOnMin, Consumer<Entity> actionOnMax, double increasePerTick, int tickRate) {
        super(type, entity, hudRender, startValue, min, max, actionOnMin, actionOnMax);
        this.increasePerTick = increasePerTick;
        this.tickRate = tickRate;
    }
}
