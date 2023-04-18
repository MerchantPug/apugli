package com.github.merchantpug.apugli.power;

import com.github.merchantpug.apugli.access.ItemStackAccess;
import com.github.merchantpug.apugli.util.ItemStackFoodComponentUtil;
import com.github.merchantpug.apugli.util.StackFoodComponentUtil;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import org.jetbrains.annotations.Nullable;
import the.great.migration.merchantpug.apugli.Apugli;
import the.great.migration.merchantpug.apugli.networking.ApugliPackets;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class EdibleItemPower extends Power {
    public final Predicate<ItemStack> predicate;
    public final FoodProperties foodComponent;
    public final UseAnim useAction;
    public final ItemStack returnStack;
    public final SoundEvent sound;
    private final Consumer<Entity> entityActionWhenEaten;
    private final int tickRate;

    public static PowerFactory<?> getFactory() {
        return new PowerFactory<EdibleItemPower>(Apugli.identifier("edible_item"),
                new SerializableData()
                        .add("item_condition", Services.CONDITION.itemDataType())
                        .add("food_component", SerializableDataTypes.FOOD_COMPONENT)
                        .add("use_action", SerializableDataTypes.USE_ACTION, null)
                        .add("return_stack", SerializableDataTypes.ITEM_STACK, null)
                        .add("sound", SerializableDataTypes.SOUND_EVENT, null)
                        .add("entity_action", Services.ACTION.entityDataType(), null)
                        .add("tick_rate", SerializableDataTypes.INT, 10),
                data ->
                        (type, player) -> new EdibleItemPower(type, player,
                                    (ConditionFactory<ItemStack>.Instance)data.get("item_condition"),
                                    (FoodProperties)data.get("food_component"),
                                    (UseAnim)data.get("use_action"),
                                    (ItemStack)data.get("return_stack"),
                                    (SoundEvent)data.get("sound"),
                                    (ActionFactory<Entity>.Instance)data.get("entity_action"),
                                    data.getInt("tick_rate")))
                .allowCondition();
    }

    public EdibleItemPower(PowerType<?> type, LivingEntity entity, Predicate<ItemStack> predicate, FoodProperties foodComponent, UseAnim useAction, ItemStack returnStack, SoundEvent sound, Consumer<Entity> entityActionWhenEaten, int tickRate) {
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
        ItemStack mainHandStack = entity.getItemBySlot(EquipmentSlot.MAINHAND);
        ItemStack offHandStack = entity.getItemBySlot(EquipmentSlot.OFFHAND);
        if(entity.tickCount % tickRate == 0) {
            if(mainHandStack != ItemStack.EMPTY) {
                if(this.predicate.test(mainHandStack) && this.isActive() && !((ItemStackAccess)(Object)mainHandStack).isItemStackFood() && ((ItemStackAccess)(Object)mainHandStack).getItemStackFoodComponent() == null) {
                    ItemStackFoodComponentUtil.setStackFood(mainHandStack, foodComponent, useAction, returnStack, sound);
                }
                if(this.predicate.test(mainHandStack) && !this.isActive() && ((ItemStackAccess)(Object)mainHandStack).getItemStackFoodComponent() == foodComponent) {
                    ItemStackFoodComponentUtil.removeStackFood(mainHandStack);
                }
            }
            if(offHandStack != ItemStack.EMPTY) {
                if(this.predicate.test(offHandStack) && this.isActive() && !((ItemStackAccess)(Object)mainHandStack).isItemStackFood() && ((ItemStackAccess)(Object)mainHandStack).getItemStackFoodComponent() == null) {
                    ItemStackFoodComponentUtil.setStackFood(offHandStack, foodComponent, useAction, returnStack, sound);
                }
                if(this.predicate.test(offHandStack) && !this.isActive() && ((ItemStackAccess)(Object)offHandStack).getItemStackFoodComponent() == foodComponent) {
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
        if(entity instanceof Player) {
            for(int i = 0; i < ((Player) entity).getInventory().items.size(); i++) {
                ItemStack itemStack = ((Player)entity).getInventory().items.get(i);
                if(predicate.test(itemStack) && ((ItemStackAccess)(Object)itemStack).getItemStackFoodComponent() == foodComponent) {
                    ItemStackFoodComponentUtil.removeStackFood(itemStack);
                    this.sendFoodComponentRemovePacket(null, StackFoodComponentUtil.InventoryLocation.MAIN, i);
                }
            }
            for(int i = 0; i < ((Player) entity).getInventory().armor.size(); i++) {
                ItemStack armorStack = ((Player) entity).getInventory().getArmor(i);
                if(predicate.test(armorStack) && ((ItemStackAccess)(Object)armorStack).getItemStackFoodComponent() == foodComponent) {
                    ItemStackFoodComponentUtil.removeStackFood(armorStack);
                    this.sendFoodComponentRemovePacket(null, StackFoodComponentUtil.InventoryLocation.ARMOR, i);
                }
            }
            ItemStack offHandStack = entity.getItemBySlot(EquipmentSlot.OFFHAND);
            if(predicate.test(offHandStack) && ((ItemStackAccess)(Object)offHandStack).getItemStackFoodComponent() == foodComponent) {
                ItemStackFoodComponentUtil.removeStackFood(offHandStack);
                this.sendFoodComponentRemovePacket(EquipmentSlot.OFFHAND, null, 0);
            }
        }
    }

    public void sendFoodComponentRemovePacket(@Nullable EquipmentSlot equipmentSlot, @Nullable StackFoodComponentUtil.InventoryLocation inventoryLocation, int inventoryIndex) {
        if(entity.level.isClientSide()) return;
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeInt(entity.getId());
        buf.writeBoolean(equipmentSlot != null);
        if(equipmentSlot != null) {
            buf.writeUtf(equipmentSlot.getName(), FriendlyByteBuf.MAX_STRING_LENGTH);
        }

        buf.writeBoolean(inventoryLocation != null);
        if(inventoryLocation != null) {
            buf.writeByte(inventoryLocation.ordinal());
            buf.writeInt(inventoryIndex);
        }

        for(ServerPlayer player : PlayerLookup.tracking(entity)) {
            ServerPlayNetworking.send(player, ApugliPackets.REMOVE_STACK_FOOD_COMPONENT, buf);
        }
        if(!(entity instanceof ServerPlayer)) return;
        ServerPlayNetworking.send((ServerPlayer)entity, ApugliPackets.REMOVE_STACK_FOOD_COMPONENT, buf);
    }
}
