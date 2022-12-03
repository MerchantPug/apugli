package net.merchantpug.apugli.condition.entity;

import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.Active;
import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.calio.data.SerializableData;
import io.netty.buffer.Unpooled;
import io.netty.buffer.UnpooledUnsafeDirectByteBuf;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.merchantpug.apugli.Apugli;
import net.merchantpug.apugli.ApugliClient;
import net.merchantpug.apugli.component.ApugliEntityComponents;
import net.merchantpug.apugli.component.KeyPressComponent;
import net.merchantpug.apugli.networking.ApugliPackets;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

public class KeyPressedCondition {
    public static boolean condition(SerializableData.Instance data, Entity entity) {
        if (entity instanceof PlayerEntity player) {
            Active.Key key = data.get("key");
            KeyPressComponent component = ApugliEntityComponents.KEY_PRESS_COMPONENT.get(player);
            if (!component.getKeysToCheck().contains(key)) {
                if (!player.world.isClient) {
                    component.addKeyToCheck(key);
                    PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
                    ApoliDataTypes.KEY.send(buf, key);
                    ServerPlayNetworking.send((ServerPlayerEntity)player, ApugliPackets.SEND_KEY_TO_CHECK, buf);
                } else if (MinecraftClient.getInstance().getCameraEntity() == player) {
                    ApugliClient.handleActiveKeys(player);
                }
            }
            return component.getCurrentlyUsedKeys().contains(key);
        }
        return false;
    }

    public static ConditionFactory<Entity> getFactory() {
        return new ConditionFactory<>(Apugli.identifier("key_pressed"), new SerializableData()
                .add("key", ApoliDataTypes.KEY),
                KeyPressedCondition::condition
        );
    }
}
