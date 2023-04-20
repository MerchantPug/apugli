package net.merchantpug.apugli.component;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.merchantpug.apugli.util.ApugliConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.*;

public class HitsOnTargetComponentImpl implements HitsOnTargetComponent, AutoSyncedComponent {
    private Map<Integer, Tuple<Integer, Integer>> previousHits = new HashMap<>();
    private final Map<Integer, Tuple<Integer, Integer>> hits = new HashMap<>();
    private final LivingEntity provider;

    public HitsOnTargetComponentImpl(LivingEntity provider) {
        this.provider = provider;
    }

    @Override
    public boolean shouldSyncWith(ServerPlayer player) {
        return player == provider || PlayerLookup.tracking(provider).contains(player);
    }

    @Override
    public void readFromNbt(CompoundTag tag) {}

    @Override
    public void writeToNbt(CompoundTag tag) {}

    @Override
    public void writeSyncPacket(FriendlyByteBuf buf, ServerPlayer recipient) {
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
        buf.writeInt(updateMap.size());
        for (Map.Entry<Integer, Tuple<Integer, Integer>> entry : updateMap.entrySet()) {
            buf.writeInt(entry.getKey());
            buf.writeInt(entry.getValue().getA());
            buf.writeInt(entry.getValue().getB());
        }
        buf.writeInt(removalMap.size());
        for (Integer id : removalMap) {
            buf.writeInt(id);
        }
    }

    public void applySyncPacket(FriendlyByteBuf buf) {
        int updateSize = buf.readInt();
        for (int i = 0; i < updateSize; ++i) {
            int entityId = buf.readInt();
            int amount = buf.readInt();
            int ticksLeft = buf.readInt();
            hits.put(entityId, new Tuple<>(amount, ticksLeft));
        }
        int removalSize = buf.readInt();
        for (int i = 0; i < removalSize; ++i) {
            int entityId = buf.readInt();
            hits.remove(entityId);
        }
    }

    @Override
    public Map<Integer, Tuple<Integer, Integer>> getHits() {
        return hits;
    }

    @Override
    public void setHits(Entity entity, int hitValue, int timer) {
        hits.put(entity.getId(), new Tuple<>(hitValue, timer));
    }

    @Override
    public void serverTick() {
        Iterator<Map.Entry<Integer, Tuple<Integer, Integer>>> it = hits.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, Tuple<Integer, Integer>> entry = it.next();
            Entity entity = provider.level.getEntity(entry.getKey());
            int hitAmount = entry.getValue().getA();
            int currentTime = entry.getValue().getB();
            if (entity == null || !entity.isAlive() || currentTime > ApugliConfig.resetTimerTicks) {
                it.remove();
                if (!it.hasNext()) {
                    ApugliEntityComponents.HITS_ON_TARGET_COMPONENT.sync(provider);
                }
                continue;
            }
            entry.setValue(new Tuple<>(hitAmount, currentTime + 1));
        }
        previousHits = hits;
    }
}
