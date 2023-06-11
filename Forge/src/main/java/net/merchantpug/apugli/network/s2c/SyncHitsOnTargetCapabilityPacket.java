
package net.merchantpug.apugli.network.s2c;

import net.merchantpug.apugli.Apugli;
import net.merchantpug.apugli.capability.HitsOnTargetCapability;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.HashMap;
import java.util.Map;

public record SyncHitsOnTargetCapabilityPacket(int entityId,
                                               Map<Integer, Tuple<Integer, Integer>> hits) implements ApugliPacketS2C {

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(entityId);

        buf.writeInt(hits.size());

        hits.forEach((entityId, value) -> {
            buf.writeInt(entityId);
            buf.writeInt(value.getA());
            buf.writeInt(value.getB());
        });
    }

    public static SyncHitsOnTargetCapabilityPacket decode(FriendlyByteBuf buf) {
        int entityId = buf.readInt();

        int hitsSize = buf.readInt();

        Map<Integer, Tuple<Integer, Integer>> hits = new HashMap<>();
        for (int i = 0; i < hitsSize; ++i) {
            int otherEntityId = buf.readInt();
            int amount = buf.readInt();
            int ticksLeft = buf.readInt();
            hits.put(otherEntityId, new Tuple<>(amount, ticksLeft));
        }
        return new SyncHitsOnTargetCapabilityPacket(entityId, hits);
    }

    @Override
    public ResourceLocation getFabricId() {
        throw new RuntimeException("ApugliPacket#getFabricId is not meant to be used in Forge specific packets.");
    }

    @Override
    public void handle() {
        Minecraft.getInstance().execute(() -> {
            Entity entity = Minecraft.getInstance().level.getEntity(entityId);

            if (!(entity instanceof LivingEntity)) {
                Apugli.LOG.warn("Could not find living entity to sync hits on target with.");
                return;
            }

            entity.getCapability(HitsOnTargetCapability.INSTANCE).ifPresent(capability -> {
                capability.getHits().clear();
                hits.forEach((id, value) -> capability.setHits(id, value.getA(), value.getB()));
            });
        });
    }
}
