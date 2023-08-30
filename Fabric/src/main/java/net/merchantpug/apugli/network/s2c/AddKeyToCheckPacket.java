package net.merchantpug.apugli.network.s2c;

import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.Active;
import net.merchantpug.apugli.Apugli;
import net.merchantpug.apugli.component.ApugliEntityComponents;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public record AddKeyToCheckPacket(int entityId, Active.Key key) implements ApugliPacketS2C {
    public static final ResourceLocation ID = Apugli.asResource("add_key_to_check");

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(this.entityId);
        ApoliDataTypes.KEY.send(buf, this.key);
    }

    public static AddKeyToCheckPacket decode(FriendlyByteBuf buf) {
        int entityId = buf.readInt();
        Active.Key key = ApoliDataTypes.KEY.receive(buf);
        return new AddKeyToCheckPacket(entityId, key);
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
            if (!player.isLocalPlayer()) return;

            ApugliEntityComponents.KEY_PRESS_COMPONENT.get(player).addKeyToCheck(key);
        });
    }
}
