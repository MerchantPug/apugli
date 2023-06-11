
package net.merchantpug.apugli.network.s2c;

import net.merchantpug.apugli.Apugli;
import net.merchantpug.apugli.capability.HitsOnTargetCapability;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public record SyncHitsOnTargetLessenedPacket(int entityId,
                                             Map<Integer, Tuple<Integer, Integer>> previousHits,
                                             Map<Integer, Tuple<Integer, Integer>> hits) implements ApugliPacketS2C {

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(entityId);

        buf.writeInt(previousHits.size());
        previousHits.forEach((entityId, value) -> {
            buf.writeInt(entityId);
            buf.writeInt(value.getA());
            buf.writeInt(value.getB());
        });

        buf.writeInt(hits.size());
        hits.forEach((entityId, value) -> {
            buf.writeInt(entityId);
            buf.writeInt(value.getA());
            buf.writeInt(value.getB());
        });
    }

    public static SyncHitsOnTargetLessenedPacket decode(FriendlyByteBuf buf) {
        int entityId = buf.readInt();

        int updateSize = buf.readInt();
        Map<Integer, Tuple<Integer, Integer>> previousHits = new HashMap<>();
        for (int i = 0; i < updateSize; ++i) {
            int otherEntityId = buf.readInt();
            int amount = buf.readInt();
            int ticksLeft = buf.readInt();
            previousHits.put(otherEntityId, new Tuple<>(amount, ticksLeft));
        }

        int hitsSize = buf.readInt();
        Map<Integer, Tuple<Integer, Integer>> hits = new HashMap<>();
        for (int i = 0; i < hitsSize; ++i) {
            int otherEntityId = buf.readInt();
            int amount = buf.readInt();
            int ticksLeft = buf.readInt();
            hits.put(otherEntityId, new Tuple<>(amount, ticksLeft));
        }
        return new SyncHitsOnTargetLessenedPacket(entityId, previousHits, hits);
    }

    @Override
    public ResourceLocation getFabricId() {
        throw new RuntimeException("ApugliPacket#getFabricId is not meant to be used in Forge specific packets.");
    }

    @Override
    public void handle() {
        // The lambda implementation of this Runnable breaks Forge servers.
        Minecraft.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                Entity entity = Minecraft.getInstance().level.getEntity(entityId);

                if (!(entity instanceof LivingEntity)) {
                    Apugli.LOG.warn("Could not find living entity to sync hits on target with.");
                    return;
                }

                entity.getCapability(HitsOnTargetCapability.INSTANCE).ifPresent(capability -> {
                    Map<Integer, Tuple<Integer, Integer>> updateMap = new HashMap<>();
                    Set<Integer> removalMap = new HashSet<>();
                    for (Map.Entry<Integer, Tuple<Integer, Integer>> entry : hits.entrySet()) {
                        if (!previousHits.containsKey(entry.getKey())) {
                            updateMap.put(entry.getKey(), entry.getValue());
                        } else {
                            Tuple<Integer, Integer> currentPair = entry.getValue();
                            Tuple<Integer, Integer> previousPair = previousHits.get(entry.getKey());
                            if ((!currentPair.getA().equals(previousPair.getA()) || !currentPair.getB().equals(currentPair.getB()))) {
                                updateMap.put(entry.getKey(), entry.getValue());
                            }
                        }
                    }
                    previousHits.keySet().stream().filter(key -> !hits.containsKey(key)).forEach(removalMap::add);

                    updateMap.forEach((entityId, value) -> capability.setHits(entityId, value.getA(), value.getB()));
                    removalMap.forEach(capability::removeHits);
                });
            }
        });
    }
}
