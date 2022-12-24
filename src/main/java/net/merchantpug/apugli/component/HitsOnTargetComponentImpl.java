package net.merchantpug.apugli.component;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.component.tick.ServerTickingComponent;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.merchantpug.apugli.util.ApugliConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Pair;

import java.util.*;

public class HitsOnTargetComponentImpl implements HitsOnTargetComponent, AutoSyncedComponent, ServerTickingComponent {
    private Map<Integer, Pair<Integer, Integer>> previousHits = new HashMap<>();
    private final Map<Integer, Pair<Integer, Integer>> hits = new HashMap<>();
    private final LivingEntity provider;

    public HitsOnTargetComponentImpl(LivingEntity provider) {
        this.provider = provider;
    }

    @Override
    public boolean shouldSyncWith(ServerPlayerEntity player) {
        return player == provider || PlayerLookup.tracking(provider).contains(player);
    }

    @Override
    public void readFromNbt(NbtCompound tag) {}

    @Override
    public void writeToNbt(NbtCompound tag) {}

    @Override
    public void writeSyncPacket(PacketByteBuf buf, ServerPlayerEntity recipient) {
        Map<Integer, Pair<Integer, Integer>> updateMap = new HashMap<>();
        Set<Integer> removalMap = new HashSet<>();
        for (Map.Entry<Integer, Pair<Integer, Integer>> entry : hits.entrySet()) {
            if (!previousHits.containsKey(entry.getKey())) {
                updateMap.put(entry.getKey(), entry.getValue());
            } else {
                Pair<Integer, Integer> currentPair = entry.getValue();
                Pair<Integer, Integer> previousPair = previousHits.get(entry.getKey());
                if ((!currentPair.getLeft().equals(previousPair.getLeft()) || !currentPair.getRight().equals(currentPair.getRight()))) {
                    updateMap.put(entry.getKey(), entry.getValue());
                }
            }
        }
        previousHits.keySet().stream().filter(key -> !hits.containsKey(key)).forEach(removalMap::add);
        buf.writeInt(updateMap.size());
        for (Map.Entry<Integer, Pair<Integer, Integer>> entry : updateMap.entrySet()) {
            buf.writeInt(entry.getKey());
            buf.writeInt(entry.getValue().getLeft());
            buf.writeInt(entry.getValue().getRight());
        }
        buf.writeInt(removalMap.size());
        for (Integer id : removalMap) {
            buf.writeInt(id);
        }
    }

    public void applySyncPacket(PacketByteBuf buf) {
        int updateSize = buf.readInt();
        for (int i = 0; i < updateSize; ++i) {
            int entityId = buf.readInt();
            int amount = buf.readInt();
            int ticksLeft = buf.readInt();
            hits.put(entityId, new Pair<>(amount, ticksLeft));
        }
        int removalSize = buf.readInt();
        for (int i = 0; i < removalSize; ++i) {
            int entityId = buf.readInt();
            hits.remove(entityId);
        }
    }

    @Override
    public Map<Integer, Pair<Integer, Integer>> getHits() {
        return hits;
    }

    @Override
    public void setHits(Entity entity, int hitValue, int timer) {
        hits.put(entity.getId(), new Pair<>(hitValue, timer));
    }

    @Override
    public void serverTick() {
        Iterator<Map.Entry<Integer, Pair<Integer, Integer>>> it = hits.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, Pair<Integer, Integer>> entry = it.next();
            Entity entity = provider.world.getEntityById(entry.getKey());
            int hitAmount = entry.getValue().getLeft();
            int currentTime = entry.getValue().getRight();
            if (entity == null || !entity.isAlive() || currentTime > ApugliConfig.resetTimerTicks) {
                it.remove();
                if (!it.hasNext()) {
                    ApugliEntityComponents.HITS_ON_TARGET_COMPONENT.sync(provider);
                }
                continue;
            }
            entry.setValue(new Pair<>(hitAmount, currentTime + 1));
        }
        previousHits = hits;
    }
}
