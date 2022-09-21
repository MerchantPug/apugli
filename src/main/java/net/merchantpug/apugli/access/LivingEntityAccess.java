package net.merchantpug.apugli.access;

import net.minecraft.entity.Entity;
import net.minecraft.util.Pair;
import org.apache.commons.lang3.tuple.Triple;

import java.util.HashMap;

public interface LivingEntityAccess {
    HashMap<Entity, Pair<Integer, Integer>> getHits();
    void setHits(Entity entity, int hitValue, int timer);
}
