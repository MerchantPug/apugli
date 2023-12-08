
package net.merchantpug.apugli.network.s2c;

import io.github.apace100.apoli.Apoli;
import io.github.edwinmindcraft.apoli.api.ApoliAPI;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.apoli.api.component.IPowerContainer;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import io.github.edwinmindcraft.apoli.api.power.factory.PowerFactory;
import net.merchantpug.apugli.Apugli;
import net.merchantpug.apugli.capability.entity.HitsOnTargetCapability;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public record SyncSinglePowerPacket(int entityId,
                                    ResourceLocation powerId,
                                    CompoundTag powerDataTag) implements ApugliPacketS2C {

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(this.entityId());
        buf.writeResourceLocation(this.powerId());
        buf.writeNbt(this.powerDataTag());
    }

    public static SyncSinglePowerPacket decode(FriendlyByteBuf buf) {
        return new SyncSinglePowerPacket(buf.readInt(), buf.readResourceLocation(), buf.readNbt());
    }

    @Override
    public ResourceLocation getFabricId() {
        throw new RuntimeException("ApugliPacket#getFabricId is not meant to be used in Forge specific packets.");
    }

    @Override
    public void handle() {
        // The lambda implementation of this Runnable breaks Forge servers.
        Minecraft.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                Entity entity = Minecraft.getInstance().level.getEntity(entityId);

                if (!(entity instanceof LivingEntity)) {
                    Apugli.LOG.warn("Could not find living entity to sync configured power with.");
                    return;
                }

                IPowerContainer container = ApoliAPI.getPowerContainer(entity);
                Holder<ConfiguredPower<IDynamicFeatureConfiguration, PowerFactory<IDynamicFeatureConfiguration>>> configuredPower = container.getPower(powerId());
                if (configuredPower != null && configuredPower.isBound()) {
                    if (!powerDataTag().isEmpty()) {
                        configuredPower.value().deserialize(container, powerDataTag());
                    }
                } else {
                    Apugli.LOG.warn("Invalid power container capability for entity {}", entity.getScoreboardName());
                }
            }
        });
    }
}
