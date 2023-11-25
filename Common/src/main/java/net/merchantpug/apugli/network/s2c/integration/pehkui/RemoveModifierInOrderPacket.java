package net.merchantpug.apugli.network.s2c.integration.pehkui;

import net.merchantpug.apugli.Apugli;
import net.merchantpug.apugli.integration.pehkui.ApoliScaleModifier;
import net.merchantpug.apugli.integration.pehkui.PehkuiUtil;
import net.merchantpug.apugli.network.s2c.ApugliPacketS2C;
import net.merchantpug.apugli.platform.Services;
import net.merchantpug.apugli.registry.power.ApugliPowers;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public record RemoveModifierInOrderPacket(int entityId,
                                          ResourceLocation powerId) implements ApugliPacketS2C {
    public static final ResourceLocation ID = Apugli.asResource("remove_modifiers_in_order");

    public static RemoveModifierInOrderPacket removePacket(int entityId, ResourceLocation powerId) {
        return new RemoveModifierInOrderPacket(entityId, powerId);
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(this.entityId());
        buf.writeResourceLocation(this.powerId());
    }

    public static RemoveModifierInOrderPacket decode(FriendlyByteBuf buf) {
        return new RemoveModifierInOrderPacket(buf.readInt(), buf.readResourceLocation());
    }

    @Override
    public ResourceLocation getFabricId() {
        return ID;
    }

    @Override
    public void handle() {
        // The lambda implementation of this Runnable breaks Forge servers.
        Minecraft.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                if (!Services.PLATFORM.isModLoaded("pehkui")) {
                    Apugli.LOG.warn("Attempted loading RemoveModifiersInOrderPacket without Pehkui.");
                    return;
                }

                Entity entity = Minecraft.getInstance().level.getEntity(entityId());

                if (!(entity instanceof LivingEntity living)) {
                    return;
                }

                Object object = ApugliPowers.MODIFY_SCALE.get().getApoliScaleModifier(powerId(), entity);
                if (!(object instanceof ApoliScaleModifier<?> apoliModifier)) {
                    Apugli.LOG.warn("Could not find ApoliScaleModifier for power id '{}' for syncing modifying order list.", powerId());
                    return;
                }

                PehkuiUtil.removeModifierFromOrderList(living, apoliModifier);
            }
        });

    }
}
