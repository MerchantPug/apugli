package net.merchantpug.apugli.capability.entity;

import io.github.edwinmindcraft.apoli.api.component.IPowerContainer;
import io.github.edwinmindcraft.apoli.api.power.IActivePower;
import net.merchantpug.apugli.network.ApugliPacketHandler;
import net.merchantpug.apugli.network.s2c.SyncKeyPressCapabilityPacket;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class KeyPressCapability implements IKeyPressCapability, ICapabilityProvider {
    private int previousPowerSize = 0;
    private final Set<IActivePower.Key> keysToCheck = new HashSet<>();

    private Set<IActivePower.Key> previouslyUsedKeys = new HashSet<>();
    private final Set<IActivePower.Key> currentlyUsedKeys = new HashSet<>();

    Player provider;

    public static final Capability<KeyPressCapability> INSTANCE = CapabilityManager.get(new CapabilityToken<>() {});
    private final LazyOptional<KeyPressCapability> thisOptional = LazyOptional.of(() -> this);

    public KeyPressCapability(Player provider) {
        this.provider = provider;
    }


    @Override
    public Set<IActivePower.Key> getCurrentlyUsedKeys() {
        return this.currentlyUsedKeys;
    }

    @Override
    public Set<IActivePower.Key> getPreviouslyUsedKeys() {
        return this.previouslyUsedKeys;
    }

    @Override
    public void setPreviouslyUsedKeys() {
        this.previouslyUsedKeys = this.currentlyUsedKeys.stream().filter(IActivePower.Key::continuous).collect(Collectors.toSet());
    }

    @Override
    public Set<IActivePower.Key> getKeysToCheck() {
        return this.keysToCheck;
    }

    @Override
    public void addKeyToCheck(IActivePower.Key key) {
        this.keysToCheck.add(key);
    }

    @Override
    public void addKey(IActivePower.Key key) {
        this.currentlyUsedKeys.add(key);
    }

    @Override
    public void addPreviousKey(IActivePower.Key key) {
        this.previouslyUsedKeys.add(key);
    }

    @Override
    public void removeKey(IActivePower.Key key) {
        this.currentlyUsedKeys.remove(key);
    }

    public void tick() {
        int powerSize = IPowerContainer.get(this.provider).resolve().isEmpty() ? 0 : IPowerContainer.get(this.provider).resolve().get().getPowers().size();
        if (this.previousPowerSize != powerSize) {
            this.keysToCheck.clear();
            this.previouslyUsedKeys.clear();
            this.currentlyUsedKeys.clear();
        }
        this.previousPowerSize = powerSize;
    }

    public void sync() {
        if (provider.level.isClientSide) return;
        ApugliPacketHandler.INSTANCE.send(PacketDistributor.TRACKING_ENTITY.with(() -> provider), new SyncKeyPressCapabilityPacket(provider.getId(), previouslyUsedKeys, currentlyUsedKeys));
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return KeyPressCapability.INSTANCE.orEmpty(cap, thisOptional);
    }
}