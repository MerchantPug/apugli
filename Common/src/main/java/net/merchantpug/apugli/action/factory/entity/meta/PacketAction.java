package net.merchantpug.apugli.action.factory.entity.meta;

import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataType;
import net.merchantpug.apugli.action.factory.IActionFactory;
import net.merchantpug.apugli.networking.c2s.ExecuteEntityActionServerPacket;
import net.merchantpug.apugli.networking.s2c.ExecuteEntityActionClientPacket;
import net.merchantpug.apugli.platform.Services;
import net.merchantpug.apugli.util.Side;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;

public class PacketAction implements IActionFactory<Entity> {

    @Override
    public SerializableData getSerializableData() {
        return new SerializableData()
                .add("side", SerializableDataType.enumValue(Side.class))
                .add("action", Services.ACTION.entityDataType());
    }

    @Override
    public void execute(SerializableData.Instance data, Entity entity) {
        if (entity.level.isClientSide && entity instanceof LocalPlayer && data.get("side") == Side.SERVER) {
            Services.PLATFORM.sendC2S(new ExecuteEntityActionServerPacket<>(data.get("action")));
        } else if (!entity.level.isClientSide && data.get("side") == Side.CLIENT) {
            Services.PLATFORM.sendS2CTrackingAndSelf(new ExecuteEntityActionClientPacket<>(entity.getId(), data.get("action")), entity);
        }
    }

}
