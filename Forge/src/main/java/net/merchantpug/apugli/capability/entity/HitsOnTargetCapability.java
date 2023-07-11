package net.merchantpug.apugli.capability.entity;

import net.merchantpug.apugli.network.ApugliPacketHandler;
import net.merchantpug.apugli.network.s2c.SyncHitsOnTargetCapabilityPacket;
import net.merchantpug.apugli.util.ApugliConfigs;
import net.minecraft.core.Direction;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class HitsOnTargetCapability implements IHitsOnTargetCapability, ICapabilityProvider {
    private Map<Integer, Tuple<Integer, Integer>> previousHits = new HashMap<>();
    private Map<Integer, Tuple<Integer, Integer>> hits = new HashMap<>();

    private final LivingEntity provider;

    public static final Capability<HitsOnTargetCapability> INSTANCE = CapabilityManager.get(new CapabilityToken<>() {});
    private final LazyOptional<HitsOnTargetCapability> thisOptional = LazyOptional.of(() -> this);

    public HitsOnTargetCapability(LivingEntity provider) {
        this.provider = provider;
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
            Entity entity = provider.level.getEntity(entry.getKey());
            int hitAmount = entry.getValue().getA();
            int currentTime = entry.getValue().getB();
            if (entity == null || !entity.isAlive() || currentTime > ApugliConfigs.SERVER.hitsOnTargetOptions.getResetTimerTicks()) {
                it.remove();
                if (!it.hasNext()) {

                }
                continue;
            }
            entry.setValue(new Tuple<>(hitAmount, currentTime + 1));
        }
        previousHits = hits;
    }

    public void sync() {
        if (provider.level.isClientSide) return;
        ApugliPacketHandler.INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> provider), new SyncHitsOnTargetCapabilityPacket(provider.getId(), hits));
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return HitsOnTargetCapability.INSTANCE.orEmpty(cap, thisOptional);
    }
}
