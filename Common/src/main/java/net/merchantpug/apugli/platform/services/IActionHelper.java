package net.merchantpug.apugli.platform.services;

import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataType;
import net.merchantpug.apugli.action.factory.IActionFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.mutable.Mutable;
import org.apache.commons.lang3.tuple.Triple;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public interface IActionHelper {
    
    SerializableDataType<?> biEntityDataType();
    
    void registerBiEntity(String name, IActionFactory<Tuple<Entity, Entity>> action);
    
    void executeBiEntity(SerializableData.Instance data, String fieldName, Entity actor, Entity target);

    <A> void executeBiEntity(A action, Entity actor, Entity target);
    
    @Nullable
    Consumer<Tuple<Entity, Entity>> biEntityConsumer(SerializableData.Instance data, String fieldName);

    <T> void writeBiEntityActionToNbt(CompoundTag tag, String path, T object);

    <T> T readBiEntityActionFromNbt(CompoundTag tag, String path);

    <T> T getBiEntityDefault();


    SerializableDataType<?> blockDataType();
    
    void registerBlock(String name, IActionFactory<Triple<Level, BlockPos, Direction>> action);
    
    void executeBlock(SerializableData.Instance data, String fieldName, Level level, BlockPos pos, Direction direction);

    <A> void executeBlock(A action, Level level, BlockPos pos, Direction direction);
    
    Consumer<Triple<Level, BlockPos, Direction>> blockConsumer(SerializableData.Instance data, String fieldName);

    <T> void writeBlockActionToNbt(CompoundTag tag, String path, T object);

    <T> T readBlockActionFromNbt(CompoundTag tag, String path);

    <T> T getBlockDefault();


    SerializableDataType<?> entityDataType();
    
    void registerEntity(String name, IActionFactory<Entity> action);
    
    void executeEntity(SerializableData.Instance data, String fieldName, Entity entity);

    <A> void executeEntity(A action, Entity entity);

    Consumer<Entity> entityConsumer(SerializableData.Instance data, String fieldName);
    
    
    SerializableDataType<?> itemDataType();
    
    void registerItem(String name, IActionFactory<Tuple<Level, Mutable<ItemStack>>> action);
    
    void executeItem(SerializableData.Instance data, String fieldName, Level level, Mutable<ItemStack> mutable);

    <A> void executeEntity(A action, Level level, Mutable<ItemStack> mutable);
    
    Consumer<Tuple<Level, Mutable<ItemStack>>> itemConsumer(SerializableData.Instance data, String fieldName);

}
