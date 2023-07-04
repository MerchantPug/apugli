package net.merchantpug.apugli.access;

import net.minecraft.world.entity.Entity;

public interface ItemStackAccess {
    Entity getEntity();
    void setEntity(Entity entity);
}
