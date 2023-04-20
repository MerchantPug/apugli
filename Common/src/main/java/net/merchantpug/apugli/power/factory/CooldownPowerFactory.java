package net.merchantpug.apugli.power.factory;

import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.util.HudRender;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public interface CooldownPowerFactory<P> extends SpecialPowerFactory<P> {
    
    static SerializableData getSerializableData() {
        return new SerializableData()
            .add("cooldown", SerializableDataTypes.INT, 1)
            .add("hud_render", ApoliDataTypes.HUD_RENDER, HudRender.DONT_RENDER);
    }
    
    boolean canUse(P power, Entity entity);
    
    void use(P power, Entity entity);
    
    int getRemainingTicks(P power, Entity entity);
    
    int setRemainingTicks(P power, Entity entity, int value);
    
    void sync(LivingEntity entity, P power);
    
}