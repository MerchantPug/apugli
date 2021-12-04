package io.github.merchantpug.apugli.power;

import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.merchantpug.apugli.Apugli;
import io.github.merchantpug.apugli.networking.ApugliPackets;
import io.github.merchantpug.apugli.util.ApugliDataTypes;
import io.github.merchantpug.apugli.util.StackFoodComponentUtil;
import io.github.merchantpug.nibbles.ItemStackFoodComponentAPI;
import io.github.merchantpug.nibbles.access.ItemStackAccess;
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
    private final Predicate<ItemStack> predicate;
    private final FoodComponent foodComponent;
    private final UseAction useAction;
    private final ItemStack returnStack;
    private final SoundEvent sound;
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
        this.setTicking(true);
    }

    @Override
    public void tick() {
        if (entity.age % tickRate == 0) {
            ItemStack mainHandStack = entity.getEquippedStack(EquipmentSlot.MAINHAND);
            if (mainHandStack != ItemStack.EMPTY) {
                if (this.isActive()) {
                    if (this.predicate.test(mainHandStack) && ((ItemStackAccess)(Object)mainHandStack).getItemStackFoodComponent() != foodComponent) {
                        ItemStackFoodComponentAPI.setStackFood(mainHandStack, foodComponent, useAction, returnStack, sound);
                        this.sendFoodComponentSyncPacket(StackFoodComponentUtil.FoodComponentAction.ADD, EquipmentSlot.MAINHAND, null, 0);
                    }
                } else {
                    if (this.predicate.test(mainHandStack) && ((ItemStackAccess)(Object)mainHandStack).getItemStackFoodComponent() == foodComponent) {
                        ItemStackFoodComponentAPI.removeStackFood(mainHandStack);
                        this.sendFoodComponentSyncPacket(StackFoodComponentUtil.FoodComponentAction.REMOVE, EquipmentSlot.MAINHAND, null, 0);
                    }
                }
            }
            ItemStack offHandStack = entity.getEquippedStack(EquipmentSlot.OFFHAND);
            if (offHandStack != ItemStack.EMPTY) {
                if (this.isActive()) {
                    if (this.predicate.test(offHandStack) && ((ItemStackAccess)(Object)offHandStack).getItemStackFoodComponent() != foodComponent) {
                        ItemStackFoodComponentAPI.setStackFood(offHandStack, foodComponent, useAction, returnStack, sound);
                        this.sendFoodComponentSyncPacket(StackFoodComponentUtil.FoodComponentAction.ADD, EquipmentSlot.OFFHAND, null, 0);
                    }
                } else {
                    if (this.predicate.test(offHandStack) && ((ItemStackAccess)(Object)offHandStack).getItemStackFoodComponent() == foodComponent) {
                        ItemStackFoodComponentAPI.removeStackFood(offHandStack);
                        this.sendFoodComponentSyncPacket(StackFoodComponentUtil.FoodComponentAction.REMOVE, EquipmentSlot.OFFHAND, null, 0);
                    }
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
                    ItemStackFoodComponentAPI.removeStackFood(itemStack);
                    this.sendFoodComponentSyncPacket(StackFoodComponentUtil.FoodComponentAction.REMOVE, null, StackFoodComponentUtil.InventoryLocation.MAIN, i);
                }
            }
            for (int i = 0; i < ((PlayerEntity) entity).getInventory().armor.size(); i++) {
                ItemStack armorStack = ((PlayerEntity) entity).getInventory().getArmorStack(i);
                if (predicate.test(armorStack) && ((ItemStackAccess)(Object)armorStack).getItemStackFoodComponent() == foodComponent) {
                    ItemStackFoodComponentAPI.removeStackFood(armorStack);
                    this.sendFoodComponentSyncPacket(StackFoodComponentUtil.FoodComponentAction.REMOVE, null, StackFoodComponentUtil.InventoryLocation.ARMOR, i);
                }
            }
            ItemStack offHandStack = entity.getEquippedStack(EquipmentSlot.OFFHAND);
            if (predicate.test(offHandStack) && ((ItemStackAccess)(Object)offHandStack).getItemStackFoodComponent() == foodComponent) {
                ItemStackFoodComponentAPI.removeStackFood(offHandStack);
                this.sendFoodComponentSyncPacket(StackFoodComponentUtil.FoodComponentAction.REMOVE, EquipmentSlot.OFFHAND, null, 0);
            }
        }
    }

    public void sendFoodComponentSyncPacket(StackFoodComponentUtil.FoodComponentAction type, @Nullable EquipmentSlot equipmentSlot, @Nullable StackFoodComponentUtil.InventoryLocation inventoryLocation, int inventoryIndex) {
        if (entity.world.isClient()) return;
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeInt(entity.getId());
        buf.writeByte(type.ordinal());
        if (type == StackFoodComponentUtil.FoodComponentAction.ADD) {
            buf.writeBoolean(useAction != null);
            buf.writeBoolean(returnStack != null);
            buf.writeBoolean(sound != null);
            SerializableDataTypes.FOOD_COMPONENT.send(buf, foodComponent);
            if (useAction != null) {
                buf.writeByte(type.ordinal());
            }
            if (returnStack != null) {
                buf.writeItemStack(returnStack);
            }
            if (sound != null) {
                buf.writeIdentifier(sound.getId());
            }
        }
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
            ServerPlayNetworking.send(player, ApugliPackets.SYNC_STACK_FOOD_COMPONENT, buf);
        }
        if (!(entity instanceof ServerPlayerEntity)) return;
        ServerPlayNetworking.send((ServerPlayerEntity)entity, ApugliPackets.SYNC_STACK_FOOD_COMPONENT, buf);
    }
}
