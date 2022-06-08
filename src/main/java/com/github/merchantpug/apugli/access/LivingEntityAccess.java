package com.github.merchantpug.apugli.access;

import net.minecraft.entity.Entity;

import java.util.HashMap;

public interface LivingEntityAccess {
    HashMap<Entity, Integer> getHits();
    void addToHits(Entity entity, int value);
    void setHits(Entity entity, int value);
}
