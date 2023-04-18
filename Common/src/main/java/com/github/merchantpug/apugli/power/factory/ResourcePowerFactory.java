package com.github.merchantpug.apugli.power.factory;

import com.github.merchantpug.apugli.platform.Services;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.util.HudRender;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public interface ResourcePowerFactory<P> extends SpecialPowerFactory<P> {
    
    static SerializableData getSerializableData() {
        return new SerializableData()
            .add("min", SerializableDataTypes.INT)
            .add("max", SerializableDataTypes.INT)
            .addFunctionedDefault("start_value", SerializableDataTypes.INT, data -> data.getInt("min"))
            .add("hud_render", ApoliDataTypes.HUD_RENDER, HudRender.DONT_RENDER)
            .add("min_action", Services.ACTION.entityDataType(), null)
            .add("max_action", Services.ACTION.entityDataType(), null);
    }
    
    int getMin(P power, Entity entity);
    
    int getMax(P power, Entity entity);
    
    int assign(P power, Entity entity, int value);
    
    int getValue(P power, Entity entity);
    
    int increment(P power, Entity entity);
    
    int decrement(P power, Entity entity);
    
    void sync(LivingEntity entity, P power);
    
}
