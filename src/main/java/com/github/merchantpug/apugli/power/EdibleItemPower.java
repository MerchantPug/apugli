package com.github.merchantpug.apugli.power;

import com.github.merchantpug.apugli.Apugli;
import com.github.merchantpug.apugli.access.ItemStackAccess;
import com.github.merchantpug.apugli.networking.ApugliPackets;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import com.github.merchantpug.apugli.util.StackFoodComponentUtil;
import com.github.merchantpug.apugli.util.ItemStackFoodComponentUtil;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.UseAction;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class EdibleItemPower extends Power {
    public final Predicate<ItemStack> predicate;
    public final FoodComponent foodComponent;
    public final UseAction useAction;
    public final ItemStack returnStack;
    public final SoundEvent sound;
    private final Consumer<Entity> entityActionWhenEaten;
    private final int tickRate;

    public static PowerFactory<?> getFactory() {
        return new PowerFactory<EdibleItemPower>(Apugli.identifier("edible_item"),
                new SerializableData()
                        .add("item_condition", ApoliDataTypes.ITEM_CONDITION)
                        .add("food_component", SerializableDataTypes.FOOD_COMPONENT)
                        .add("use_action", SerializableDataTypes.USE_ACTION, null)
                        .add("return_stack", SerializableDataTypes.ITEM_STACK, null)
                        .add("sound", SerializableDataTypes.SOUND_EVENT, null)
                        .add("entity_action", ApoliDataTypes.ENTITY_ACTION, null)
                        .add("tick_rate", SerializableDataTypes.INT, 10),
                data ->
                        (type, player) -> new EdibleItemPower(type, player,
                                    (ConditionFactory<ItemStack>.Instance)data.get("item_condition"),
                                    (FoodComponent)data.get("food_component"),
                                    (UseAction)data.get("use_action"),
                                    (ItemStack)data.get("return_stack"),
                                    (SoundEvent)data.get("sound"),
                                    (ActionFactory<Entity>.Instance)data.get("entity_action"),
                                    data.getInt("tick_rate")))
                .allowCondition();
    }

    public EdibleItemPower(PowerType<?> type, LivingEntity entity, Predicate<ItemStack> predicate, FoodComponent foodComponent, UseAction useAction, ItemStack returnStack, SoundEvent sound, Consumer<Entity> entityActionWhenEaten, int tickRate) {
        super(type, entity);
        this.predicate = predicate;
        this.foodComponent = foodComponent;
        this.useAction = useAction;
        this.returnStack = returnStack;
        this.sound = sound;
        this.entityActionWhenEaten = entityActionWhenEaten;
        this.tickRate = tickRate;
    }

    // This method will exist as long as the tick() method only functions serverside, I'd be sending a million packets which have desync otherwise
    public void tempTick() {
        ItemStack mainHandStack = entity.getEquippedStack(EquipmentSlot.MAINHAND);
        ItemStack offHandStack = entity.getEquippedStack(EquipmentSlot.OFFHAND);
        if (entity.age % tickRate == 0) {
            if (mainHandStack != ItemStack.EMPTY) {
                if (this.predicate.test(mainHandStack) && this.isActive() && !((ItemStackAccess)(Object)mainHandStack).isItemStackFood() && ((ItemStackAccess)(Object)mainHandStack).getItemStackFoodComponent() == null) {
                    ItemStackFoodComponentUtil.setStackFood(mainHandStack, foodComponent, useAction, returnStack, sound);
                }
                if (this.predicate.test(mainHandStack) && !this.isActive() && ((ItemStackAccess)(Object)mainHandStack).getItemStackFoodComponent() == foodComponent) {
                    ItemStackFoodComponentUtil.removeStackFood(mainHandStack);
                }
            }
            if (offHandStack != ItemStack.EMPTY) {
                if (this.predicate.test(offHandStack) && this.isActive() && !((ItemStackAccess)(Object)mainHandStack).isItemStackFood() && ((ItemStackAccess)(Object)mainHandStack).getItemStackFoodComponent() == null) {
                    ItemStackFoodComponentUtil.setStackFood(offHandStack, foodComponent, useAction, returnStack, sound);
                }
                if (this.predicate.test(offHandStack) && !this.isActive() && ((ItemStackAccess)(Object)offHandStack).getItemStackFoodComponent() == foodComponent) {
                    ItemStackFoodComponentUtil.removeStackFood(offHandStack);
                }
            }
        }
    }

    public boolean doesApply(ItemStack stack) {
        return this.predicate.test(stack);
    }

    public void eat() {
        if(entityActionWhenEaten != null) {
            entityActionWhenEaten.accept(entity);
        }
    }

    @Override
    public void onRemoved() {
        if (entity instanceof PlayerEntity) {
            for (int i = 0; i < ((PlayerEntity) entity).getInventory().main.size(); i++) {
                ItemStack itemStack = ((PlayerEntity)entity).getInventory().main.get(i);
                if (predicate.test(itemStack) && ((ItemStackAccess)(Object)itemStack).getItemStackFoodComponent() == foodComponent) {
                    ItemStackFoodComponentUtil.removeStackFood(itemStack);
                    this.sendFoodComponentRemovePacket(null, StackFoodComponentUtil.InventoryLocation.MAIN, i);
                }
            }
            for (int i = 0; i < ((PlayerEntity) entity).getInventory().armor.size(); i++) {
                ItemStack armorStack = ((PlayerEntity) entity).getInventory().getArmorStack(i);
                if (predicate.test(armorStack) && ((ItemStackAccess)(Object)armorStack).getItemStackFoodComponent() == foodComponent) {
                    ItemStackFoodComponentUtil.removeStackFood(armorStack);
                    this.sendFoodComponentRemovePacket(null, StackFoodComponentUtil.InventoryLocation.ARMOR, i);
                }
            }
            ItemStack offHandStack = entity.getEquippedStack(EquipmentSlot.OFFHAND);
            if (predicate.test(offHandStack) && ((ItemStackAccess)(Object)offHandStack).getItemStackFoodComponent() == foodComponent) {
                ItemStackFoodComponentUtil.removeStackFood(offHandStack);
                this.sendFoodComponentRemovePacket(EquipmentSlot.OFFHAND, null, 0);
            }
        }
    }

    public void sendFoodComponentRemovePacket(@Nullable EquipmentSlot equipmentSlot, @Nullable StackFoodComponentUtil.InventoryLocation inventoryLocation, int inventoryIndex) {
        if (entity.world.isClient()) return;
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeInt(entity.getId());
        buf.writeBoolean(equipmentSlot != null);
        if (equipmentSlot != null) {
            buf.writeString(equipmentSlot.getName(), PacketByteBuf.DEFAULT_MAX_STRING_LENGTH);
        }

        buf.writeBoolean(inventoryLocation != null);
        if (inventoryLocation != null) {
            buf.writeByte(inventoryLocation.ordinal());
            buf.writeInt(inventoryIndex);
        }

        for (ServerPlayerEntity player : PlayerLookup.tracking(entity)) {
            ServerPlayNetworking.send(player, ApugliPackets.REMOVE_STACK_FOOD_COMPONENT, buf);
        }
        if (!(entity instanceof ServerPlayerEntity)) return;
        ServerPlayNetworking.send((ServerPlayerEntity)entity, ApugliPackets.REMOVE_STACK_FOOD_COMPONENT, buf);
    }
}
