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
        return this.currentlyUsedKeys;
    }

    @Override
    public Set<Active.Key> getPreviouslyUsedKeys() {
        return this.previouslyUsedKeys;
    }

    @Override
    public void setPreviouslyUsedKeys() {
        this.previouslyUsedKeys = this.currentlyUsedKeys.stream().filter(key -> key.continuous).collect(Collectors.toSet());
    }

    @Override
    public Set<Active.Key> getKeysToCheck() {
        return this.keysToCheck;
    }

    @Override
    public void addKeyToCheck(Active.Key key) {
        this.keysToCheck.add(key);
    }

    @Override
    public void addKey(Active.Key key) {
        this.currentlyUsedKeys.add(key);
    }

    @Override
    public void removeKey(Active.Key key) {
        this.currentlyUsedKeys.remove(key);
    }

    @Override
    public void tick() {
        int powerSize = PowerHolderComponent.KEY.get(this.provider).getPowers(Power.class, true).size();
        if (this.previousPowerSize != powerSize) {
            this.keysToCheck.clear();
            this.previouslyUsedKeys.clear();
            this.currentlyUsedKeys.clear();
        }
        this.previousPowerSize = powerSize;
    }

    @Override
    public void writeSyncPacket(FriendlyByteBuf buf, ServerPlayer recipient) {
        buf.writeInt(this.currentlyUsedKeys.size());
        for (Active.Key key : this.currentlyUsedKeys) {
            ApoliDataTypes.KEY.send(buf, key);
        }

        buf.writeInt(this.previouslyUsedKeys.size());
        for (Active.Key key : this.previouslyUsedKeys) {
            ApoliDataTypes.KEY.send(buf, key);
        }
    }

    public void applySyncPacket(FriendlyByteBuf buf) {
        int keysToAddSize = buf.readInt();
        for (int i = 0; i < keysToAddSize; ++i) {
            this.currentlyUsedKeys.add(ApoliDataTypes.KEY.receive(buf));
        }

        int previousKeysToAddSize = buf.readInt();
        for (int i = 0; i < previousKeysToAddSize; ++i) {
            this.previouslyUsedKeys.add(ApoliDataTypes.KEY.receive(buf));
        }
    }
}
