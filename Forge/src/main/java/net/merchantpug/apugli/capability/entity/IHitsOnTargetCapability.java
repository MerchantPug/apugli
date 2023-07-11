package net.merchantpug.apugli.capability.entity;

import net.merchantpug.apugli.Apugli;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;

import java.util.Map;

public interface IHitsOnTargetCapability {
    ResourceLocation ID = Apugli.asResource("hits_on_target");

    Map<Integer, Tuple<Integer, Integer>> getHits();
    Map<Integer, Tuple<Integer, Integer>> getPreviousHits();

    void setHits(int entityId, int hitValue, int timer);
    void removeHits(int entityId);

    void serverTick();
}
