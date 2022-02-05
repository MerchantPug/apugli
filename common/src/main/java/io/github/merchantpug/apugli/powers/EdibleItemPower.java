package io.github.merchantpug.apugli.powers;

import dev.architectury.injectables.annotations.ExpectPlatform;
import io.github.apace100.origins.power.Power;
import io.github.apace100.origins.power.PowerType;
import io.github.apace100.origins.power.factory.PowerFactory;
import io.github.apace100.origins.power.factory.action.ActionFactory;
import io.github.apace100.origins.power.factory.condition.ConditionFactory;
import io.github.apace100.origins.util.SerializableData;
import io.github.apace100.origins.util.SerializableDataType;
import io.github.merchantpug.apugli.Apugli;
import io.github.merchantpug.apugli.access.ItemStackAccess;
import io.github.merchantpug.apugli.networking.ApugliPackets;
import io.github.merchantpug.apugli.util.BackportedDataTypes;
import io.github.merchantpug.apugli.util.ItemStackFoodComponentAPI;
import io.github.merchantpug.apugli.util.StackFoodComponentUtil;
import io.netty.buffer.Unpooled;
import me.shedaniel.architectury.networking.NetworkManager;
import me.shedaniel.architectury.utils.GameInstance;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
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
                        .add("item_condition", SerializableDataType.ITEM_CONDITION)
                        .add("food_component", BackportedDataTypes.FOOD_COMPONENT)
                        .add("use_action", SerializableDataType.enumValue(UseAction.class), null)
                        .add("return_stack", SerializableDataType.ITEM_STACK, null)
                        .add("sound", SerializableDataType.SOUND_EVENT, null)
                        .add("entity_action", SerializableDataType.ENTITY_ACTION, null)
                        .add("tick_rate", SerializableDataType.INT, 10),
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

    public EdibleItemPower(PowerType<?> type, PlayerEntity player, Predicate<ItemStack> predicate, FoodComponent foodComponent, UseAction useAction, ItemStack returnStack, SoundEvent sound, Consumer<Entity> entityActionWhenEaten, int tickRate) {
        super(type, player);
        this.predicate = predicate;
        this.foodComponent = foodComponent;
        this.useAction = useAction;
        this.returnStack = returnStack;
        this.sound = sound;
        this.entityActionWhenEaten = entityActionWhenEaten;
        this.tickRate = tickRate;
        this.setTicking(true);
    }

    // This method will exist as long as the tick() method only functions serverside, I'd be sending a million packets which have desync otherwise
    public void tempTick() {
        ItemStack mainHandStack = player.getEquippedStack(EquipmentSlot.MAINHAND);
        ItemStack offHandStack = player.getEquippedStack(EquipmentSlot.OFFHAND);
        if (player.age % tickRate == 0) {
            if (mainHandStack != ItemStack.EMPTY) {
                if (this.predicate.test(mainHandStack) && this.isActive() && !((ItemStackAccess)(Object)mainHandStack).isItemStackFood()) {
                    ItemStackFoodComponentAPI.setStackFood(mainHandStack, foodComponent, useAction, returnStack, sound);
                }
                if (this.predicate.test(mainHandStack) && !this.isActive() && ((ItemStackAccess)(Object)mainHandStack).getItemStackFoodComponent() == foodComponent) {
                    ItemStackFoodComponentAPI.removeStackFood(mainHandStack);
                }
            }
            if (offHandStack != ItemStack.EMPTY) {
                if (this.predicate.test(offHandStack) && this.isActive() && !((ItemStackAccess)(Object)mainHandStack).isItemStackFood()) {
                    ItemStackFoodComponentAPI.setStackFood(offHandStack, foodComponent, useAction, returnStack, sound);
                }
                if (this.predicate.test(offHandStack) && !this.isActive() && ((ItemStackAccess)(Object)offHandStack).getItemStackFoodComponent() == foodComponent) {
                    ItemStackFoodComponentAPI.removeStackFood(offHandStack);
                }
            }
        }
    }

    public boolean doesApply(ItemStack stack) {
        return this.predicate.test(stack);
    }

    public void eat() {
        if(entityActionWhenEaten != null) {
            entityActionWhenEaten.accept(player);
        }
    }

    @Override
    public void onRemoved() {
        for (int i = 0; i < player.inventory.main.size(); i++) {
            ItemStack itemStack = player.inventory.main.get(i);
            if (predicate.test(itemStack) && ((ItemStackAccess) (Object) itemStack).getItemStackFoodComponent() == foodComponent) {
                ItemStackFoodComponentAPI.removeStackFood(itemStack);
                this.sendFoodComponentRemovePacket(null, StackFoodComponentUtil.InventoryLocation.MAIN, i);
            }
        }
        for (int i = 0; i < player.inventory.armor.size(); i++) {
            ItemStack armorStack = player.inventory.getArmorStack(i);
            if (predicate.test(armorStack) && ((ItemStackAccess) (Object) armorStack).getItemStackFoodComponent() == foodComponent) {
                ItemStackFoodComponentAPI.removeStackFood(armorStack);
                this.sendFoodComponentRemovePacket(null, StackFoodComponentUtil.InventoryLocation.ARMOR, i);
            }
        }
        ItemStack offHandStack = player.getEquippedStack(EquipmentSlot.OFFHAND);
        if (predicate.test(offHandStack) && ((ItemStackAccess) (Object) offHandStack).getItemStackFoodComponent() == foodComponent) {
            ItemStackFoodComponentAPI.removeStackFood(offHandStack);
            this.sendFoodComponentRemovePacket(EquipmentSlot.OFFHAND, null, 0);
        }
    }

    public void sendFoodComponentRemovePacket(@Nullable EquipmentSlot equipmentSlot, @Nullable StackFoodComponentUtil.InventoryLocation inventoryLocation, int inventoryIndex) {
        if (player.world.isClient()) return;
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeInt(player.getEntityId());
        buf.writeBoolean(equipmentSlot != null);
        if (equipmentSlot != null) {
            buf.writeString(equipmentSlot.getName(), Short.MAX_VALUE);
        }

        buf.writeBoolean(inventoryLocation != null);
        if (inventoryLocation != null) {
            buf.writeByte(inventoryLocation.ordinal());
            buf.writeInt(inventoryIndex);
        }

        if (!(player instanceof ServerPlayerEntity)) return;
        sendPackets((ServerPlayerEntity) player, buf);
    }

    @ExpectPlatform
    public static void sendPackets(ServerPlayerEntity player, PacketByteBuf buf) {
        throw new AssertionError();
    }
}