package net.merchantpug.apugli.util;

import io.github.apace100.apoli.util.HudRender;
import io.github.edwinmindcraft.apoli.common.registry.condition.ApoliDefaultConditions;
import net.minecraft.resources.ResourceLocation;

public class HudRenderUtil {

    public static final HudRender DONT_RENDER = new HudRender(false, 0, new ResourceLocation("origins", "textures/gui/resource_bar.png"), ApoliDefaultConditions.ENTITY_DEFAULT.getHolder().orElseThrow(), false);

}
