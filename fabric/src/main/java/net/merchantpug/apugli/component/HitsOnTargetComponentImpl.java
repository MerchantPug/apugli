package net.merchantpug.apugli.component;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.merchantpug.apugli.network.ApugliPackets;
import net.merchantpug.apugli.network.s2c.SyncHitsOnTargetLessenedPacket;
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
    private Map<Integer, Tuple<Integer, Integer>> hits = new HashMap<>();
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
        buf.writeInt(hits.size());

        hits.forEach((entityId, value) -> {
            buf.writeInt(entityId);
            buf.writeInt(value.getA());
            buf.writeInt(value.getB());
        });
    }

    public void applySyncPacket(FriendlyByteBuf buf) {
        int hitsSize = buf.readInt();
        Map<Integer, Tuple<Integer, Integer>> hits = new HashMap<>();
        for (int i = 0; i < hitsSize; ++i) {
            int otherEntityId = buf.readInt();
            int amount = buf.readInt();
            int ticksLeft = buf.readInt();
            hits.put(otherEntityId, new Tuple<>(amount, ticksLeft));
        }

        this.hits = hits;
    }

    @Override
    public Map<Integer, Tuple<Integer, Integer>> getHits() {
        return hits;
    }

    @Override
    public Map<Integer, Tuple<Integer, Integer>> getPreviousHits() {
        return previousHits;
    }

    @Override
    public void setHits(int entityId, int hitValue, int timer) {
        hits.put(entityId, new Tuple<>(hitValue, timer));
    }

    @Override
    public void removeHits(int entityId) {
        hits.remove(entityId);
    }

    @Override
    public void serverTick() {
        Iterator<Map.Entry<Integer, Tuple<Integer, Integer>>> it = hits.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, Tuple<Integer, Integer>> entry = it.next();
            Entity entity = provider.level().getEntity(entry.getKey());
            int hitAmount = entry.getValue().getA();
            int currentTime = entry.getValue().getB();
            if (entity == null || !entity.isAlive() || currentTime > ApugliConfig.resetTimerTicks) {
                it.remove();
                if (!it.hasNext() && provider instanceof ServerPlayer serverPlayer) {
                    ApugliPackets.sendS2CTrackingAndSelf(new SyncHitsOnTargetLessenedPacket(provider.getId(), previousHits, hits), serverPlayer);
                }
                continue;
            }
            entry.setValue(new Tuple<>(hitAmount, currentTime + 1));
        }
        previousHits = hits;
    }

}
