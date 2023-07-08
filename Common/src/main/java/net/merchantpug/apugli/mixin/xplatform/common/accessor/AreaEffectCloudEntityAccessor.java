package net.merchantpug.apugli.mixin.xplatform.common.accessor;

import net.minecraft.world.entity.AreaEffectCloud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AreaEffectCloud.class)
public interface AreaEffectCloudEntityAccessor {
    @Accessor("reapplicationDelay")
    int getReapplicationDelay();

    @Accessor("reapplicationDelay")
    void setReapplicationDelay(int value);
}
