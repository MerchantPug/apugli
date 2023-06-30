package net.merchantpug.apugli.mixin.xplatform.client.accessor;

import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(GameRenderer.class)
public interface GameRendererAccessor {

    @Accessor("oldFov")
    float getOldFov();

    @Accessor("fov")
    float getFov();

}
