package net.merchantpug.apugli.mixin.xplatform.common.accessor;

import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AreaEffectCloud.class)
public interface AreaEffectCloudEntityAccessor {
    @Accessor("reapplicationDelay")
    int apugli$getReapplicationDelay();

    @Accessor("reapplicationDelay")
    void apugli$setReapplicationDelay(int value);

    @Accessor("owner")
    @Nullable LivingEntity apugli$getOwner();
}
