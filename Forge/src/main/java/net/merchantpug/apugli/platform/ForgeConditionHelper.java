package net.merchantpug.apugli.platform;

import net.merchantpug.apugli.access.ItemStackLevelAccess;
import net.merchantpug.apugli.condition.*;
import net.merchantpug.apugli.data.ApoliForgeDataTypes;
import net.merchantpug.apugli.condition.factory.IConditionFactory;
import net.merchantpug.apugli.platform.services.IConditionHelper;
import net.merchantpug.apugli.registry.ApugliRegisters;
import com.google.auto.service.AutoService;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataType;
import io.github.edwinmindcraft.apoli.api.power.configuration.*;
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

@AutoService(IConditionHelper.class)
public class ForgeConditionHelper implements IConditionHelper {
    
    @Override
    public SerializableDataType<?> biEntityDataType() {
        return ApoliForgeDataTypes.BIENTITY_CONDITION.get();
    }
    
    @Override
    public void registerBiEntity(String name, IConditionFactory<Tuple<Entity, Entity>> condition) {
        ApugliRegisters.BIENTITY_CONDITIONS.register(name, () -> new FabricBiEntityCondition(condition.getSerializableData(), condition::check));
    }
    
    @Override
    public boolean checkBiEntity(SerializableData.Instance data, String fieldName, Entity actor, Entity target) {
        return !data.isPresent(fieldName) || ((ConfiguredBiEntityCondition<?, ?>)data.get(fieldName)).check(actor, target);
    }
    
    @Override
    @Nullable
    public Predicate<Tuple<Entity, Entity>> biEntityPredicate(SerializableData.Instance data, String fieldName) {
        if(!data.isPresent(fieldName)) {
            return null;
        }
        return pair -> ((ConfiguredBiEntityCondition<?, ?>)data.get(fieldName)).check(pair.getA(), pair.getB());
    }
    
    
    @Override
    public SerializableDataType<?> biomeDataType() {
        return ApoliForgeDataTypes.BIOME_CONDITION.get();
    }
    
    @Override
    public void registerBiome(String name, IConditionFactory<Holder<Biome>> condition) {
        ApugliRegisters.BIOME_CONDITIONS.register(name, () -> new FabricBiomeCondition(condition.getSerializableData(), condition::check));
    }
    
    @Override
    public boolean checkBiome(SerializableData.Instance data, String fieldName, Holder<Biome> biome) {
        return !data.isPresent(fieldName) || ((ConfiguredBiomeCondition<?, ?>)data.get(fieldName)).check(biome);
    }
    
    @Override
    @Nullable
    public Predicate<Holder<Biome>> biomePredicate(SerializableData.Instance data, String fieldName) {
        if(!data.isPresent(fieldName)) {
            return null;
        }
        return biome -> ((ConfiguredBiomeCondition<?, ?>)data.get(fieldName)).check(biome);
    }
    
    
    @Override
    public SerializableDataType<?> blockDataType() {
        return ApoliForgeDataTypes.BLOCK_CONDITION.get();
    }
    
    @Override
    public void registerBlock(String name, IConditionFactory<BlockInWorld> condition) {
        ApugliRegisters.BLOCK_CONDITIONS.register(name, () -> new FabricBlockCondition(condition.getSerializableData(), condition::check));
    }
    
    @Override
    public boolean checkBlock(SerializableData.Instance data, String fieldName, Level level, BlockPos pos) {
        return !data.isPresent(fieldName) || ((ConfiguredBlockCondition<?, ?>)data.get(fieldName)).check(level, pos, () -> level.getBlockState(pos));
    }
    
    @Override
    @Nullable
    public Predicate<BlockInWorld> blockPredicate(SerializableData.Instance data, String fieldName) {
        if(!data.isPresent(fieldName)) {
            return null;
        }
        return block -> ((ConfiguredBlockCondition<?, ?>)data.get(fieldName)).check(block.getLevel(), block.getPos(), block::getState);
    }
    
    
    @Override
    public SerializableDataType<?> damageDataType() {
        return ApoliForgeDataTypes.DAMAGE_CONDITION.get();
    }
    
