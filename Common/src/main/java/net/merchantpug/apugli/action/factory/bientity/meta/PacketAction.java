package net.merchantpug.apugli.action.factory.bientity.meta;

import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataType;
import net.merchantpug.apugli.action.factory.IActionFactory;
import net.merchantpug.apugli.network.c2s.ExecuteBiEntityActionServerPacket;
import net.merchantpug.apugli.network.s2c.ExecuteBiEntityActionClientPacket;
import net.merchantpug.apugli.platform.Services;
import net.merchantpug.apugli.util.Side;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;

public class PacketAction implements IActionFactory<Tuple<Entity, Entity>> {

    @Override
    public SerializableData getSerializableData() {
        return new SerializableData()
                .add("side", SerializableDataType.enumValue(Side.class))
                .add("action", Services.ACTION.biEntityDataType());
    }

    @Override
    public void execute(SerializableData.Instance data, Tuple<Entity, Entity> pair) {
        if (pair.getA().level.isClientSide && pair.getB().level.isClientSide && (pair.getA() instanceof LocalPlayer || pair.getB() instanceof LocalPlayer) && data.get("side") == Side.SERVER) {
            Services.PLATFORM.sendC2S(new ExecuteBiEntityActionServerPacket<>(pair.getA() instanceof LocalPlayer ? pair.getB().getId() : pair.getA().getId(), !(pair.getB() instanceof LocalPlayer), data.get("action")));
        } else if (!pair.getA().level.isClientSide && !pair.getB().level.isClientSide && data.get("side") == Side.CLIENT) {
            Services.PLATFORM.sendS2CTrackingAndSelf(new ExecuteBiEntityActionClientPacket<>(pair.getA().getId(), pair.getB().getId(), data.get("action")), pair.getA());
        }
    }

}
