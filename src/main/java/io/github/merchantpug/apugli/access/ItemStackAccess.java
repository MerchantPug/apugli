package io.github.merchantpug.apugli.access;

import net.minecraft.entity.Entity;

public interface ItemStackAccess {
    void setEntity(Entity entity);
    Entity getEntity();
}