package net.merchantpug.apugli.access;

import net.minecraft.world.entity.Entity;

public interface ItemStackAccess {
    void apugli$setEntity(Entity entity);
    Entity apugli$getEntity();
}