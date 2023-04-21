package net.merchantpug.apugli.power.configuration;

import io.github.apace100.apoli.util.HudRender;
import io.github.apace100.calio.data.SerializableData;
import io.github.edwinmindcraft.apoli.api.power.IActivePower;
import io.github.edwinmindcraft.apoli.api.power.configuration.power.IActiveCooldownPowerConfiguration;
import io.github.edwinmindcraft.apoli.api.power.configuration.power.ICooldownPowerConfiguration;
import net.minecraft.MethodsReturnNonnullByDefault;

@MethodsReturnNonnullByDefault
public record FabricActiveCooldownConfiguration(SerializableData.Instance data) implements IActiveCooldownPowerConfiguration {
    
    @Override
    public int duration() {
        return data.get("duration");
    }
    
    @Override
    public HudRender hudRender() {
        return data.get("hud_render");
    }

    @Override
    public IActivePower.Key key() {
        return data.get("key");
    }

}
