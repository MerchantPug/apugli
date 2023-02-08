package net.merchantpug.apugli.networking.s2c;

import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.Active;
import net.merchantpug.apugli.Apugli;
import net.merchantpug.apugli.component.ApugliEntityComponents;
import net.merchantpug.apugli.component.KeyPressComponent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public record SendKeyToCheckPacket(Active.Key key) implements ApugliPacketS2C {
    public static final Identifier ID = Apugli.identifier("send_key_to_check");

    @Override
    public void encode(PacketByteBuf buf) {
        ApoliDataTypes.KEY.send(buf, key);
    }

    public static SendKeyToCheckPacket decode(PacketByteBuf buf) {
        Active.Key key = ApoliDataTypes.KEY.receive(buf);
        return new SendKeyToCheckPacket(key);
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public void handle(MinecraftClient client) {
        client.execute(() -> {
            PlayerEntity player = MinecraftClient.getInstance().player;
            if (MinecraftClient.getInstance().player == null) {
                Apugli.LOGGER.warn("Could not find client player.");
                return;
            }
            KeyPressComponent component = ApugliEntityComponents.KEY_PRESS_COMPONENT.get(player);
            component.getKeysToCheck().add(key);
            component.changePreviousKeysToCheckToCurrent();
        });
    }
}
