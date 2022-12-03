package net.merchantpug.apugli.component;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.component.tick.CommonTickingComponent;
import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.Active;
import io.github.apace100.apoli.power.Power;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class KeyPressComponentImpl implements KeyPressComponent, CommonTickingComponent, AutoSyncedComponent {
    private int previousPowerSize = 0;
    private Set<Active.Key> previousKeysToCheck = new HashSet<>();
    private final Set<Active.Key> keysToCheck = new HashSet<>();

    private Set<Active.Key> previouslyUsedKeys = new HashSet<>();
    private final Set<Active.Key> currentlyUsedKeys = new HashSet<>();

    private final PlayerEntity provider;

    public KeyPressComponentImpl(PlayerEntity provider) {
        this.provider = provider;
    }

    @Override
    public void readFromNbt(NbtCompound tag) {}

    @Override
    public void writeToNbt(NbtCompound tag) {}

    @Override
    public Set<Active.Key> getCurrentlyUsedKeys() {
        return currentlyUsedKeys;
    }

    @Override
    public void changePreviousKeysToCheckToCurrent() {
        this.previouslyUsedKeys = this.currentlyUsedKeys;
    }

    @Override
    public Set<Active.Key> getKeysToCheck() {
        return keysToCheck;
    }

    @Override
    public void addKeyToCheck(Active.Key key) {
        keysToCheck.add(key);
    }

    @Override
    public void addKey(Active.Key key) {
        currentlyUsedKeys.add(key);
    }

    @Override
    public void removeKey(Active.Key key) {
        currentlyUsedKeys.remove(key);
    }

    @Override
    public void tick() {
        int powerSize = PowerHolderComponent.KEY.get(provider).getPowers(Power.class, true).size();
        if (previousPowerSize != powerSize) {
            previousKeysToCheck.clear();
            keysToCheck.clear();
            previouslyUsedKeys.clear();
            currentlyUsedKeys.clear();
        }
        previousPowerSize = powerSize;
    }

    @Override
    public void writeSyncPacket(PacketByteBuf buf, ServerPlayerEntity recipient) {
        List<Active.Key> newKeysToCheck = keysToCheck.stream().filter(key -> !previousKeysToCheck.contains(key)).toList();
        buf.writeInt(newKeysToCheck.size());
        for (Active.Key key : newKeysToCheck) {
            ApoliDataTypes.KEY.send(buf, key);
        }
        previousKeysToCheck = keysToCheck;

        List<Active.Key> keysToAdd = currentlyUsedKeys.stream().filter(key -> !previouslyUsedKeys.contains(key)).toList();
        buf.writeInt(keysToAdd.size());
        for (Active.Key key : keysToAdd) {
            ApoliDataTypes.KEY.send(buf, key);
        }

        List<Active.Key> keysToRemove = previouslyUsedKeys.stream().filter(key -> !currentlyUsedKeys.contains(key)).toList();
        buf.writeInt(keysToRemove.size());
        for (Active.Key key : keysToRemove) {
            ApoliDataTypes.KEY.send(buf, key);
        }
        previouslyUsedKeys = currentlyUsedKeys.stream().filter(key -> key.continuous).collect(Collectors.toSet());
    }

    public void applySyncPacket(PacketByteBuf buf) {
        int keysToCheckSize = buf.readInt();
        for (int i = 0; i < keysToCheckSize; ++i) {
            keysToCheck.add(ApoliDataTypes.KEY.receive(buf));
        }

        int keysToAddSize = buf.readInt();
        for (int i = 0; i < keysToAddSize; ++i) {
            currentlyUsedKeys.add(ApoliDataTypes.KEY.receive(buf));
        }

        int keysToRemoveSize = buf.readInt();
        for (int i = 0; i < keysToRemoveSize; ++i) {
            currentlyUsedKeys.remove(ApoliDataTypes.KEY.receive(buf));
        }
    }
}
