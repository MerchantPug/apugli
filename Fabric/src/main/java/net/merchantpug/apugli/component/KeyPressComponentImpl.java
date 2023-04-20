package net.merchantpug.apugli.component;

import dev.onyxstudios.cca.api.v3.component.tick.CommonTickingComponent;
import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.Active;
import io.github.apace100.apoli.power.Power;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class KeyPressComponentImpl implements KeyPressComponent, CommonTickingComponent {
    private int previousPowerSize = 0;
    private Set<Active.Key> previousKeysToCheck = new HashSet<>();
    private final Set<Active.Key> keysToCheck = new HashSet<>();

    private Set<Active.Key> previouslyUsedKeys = new HashSet<>();
    private final Set<Active.Key> currentlyUsedKeys = new HashSet<>();

    private final Player provider;

    public KeyPressComponentImpl(Player provider) {
        this.provider = provider;
    }

    @Override
    public void readFromNbt(CompoundTag tag) {}

    @Override
    public void writeToNbt(CompoundTag tag) {}

    @Override
    public Set<Active.Key> getCurrentlyUsedKeys() {
        return currentlyUsedKeys;
    }

    @Override
    public Set<Active.Key> getPreviouslyUsedKeys() {
        return previouslyUsedKeys;
    }

    @Override
    public void setPreviouslyUsedKeys() {
        previousKeysToCheck = keysToCheck;
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
    public Set<Active.Key> getPreviousKeysToCheck() {
        return previousKeysToCheck;
    }

    @Override
    public void setPreviousKeysToCheck() {
        previouslyUsedKeys = currentlyUsedKeys.stream().filter(key -> key.continuous).collect(Collectors.toSet());
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
    public void writeSyncPacket(FriendlyByteBuf buf, ServerPlayer recipient) {
        buf.writeInt(keysToCheck.size());
        for (Active.Key key : keysToCheck) {
            ApoliDataTypes.KEY.send(buf, key);
        }
        setPreviousKeysToCheck();

        buf.writeInt(currentlyUsedKeys.size());
        for (Active.Key key : currentlyUsedKeys) {
            ApoliDataTypes.KEY.send(buf, key);
        }

        buf.writeInt(previouslyUsedKeys.size());
        for (Active.Key key : previouslyUsedKeys) {
            ApoliDataTypes.KEY.send(buf, key);
        }
        setPreviouslyUsedKeys();
    }

    public void applySyncPacket(FriendlyByteBuf buf) {
        int keysToCheckSize = buf.readInt();
        for (int i = 0; i < keysToCheckSize; ++i) {
            keysToCheck.add(ApoliDataTypes.KEY.receive(buf));
        }

        int keysToAddSize = buf.readInt();
        for (int i = 0; i < keysToAddSize; ++i) {
            currentlyUsedKeys.add(ApoliDataTypes.KEY.receive(buf));
        }
    }
}
