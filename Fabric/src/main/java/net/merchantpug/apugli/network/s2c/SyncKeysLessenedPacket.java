package net.merchantpug.apugli.network.s2c;

import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.Active;
import net.merchantpug.apugli.Apugli;
import net.merchantpug.apugli.component.ApugliEntityComponents;
import net.merchantpug.apugli.component.KeyPressComponent;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

import java.util.HashSet;
import java.util.Set;

public record SyncKeysLessenedPacket(int entityId,
                                     Set<Active.Key> keysToCheck,
                                     Set<Active.Key> addedKeys,
                                     Set<Active.Key> removedKeys) implements ApugliPacketS2C {
    public static final ResourceLocation ID = Apugli.asResource("sync_keys_lessened");

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(entityId);

        buf.writeInt(keysToCheck.size());
        for (Active.Key key : keysToCheck) {
            ApoliDataTypes.KEY.send(buf, key);
        }

        buf.writeInt(addedKeys.size());
        for (Active.Key key : addedKeys) {
            ApoliDataTypes.KEY.send(buf, key);
        }

        buf.writeInt(removedKeys.size());
        for (Active.Key key : removedKeys) {
            ApoliDataTypes.KEY.send(buf, key);
        }
    }

    public static SyncKeysLessenedPacket decode(FriendlyByteBuf buf) {
        int entityId = buf.readInt();

        Set<Active.Key> keysToCheck = new HashSet<>();
        int keysToCheckSize = buf.readInt();
        for (int i = 0; i < keysToCheckSize; ++i) {
            keysToCheck.add(ApoliDataTypes.KEY.receive(buf));
        }

        Set<Active.Key> addedKeys = new HashSet<>();
        int keysToAddSize = buf.readInt();
        for (int i = 0; i < keysToAddSize; ++i) {
            addedKeys.add(ApoliDataTypes.KEY.receive(buf));
        }

        Set<Active.Key> removedKeys = new HashSet<>();
        int keysToRemoveSize = buf.readInt();
        for (int i = 0; i < keysToRemoveSize; ++i) {
            removedKeys.add(ApoliDataTypes.KEY.receive(buf));
        }

        return new SyncKeysLessenedPacket(entityId, keysToCheck, addedKeys, removedKeys);
    }

    @Override
    public ResourceLocation getFabricId() {
        return ID;
    }

    @Override
    public void handle() {
        Minecraft.getInstance().execute(() -> {
            Entity entity = Minecraft.getInstance().level.getEntity(entityId);
            if (!(entity instanceof Player player)) {
                Apugli.LOG.warn("Could not find player entity to sync keys with.");
                return;
            }
            KeyPressComponent component = ApugliEntityComponents.KEY_PRESS_COMPONENT.get(player);

            for (Active.Key key : keysToCheck) {
                component.addKeyToCheck(key);
            }

            for (Active.Key key : addedKeys) {
                component.addKey(key);
            }

            for (Active.Key key : removedKeys) {
                component.removeKey(key);
            }
        });
    }
}
