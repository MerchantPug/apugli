package net.merchantpug.apugli.access;

import net.minecraft.world.entity.Entity;

public interface ItemStackAccess {
    void setEntity(Entity entity);
    Entity getEntity();
}