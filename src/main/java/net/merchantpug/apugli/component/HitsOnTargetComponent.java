package net.merchantpug.apugli.component;

import dev.onyxstudios.cca.api.v3.component.Component;
import net.minecraft.entity.Entity;
import net.minecraft.util.Pair;

import java.util.Map;

public interface HitsOnTargetComponent extends Component {
    Map<Integer, Pair<Integer, Integer>> getHits();
    void setHits(Entity entity, int hitValue, int timer);
}
