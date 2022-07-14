package com.github.merchantpug.apugli.powers;

import io.github.apace100.origins.power.PowerType;
import io.github.apace100.origins.power.ResourcePower;
import io.github.apace100.origins.power.factory.PowerFactory;
import io.github.apace100.origins.power.factory.action.ActionFactory;
import io.github.apace100.origins.util.HudRender;
import io.github.apace100.origins.util.SerializableData;
import io.github.apace100.origins.util.SerializableDataType;
import com.github.merchantpug.apugli.Apugli;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

import java.util.function.Consumer;

public class BunnyHopPower extends ResourcePower {
    public final double increasePerTick;
    public final int tickRate;

    public static PowerFactory<?> getFactory() {
        return new PowerFactory<BunnyHopPower>(Apugli.identifier("bunny_hop"),
                new SerializableData()
                        .add("min", SerializableDataType.INT)
                        .add("max", SerializableDataType.INT)
                        .addFunctionedDefault("start_value", SerializableDataType.INT, data -> data.getInt("min"))
                        .add("hud_render", SerializableDataType.HUD_RENDER)
                        .add("min_action", SerializableDataType.ENTITY_ACTION, null)
                        .add("max_action", SerializableDataType.ENTITY_ACTION, null)
                        .add("increase_per_tick", SerializableDataType.DOUBLE, 0.000375)
                        .add("tick_rate", SerializableDataType.INT, 10),
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

    public BunnyHopPower(PowerType<?> type, PlayerEntity player, HudRender hudRender, int startValue, int min, int max, Consumer<Entity> actionOnMin, Consumer<Entity> actionOnMax, double increasePerTick, int tickRate) {
        super(type, player, hudRender, startValue, min, max, actionOnMin, actionOnMax);
        this.increasePerTick = increasePerTick;
        this.tickRate = tickRate;
    }
}
