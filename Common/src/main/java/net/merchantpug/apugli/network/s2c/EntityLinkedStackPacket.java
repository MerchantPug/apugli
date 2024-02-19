package net.merchantpug.apugli.network.s2c;

import net.merchantpug.apugli.Apugli;
import net.merchantpug.apugli.platform.Services;
import net.merchantpug.apugli.power.factory.ModifyEnchantmentLevelPowerFactory;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public record EntityLinkedStackPacket(int entityId, List<Integer> slotIndexes) implements ApugliPacketS2C {
    public static final ResourceLocation ID = Apugli.asResource("empty_stacks");

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(this.entityId());
        buf.writeInt(slotIndexes().size());
        for (int i = 0; i < slotIndexes().size(); ++i) {
            buf.writeInt(slotIndexes().get(i));
        }
    }

    public static EntityLinkedStackPacket decode(FriendlyByteBuf buf) {
        int entityId = buf.readInt();
        int slotIndexSize = buf.readInt();

        List<Integer> slotIndexes = new ArrayList<>();
        for (int i = 0; i < slotIndexSize; ++i) {
            slotIndexes.add(buf.readInt());
        }
        return new EntityLinkedStackPacket(entityId, slotIndexes);
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

                for (int slot : slotIndexes()) {

                    SlotAccess slotAccess = entity.getSlot(slot);
                    if (slotAccess == SlotAccess.NULL) {
                        continue;
                    }

                    ItemStack stack = slotAccess.get();
                    if (stack.isEmpty() && !ModifyEnchantmentLevelPowerFactory.isWorkableEmptyStack(entity, slotAccess)) {
                        slotAccess.set(ModifyEnchantmentLevelPowerFactory.getWorkableEmptyStack(entity));
                    } else {
                        Services.PLATFORM.setEntityToItemStack(stack, entity);
                    }
                }

            }
        });
    }
}
