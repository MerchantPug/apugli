package net.merchantpug.apugli.mixin.xplatform.common.accessor;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Projectile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.UUID;

@Mixin(Projectile.class)
public interface ProjectileEntityAccessor {

    @Accessor("leftOwner")
    boolean apugli$getLeftOwner();

    @Accessor("ownerUUID")
    UUID apugli$getOwnerUUID();

    @Accessor("cachedOwner")
    Entity apugli$getCachedOwner();

    @Accessor("cachedOwner")
    void apugli$setCachedOwner(Entity value);
}
