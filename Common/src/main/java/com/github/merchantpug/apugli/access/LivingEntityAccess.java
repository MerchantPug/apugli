package com.github.merchantpug.apugli.access;

import org.apache.commons.lang3.tuple.Triple;

import java.util.HashMap;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;

public interface LivingEntityAccess {
    HashMap<Entity, Tuple<Integer, Integer>> getHits();
    void setHits(Entity entity, int hitValue, int timer);
}
