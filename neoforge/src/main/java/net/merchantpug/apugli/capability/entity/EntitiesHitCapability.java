package net.merchantpug.apugli.capability.entity;

import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class EntitiesHitCapability implements IEntitiesHitCapability, ICapabilityProvider {

    private final Map<ResourceLocation, Integer> apugli$entitiesHit = new HashMap<>();

    public static final Capability<EntitiesHitCapability> INSTANCE = CapabilityManager.get(new CapabilityToken<>() {});
    private final LazyOptional<EntitiesHitCapability> thisOptional = LazyOptional.of(() -> this);

    public EntitiesHitCapability() {}

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return EntitiesHitCapability.INSTANCE.orEmpty(cap, thisOptional);
    }

    @Override
    public Set<ResourceLocation> apugli$powersThatHaveLanded() {
        return this.apugli$entitiesHit.keySet();
    }

    @Override
    public int getPowerValue(ResourceLocation value) {
        return this.apugli$entitiesHit.getOrDefault(value, 0);
    }


    @Override
    public void addToPowersThatHaveLanded(ResourceLocation value) {
        this.apugli$entitiesHit.compute(value, (resourceLocation, integer) -> integer != null ? integer + 1 : 1);
    }
}
