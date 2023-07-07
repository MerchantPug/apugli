package net.merchantpug.apugli.mixin.xplatform.common.accessor;

import net.minecraft.world.entity.projectile.Projectile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Projectile.class)
public interface ProjectileEntityAccessor {

    @Accessor("leftOwner")
    boolean getLeftOwner();

}
