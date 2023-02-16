package net.merchantpug.apugli.networking.c2s;

import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.Active;
import net.merchantpug.apugli.Apugli;
import net.merchantpug.apugli.component.ApugliEntityComponents;
import net.merchantpug.apugli.component.KeyPressComponent;
import net.merchantpug.apugli.networking.ApugliPackets;
import net.merchantpug.apugli.networking.s2c.SyncKeysLessenedPacket;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public record UpdateKeysPressedPacket(Set<Active.Key> addedKeys,
                                Set<Active.Key> removedKeys) implements ApugliPacketC2S {
    public static final Identifier ID = Apugli.identifier("update_keys_pressed");

    @Override
    public void encode(PacketByteBuf buf) {
        buf.writeInt(addedKeys.size());
        for (Active.Key key : addedKeys) {
            ApoliDataTypes.KEY.send(buf, key);
        }

        buf.writeInt(removedKeys.size());
        for (Active.Key key : removedKeys) {
            ApoliDataTypes.KEY.send(buf, key);
        }
    }

    public static UpdateKeysPressedPacket decode(PacketByteBuf buf) {
        Set<Active.Key> addedKeys = new HashSet<>();
        int addedKeySize = buf.readInt();
        for (int i = 0; i < addedKeySize; ++i) {
            addedKeys.add(ApoliDataTypes.KEY.receive(buf));
        }

        Set<Active.Key> removedKeys = new HashSet<>();
        int removedKeySize = buf.readInt();
        for (int i = 0; i < removedKeySize; ++i) {
            removedKeys.add(ApoliDataTypes.KEY.receive(buf));
        }

        return new UpdateKeysPressedPacket(addedKeys, removedKeys);
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public void handle(MinecraftServer server, ServerPlayerEntity player) {
        server.execute(() -> {
            KeyPressComponent component = ApugliEntityComponents.KEY_PRESS_COMPONENT.get(player);
            addedKeys.forEach(component::addKey);
            removedKeys.forEach(component::removeKey);

            List<Active.Key> keysToCheck = component.getKeysToCheck().stream().filter(key -> !component.getPreviousKeysToCheck().add(key)).toList();
            component.setPreviousKeysToCheck();
            List<Active.Key> keysToAdd = component.getCurrentlyUsedKeys().stream().filter(key -> !component.getPreviouslyUsedKeys().contains(key)).toList();
            List<Active.Key> keysToRemove = component.getPreviouslyUsedKeys().stream().filter(key -> !component.getCurrentlyUsedKeys().contains(key)).toList();
            component.setPreviouslyUsedKeys();

            for (ServerPlayerEntity otherPlayer : server.getPlayerManager().getPlayerList())
                ApugliPackets.sendS2C(new SyncKeysLessenedPacket(player.getId(), keysToCheck, keysToAdd, keysToRemove), otherPlayer);
        });
    }
}
