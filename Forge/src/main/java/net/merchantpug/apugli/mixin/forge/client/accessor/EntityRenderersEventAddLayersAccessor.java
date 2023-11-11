package net.merchantpug.apugli.mixin.forge.client.accessor;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.client.event.EntityRenderersEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(EntityRenderersEvent.AddLayers.class)
public interface EntityRenderersEventAddLayersAccessor {
    @Accessor(value = "renderers", remap = false)
    Map<EntityType<?>, EntityRenderer<?>> apugli$getRenderers();
}
