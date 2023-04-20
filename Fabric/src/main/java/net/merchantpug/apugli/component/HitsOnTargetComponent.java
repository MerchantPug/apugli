package net.merchantpug.apugli.component;

import dev.onyxstudios.cca.api.v3.component.tick.ServerTickingComponent;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;

import java.util.Map;

public interface HitsOnTargetComponent extends ServerTickingComponent {
    Map<Integer, Tuple<Integer, Integer>> getHits();
    void setHits(Entity entity, int hitValue, int timer);
}
