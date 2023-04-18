package com.github.merchantpug.apugli.platform.services;

import com.github.merchantpug.apugli.condition.factory.IConditionFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
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
    
    @Nullable
    Predicate<Tuple<Entity, Entity>> biEntityPredicate(SerializableData.Instance data, String fieldName);
    
    
    SerializableDataType<?> biomeDataType();
    
    void registerBiome(String name, IConditionFactory<Holder<Biome>> condition);
    
    boolean checkBiome(SerializableData.Instance data, String fieldName, Holder<Biome> biome);
    
    @Nullable
    Predicate<Holder<Biome>> biomePredicate(SerializableData.Instance data, String fieldName);
    
    
    SerializableDataType<?> blockDataType();
    
    void registerBlock(String name, IConditionFactory<BlockInWorld> condition);
    
    boolean checkBlock(SerializableData.Instance data, String fieldName, Level level, BlockPos pos);
    
    @Nullable
    Predicate<BlockInWorld> blockPredicate(SerializableData.Instance data, String fieldName);
    
    
    SerializableDataType<?> damageDataType();
    
    void registerDamage(String name, IConditionFactory<Tuple<DamageSource, Float>> condition);
    
    boolean checkDamage(SerializableData.Instance data, String fieldName, DamageSource source, float amount);
    
    @Nullable
    Predicate<Tuple<DamageSource, Float>> damagePredicate(SerializableData.Instance data, String fieldName);
    
    
    SerializableDataType<?> entityDataType();
    
    void registerEntity(String name, IConditionFactory<Entity> condition);
    
    boolean checkEntity(SerializableData.Instance data, String fieldName, Entity entity);
    
    @Nullable
    Predicate<Entity> entityPredicate(SerializableData.Instance data, String fieldName);
    
    
    SerializableDataType<?> fluidDataType();
    
    void registerFluid(String name, IConditionFactory<FluidState> condition);
    
    boolean checkFluid(SerializableData.Instance data, String fieldName, FluidState fluidState);
    
    @Nullable
    Predicate<FluidState> fluidPredicate(SerializableData.Instance data, String fieldName);
    
    
    SerializableDataType<?> itemDataType();
    
    void registerItem(String name, IConditionFactory<ItemStack> condition);
    
    boolean checkItem(SerializableData.Instance data, String fieldName, ItemStack stack);
    
    @Nullable
    Predicate<ItemStack> itemPredicate(SerializableData.Instance data, String fieldName);
    
}
