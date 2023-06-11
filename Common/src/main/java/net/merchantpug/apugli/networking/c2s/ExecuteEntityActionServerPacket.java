
package net.merchantpug.apugli.networking.c2s;

import net.merchantpug.apugli.Apugli;
import net.merchantpug.apugli.networking.s2c.ApugliPacketS2C;
import net.merchantpug.apugli.platform.Services;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

public record ExecuteEntityActionServerPacket<A>(A entityAction) implements ApugliPacketC2S {
    public static final ResourceLocation ID = Apugli.asResource("execute_entity_action_serverside");

    @Override
    public void encode(FriendlyByteBuf buf) {
        Services.ACTION.entityDataType().send(buf, entityAction);
    }

    public static <A> ExecuteEntityActionServerPacket<A> decode(FriendlyByteBuf buf) {
        A entityAction = (A) Services.ACTION.entityDataType().receive(buf);
        return new ExecuteEntityActionServerPacket<>(entityAction);
    }

    @Override
    public ResourceLocation getFabricId() {
        return ID;
    }

    @Override
    public void handle(MinecraftServer server, ServerPlayer player) {
        server.execute(() -> Services.ACTION.executeEntity(entityAction, player));
    }

}
