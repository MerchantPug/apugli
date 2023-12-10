package net.merchantpug.apugli.network.s2c;

import net.merchantpug.apugli.Apugli;
import net.merchantpug.apugli.platform.Services;
import net.merchantpug.apugli.registry.power.ApugliPowers;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public record ModifyEnchantmentLevelPacket(int entityId, ResourceLocation powerId, boolean remove) implements ApugliPacketS2C {
    public static final ResourceLocation ID = Apugli.asResource("modify_enchantment_level");

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(this.entityId());
        buf.writeResourceLocation(this.powerId());
        buf.writeBoolean(this.remove());
    }

    public static ModifyEnchantmentLevelPacket decode(FriendlyByteBuf buf) {
        return new ModifyEnchantmentLevelPacket(buf.readInt(), buf.readResourceLocation(), buf.readBoolean());
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
                Entity entity = Minecraft.getInstance().level.getEntity(entityId());
                if (!(entity instanceof LivingEntity living)) {
                    return;
                }

                Object power = Services.POWER.getPowers(living, ApugliPowers.MODIFY_ENCHANTMENT_LEVEL.get()).stream().filter(p -> Services.POWER.getPowerId(p) == powerId()).findFirst();
                if (remove()) {
                    ApugliPowers.MODIFY_ENCHANTMENT_LEVEL.get().onRemoved(power, living);
                } else {
                    ApugliPowers.MODIFY_ENCHANTMENT_LEVEL.get().onAdded(power, living);
                }
            }
        });
    }
}