    @Override
    public void registerDamage(String name, IConditionFactory<Tuple<DamageSource, Float>> condition) {
        ApugliRegisters.DAMAGE_CONDITIONS.register(name, () -> new FabricDamageCondition(condition.getSerializableData(), condition::check));
    }
    
    @Override
    public boolean checkDamage(SerializableData.Instance data, String fieldName, DamageSource source, float amount) {
        return !data.isPresent(fieldName) || ((ConfiguredDamageCondition<?, ?>)data.get(fieldName)).check(source, amount);
    }
    
    @Override
    @Nullable
    public Predicate<Tuple<DamageSource, Float>> damagePredicate(SerializableData.Instance data, String fieldName) {
        if(!data.isPresent(fieldName)) {
            return null;
        }
        return pair -> ((ConfiguredDamageCondition<?, ?>)data.get(fieldName)).check(pair.getA(), pair.getB());
    }
    
    
    @Override
    public SerializableDataType<?> entityDataType() {
        return ApoliForgeDataTypes.ENTITY_CONDITION.get();
    }
    
    @Override
    public void registerEntity(String name, IConditionFactory<Entity> condition) {
        ApugliRegisters.ENTITY_CONDITIONS.register(name, () -> new FabricEntityCondition(condition.getSerializableData(), condition::check));
    }
    
    @Override
    public boolean checkEntity(SerializableData.Instance data, String fieldName, Entity entity) {
        return !data.isPresent(fieldName) || ((ConfiguredEntityCondition<?, ?>)data.get(fieldName)).check(entity);
    }
    
    @Override
    @Nullable
    public Predicate<Entity> entityPredicate(SerializableData.Instance data, String fieldName) {
        if(!data.isPresent(fieldName)) {
            return null;
        }
        return entity -> ((ConfiguredEntityCondition<?, ?>)data.get(fieldName)).check(entity);
    }
    
    
    @Override
    public SerializableDataType<?> fluidDataType() {
        return ApoliForgeDataTypes.FLUID_CONDITION.get();
    }
    
    @Override
    public void registerFluid(String name, IConditionFactory<FluidState> condition) {
        ApugliRegisters.FLUID_CONDITIONS.register(name, () -> new FabricFluidCondition(condition.getSerializableData(), condition::check));
    }
    
    @Override
    public boolean checkFluid(SerializableData.Instance data, String fieldName, FluidState fluidState) {
        return !data.isPresent(fieldName) || ((ConfiguredFluidCondition<?, ?>)data.get(fieldName)).check(fluidState);
    }
    
    @Override
    @Nullable
    public Predicate<FluidState> fluidPredicate(SerializableData.Instance data, String fieldName) {
        if(!data.isPresent(fieldName)) {
            return null;
        }
        return fluid -> ((ConfiguredFluidCondition<?, ?>)data.get(fieldName)).check(fluid);
    }
    
    
    @Override
    public SerializableDataType<?> itemDataType() {
        return ApoliForgeDataTypes.ITEM_CONDITION.get();
    }
    
    @Override
    public void registerItem(String name, IConditionFactory<ItemStack> condition) {
        ApugliRegisters.ITEM_CONDITIONS.register(name, () -> new FabricItemCondition(condition.getSerializableData(), condition::check));
    }
    
    @Override
    public boolean checkItem(SerializableData.Instance data, String fieldName, ItemStack stack) {
        return !data.isPresent(fieldName) || ((ConfiguredItemCondition<?, ?>)data.get(fieldName)).check(((ItemStackLevelAccess)(Object)stack).getLevel(), stack);
    }
    
    @Override
    @Nullable
    public Predicate<ItemStack> itemPredicate(SerializableData.Instance data, String fieldName) {
        if(!data.isPresent(fieldName)) {
            return null;
        }
        return item -> ((ConfiguredItemCondition<?, ?>)data.get(fieldName)).check(((ItemStackLevelAccess)(Object)item).getLevel(), item);
    }
    
}
