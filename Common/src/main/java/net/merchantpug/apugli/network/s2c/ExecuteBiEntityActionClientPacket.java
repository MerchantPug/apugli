
package net.merchantpug.apugli.network.s2c;

import net.merchantpug.apugli.Apugli;
import net.merchantpug.apugli.network.ApugliPacket;
import net.merchantpug.apugli.platform.Services;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public record ExecuteBiEntityActionClientPacket<A>(int actorId, int targetId, A entityAction) implements ApugliPacket {
    public static final ResourceLocation ID = Apugli.asResource("execute_bientity_action_clientside");

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(actorId);
        buf.writeInt(targetId);
        Services.ACTION.entityDataType().send(buf, entityAction);
    }

    public static <A> ExecuteBiEntityActionClientPacket<A> decode(FriendlyByteBuf buf) {
        int actorId = buf.readInt();
        int targetId = buf.readInt();
        A entityAction = (A) Services.ACTION.entityDataType().receive(buf);
        return new ExecuteBiEntityActionClientPacket<>(actorId, targetId, entityAction);
    }

    @Override
    public ResourceLocation getFabricId() {
        return ID;
    }

    public static class Handler {
        public static void handle(ExecuteBiEntityActionClientPacket<?> packet) {
            Minecraft.getInstance().execute(() -> {
                Level level = Minecraft.getInstance().level;
                Entity actor = level.getEntity(packet.actorId);
                Entity target = level.getEntity(packet.targetId);
                if (actor == null) {
                    Apugli.LOG.warn("Could not find actor entity for clientsided bi-entity action.");
                    return;
                } else if (target == null) {
                    Apugli.LOG.warn("Could not find target entity for clientsided bi-entity action.");
                    return;
                }
                Services.ACTION.executeBiEntity(packet.entityAction, actor, target);
            });
        }
    }
}
