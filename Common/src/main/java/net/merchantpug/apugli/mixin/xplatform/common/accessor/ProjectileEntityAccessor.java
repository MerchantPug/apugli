package net.merchantpug.apugli.mixin.xplatform.common.accessor;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Projectile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.UUID;

@Mixin(Projectile.class)
public interface ProjectileEntityAccessor {

    @Accessor("leftOwner")
    boolean getLeftOwner();

    @Accessor("ownerUUID")
    UUID getOwnerUUID();

    @Accessor("cachedOwner")
    Entity getCachedOwner();

    @Accessor("cachedOwner")
    void setCachedOwner(Entity value);
}
