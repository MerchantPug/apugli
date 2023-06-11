
package net.merchantpug.apugli.network.s2c;

import net.merchantpug.apugli.Apugli;
import net.merchantpug.apugli.network.ApugliPacket;
import net.merchantpug.apugli.platform.Services;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public record ExecuteEntityActionClientPacket<A>(int entityId, A entityAction) implements ApugliPacket {
    public static final ResourceLocation ID = Apugli.asResource("execute_entity_action_clientside");

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(entityId);
        Services.ACTION.entityDataType().send(buf, entityAction);
    }

    public static <A> ExecuteEntityActionClientPacket<A> decode(FriendlyByteBuf buf) {
        int entityId = buf.readInt();
        A entityAction = (A) Services.ACTION.entityDataType().receive(buf);
        return new ExecuteEntityActionClientPacket<>(entityId, entityAction);
    }

    @Override
    public ResourceLocation getFabricId() {
        return ID;
    }

    public static class Handler {
        public static void handle(ExecuteEntityActionClientPacket<?> packet) {
            Minecraft.getInstance().execute(() -> {
                Level level = Minecraft.getInstance().level;
                Entity entity = level.getEntity(packet.entityId);
                if (entity == null) {
                    Apugli.LOG.warn("Could not find entity for clientsided entity action.");
                    return;
                }
                Services.ACTION.executeEntity(packet.entityAction, entity);
            });
        }
    }
}
