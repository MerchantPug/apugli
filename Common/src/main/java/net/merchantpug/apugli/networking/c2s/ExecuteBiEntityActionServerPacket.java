
package net.merchantpug.apugli.networking.c2s;

import net.merchantpug.apugli.Apugli;
import net.merchantpug.apugli.networking.ApugliPacket;
import net.merchantpug.apugli.platform.Services;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

public record ExecuteBiEntityActionServerPacket<A>(int otherEntityId, boolean isOtherEntityTarget, A biEntityAction) implements ApugliPacket {
    public static final ResourceLocation ID = Apugli.asResource("execute_bientity_action_serverside");

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(otherEntityId);
        buf.writeBoolean(isOtherEntityTarget);
        Services.ACTION.biEntityDataType().send(buf, biEntityAction);
    }

    public static <A> ExecuteBiEntityActionServerPacket<A> decode(FriendlyByteBuf buf) {
        int otherEntityId = buf.readInt();
        boolean isOtherEntityTarget = buf.readBoolean();
        A entityAction = (A) Services.ACTION.entityDataType().receive(buf);
        return new ExecuteBiEntityActionServerPacket<>(otherEntityId, isOtherEntityTarget, entityAction);
    }

    @Override
    public ResourceLocation getFabricId() {
        return ID;
    }

    public static class Handler {
        public static void handle(ExecuteBiEntityActionServerPacket<?> packet, MinecraftServer server, ServerPlayer player) {
            server.execute(() -> {
                Entity otherEntity = player.getLevel().getEntity(packet.otherEntityId);

                Entity actor = packet.isOtherEntityTarget ? player : otherEntity;
                Entity target = packet.isOtherEntityTarget ? otherEntity : player;

                Services.ACTION.executeBiEntity(packet.biEntityAction, actor, target);
            });
        }
    }

}
