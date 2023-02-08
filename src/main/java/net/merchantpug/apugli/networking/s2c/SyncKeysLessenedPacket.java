package net.merchantpug.apugli.networking.s2c;

import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.Active;
import net.merchantpug.apugli.Apugli;
import net.merchantpug.apugli.component.ApugliEntityComponents;
import net.merchantpug.apugli.component.KeyPressComponent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public record SyncKeysLessenedPacket(int entityId,
                                     List<Active.Key> keysToCheck,
                                     List<Active.Key> addedKeys,
                                     List<Active.Key> removedKeys) implements ApugliPacketS2C {
    public static final Identifier ID = Apugli.identifier("sync_keys_lessened");

    @Override
    public void encode(PacketByteBuf buf) {
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

    public static SyncKeysLessenedPacket decode(PacketByteBuf buf) {
        int entityId = buf.readInt();
        List<Active.Key> keysToCheck = new ArrayList<>();
        int keysToCheckSize = buf.readInt();
        for (int i = 0; i < keysToCheckSize; ++i) {
            keysToCheck.add(ApoliDataTypes.KEY.receive(buf));
        }

        List<Active.Key> addedKeys = new ArrayList<>();
        int keysToAddSize = buf.readInt();
        for (int i = 0; i < keysToAddSize; ++i) {
            addedKeys.add(ApoliDataTypes.KEY.receive(buf));
        }

        List<Active.Key> removedKeys = new ArrayList<>();
        int keysToRemoveSize = buf.readInt();
        for (int i = 0; i < keysToRemoveSize; ++i) {
            removedKeys.add(ApoliDataTypes.KEY.receive(buf));
        }

        return new SyncKeysLessenedPacket(entityId, keysToCheck, addedKeys, removedKeys);
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public void handle(MinecraftClient client) {
        client.execute(() -> {
            Entity entity = client.world.getEntityById(entityId);
            if (!(entity instanceof PlayerEntity player)) {
                Apugli.LOGGER.warn("Could not find player entity to sync keys with.");
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
