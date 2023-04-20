package net.merchantpug.apugli.power.configuration;

import io.github.apace100.apoli.util.HudRender;
import io.github.apace100.calio.data.SerializableData;
import io.github.edwinmindcraft.apoli.api.power.configuration.power.ICooldownPowerConfiguration;
import net.minecraft.MethodsReturnNonnullByDefault;

@MethodsReturnNonnullByDefault
public record FabricCooldownConfiguration(SerializableData.Instance data) implements ICooldownPowerConfiguration {
    
    @Override
    public int duration() {
        return data.get("duration");
    }
    
    @Override
    public HudRender hudRender() {
        return data.get("hud_render");
    }
    
}
