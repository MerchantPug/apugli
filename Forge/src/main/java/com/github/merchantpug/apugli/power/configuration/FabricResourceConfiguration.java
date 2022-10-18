package com.github.merchantpug.apugli.power.configuration;

import io.github.apace100.apoli.util.HudRender;
import io.github.apace100.calio.data.SerializableData;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredEntityAction;
import io.github.edwinmindcraft.apoli.api.power.configuration.power.IHudRenderedVariableIntPowerConfiguration;
import net.minecraft.MethodsReturnNonnullByDefault;
import org.jetbrains.annotations.Nullable;

@MethodsReturnNonnullByDefault
public record FabricResourceConfiguration(SerializableData.Instance data) implements IHudRenderedVariableIntPowerConfiguration {
    
    @Override
    public HudRender hudRender() {
        return data.get("hud_render");
    }
    
    @Override
    public int min() {
        return data.getInt("min");
    }
    
    @Override
    public int max() {
        return data.getInt("max");
    }
    
    @Override
    public int initialValue() {
        return data.getInt("start_value");
    }
    
    @Nullable
    public ConfiguredEntityAction<?, ?> minAction() {
        return data.get("min_action");
    }
    
    @Nullable
    public ConfiguredEntityAction<?, ?> maxAction() {
        return data.get("max_action");
    }
    
}
