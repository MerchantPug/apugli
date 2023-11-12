package net.merchantpug.apugli.platform.services;

import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataType;
import net.merchantpug.apugli.condition.factory.IConditionFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Tuple;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public interface IConditionHelper {
    
    SerializableDataType<?> biEntityDataType();
    
    void registerBiEntity(String name, IConditionFactory<Tuple<Entity, Entity>> condition);
    
    boolean checkBiEntity(SerializableData.Instance data, String fieldName, Entity actor, Entity target);

    <C> boolean checkBiEntity(C condition, Entity actor, Entity target);
    
    @Nullable
    Predicate<Tuple<Entity, Entity>> biEntityPredicate(SerializableData.Instance data, String fieldName);

    <T> void writeBiEntityConditionToNbt(CompoundTag tag, String path, T object);

    <T> T readBiEntityConditionFromNbt(CompoundTag tag, String path);


    SerializableDataType<?> biomeDataType();
    
    void registerBiome(String name, IConditionFactory<Holder<Biome>> condition);
    
    boolean checkBiome(SerializableData.Instance data, String fieldName, Holder<Biome> biome);
    
    @Nullable
    Predicate<Holder<Biome>> biomePredicate(SerializableData.Instance data, String fieldName);

    <C> boolean checkBiome(C condition, Holder<Biome> biome);


    SerializableDataType<?> blockDataType();
    
    void registerBlock(String name, IConditionFactory<BlockInWorld> condition);
    
    boolean checkBlock(SerializableData.Instance data, String fieldName, Level level, BlockPos pos);

    <C> boolean checkBlock(C condition, Level level, BlockPos pos);

    <T> void writeBlockConditionToNbt(CompoundTag tag, String path, T object);

    <T> T readBlockConditionFromNbt(CompoundTag tag, String path);


    @Nullable
    Predicate<BlockInWorld> blockPredicate(SerializableData.Instance data, String fieldName);

    
    SerializableDataType<?> damageDataType();
    
    void registerDamage(String name, IConditionFactory<Tuple<DamageSource, Float>> condition);
    
    boolean checkDamage(SerializableData.Instance data, String fieldName, DamageSource source, float amount);

    <C> boolean checkDamage(C condition, DamageSource source, float amount);
    
    @Nullable
    Predicate<Tuple<DamageSource, Float>> damagePredicate(SerializableData.Instance data, String fieldName);


    SerializableDataType<?> entityDataType();
    
    void registerEntity(String name, IConditionFactory<Entity> condition);
    
    boolean checkEntity(SerializableData.Instance data, String fieldName, Entity entity);

    <C> boolean checkEntity(C condition, Entity entity);
    
    @Nullable
    Predicate<Entity> entityPredicate(SerializableData.Instance data, String fieldName);

    
    SerializableDataType<?> fluidDataType();
    
    void registerFluid(String name, IConditionFactory<FluidState> condition);
    
    boolean checkFluid(SerializableData.Instance data, String fieldName, FluidState fluidState);

    <C> boolean checkFluid(C condition, FluidState fluidState);
    
    @Nullable
    Predicate<FluidState> fluidPredicate(SerializableData.Instance data, String fieldName);


    SerializableDataType<?> itemDataType();
    
    void registerItem(String name, IConditionFactory<Tuple<Level, ItemStack>> condition);

    boolean checkItem(SerializableData.Instance data, String fieldName, Level level, ItemStack stack);

    <C> boolean checkItem(C condition, Level level, ItemStack stack);

    @Nullable
    Predicate<Tuple<Level, ItemStack>> itemPredicate(SerializableData.Instance data, String fieldName);

}
