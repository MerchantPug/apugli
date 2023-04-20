package net.merchantpug.apugli.networking.c2s;

import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.Active;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.merchantpug.apugli.Apugli;
import net.merchantpug.apugli.component.ApugliEntityComponents;
import net.merchantpug.apugli.component.KeyPressComponent;
import net.merchantpug.apugli.networking.ApugliPackets;
import net.merchantpug.apugli.networking.s2c.SyncKeysLessenedPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public record UpdateKeysPressedPacket(Set<Active.Key> addedKeys,
                                      Set<Active.Key> removedKeys) implements ApugliPacketC2S {
    public static final ResourceLocation ID = Apugli.asResource("update_keys_pressed");

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(addedKeys.size());
        for (Active.Key key : addedKeys) {
            ApoliDataTypes.KEY.send(buf, key);
        }

        buf.writeInt(removedKeys.size());
        for (Active.Key key : removedKeys) {
            ApoliDataTypes.KEY.send(buf, key);
        }
    }

    public static UpdateKeysPressedPacket decode(FriendlyByteBuf buf) {
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
    public ResourceLocation getFabricId() {
        return ID;
    }

    @Override
    public void handle(MinecraftServer server, ServerPlayer player) {
        server.execute(() -> {
            KeyPressComponent component = ApugliEntityComponents.KEY_PRESS_COMPONENT.get(player);
            addedKeys.forEach(component::addKey);
            removedKeys.forEach(component::removeKey);

            Set<Active.Key> keysToCheck = component.getKeysToCheck().stream().filter(key -> !component.getPreviousKeysToCheck().add(key)).collect(Collectors.toSet());
            component.setPreviousKeysToCheck();
            Set<Active.Key> keysToAdd = component.getCurrentlyUsedKeys().stream().filter(key -> !component.getPreviouslyUsedKeys().contains(key)).collect(Collectors.toSet());
            Set<Active.Key> keysToRemove = component.getPreviouslyUsedKeys().stream().filter(key -> !component.getCurrentlyUsedKeys().contains(key)).collect(Collectors.toSet());
            component.setPreviouslyUsedKeys();

            for (ServerPlayer otherPlayer : PlayerLookup.tracking(player))
                ApugliPackets.sendS2C(new SyncKeysLessenedPacket(player.getId(), keysToCheck, keysToAdd, keysToRemove), otherPlayer);
        });
    }
}
