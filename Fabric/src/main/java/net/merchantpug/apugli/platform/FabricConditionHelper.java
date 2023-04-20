package net.merchantpug.apugli.platform;

import net.merchantpug.apugli.Apugli;
import net.merchantpug.apugli.condition.factory.IConditionFactory;
import net.merchantpug.apugli.platform.services.IConditionHelper;
import com.google.auto.service.AutoService;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.apoli.registry.ApoliRegistries;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
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

@SuppressWarnings("unchecked")
@AutoService(IConditionHelper.class)
public class FabricConditionHelper implements IConditionHelper {
    
    @Override
    public SerializableDataType<?> biEntityDataType() {
        return ApoliDataTypes.BIENTITY_CONDITION;
    }
    
    @Override
    public void registerBiEntity(String name, IConditionFactory<Tuple<Entity, Entity>> condition) {
        ResourceLocation id = Apugli.asResource(name);
        Registry.register(ApoliRegistries.BIENTITY_CONDITION, id, new ConditionFactory<>(id, condition.getSerializableData(), condition::check));
    }
    
    @Override
    public boolean checkBiEntity(SerializableData.Instance data, String fieldName, Entity actor, Entity target) {
        return !data.isPresent(fieldName) || ((Predicate<Tuple<Entity, Entity>>)data.get(fieldName)).test(new Tuple<>(actor, target));
    }
    
    @Override
    @Nullable
    public Predicate<Tuple<Entity, Entity>> biEntityPredicate(SerializableData.Instance data, String fieldName) {
        return data.get(fieldName);
    }
    
    
    @Override
    public SerializableDataType<?> biomeDataType() {
        return ApoliDataTypes.BIOME_CONDITION;
    }
    
    @Override
    public void registerBiome(String name, IConditionFactory<Holder<Biome>> condition) {
        ResourceLocation id = Apugli.asResource(name);
        Registry.register(ApoliRegistries.BIOME_CONDITION, id, new ConditionFactory<>(id, condition.getSerializableData(), condition::check));
    }
    
    @Override
    public boolean checkBiome(SerializableData.Instance data, String fieldName, Holder<Biome> biome) {
        return !data.isPresent(fieldName) || ((Predicate<Holder<Biome>>)data.get(fieldName)).test(biome);
    }
    
    @Override
    @Nullable
    public Predicate<Holder<Biome>> biomePredicate(SerializableData.Instance data, String fieldName) {
        return data.get(fieldName);
    }
    
    
    @Override
    public SerializableDataType<?> blockDataType() {
        return ApoliDataTypes.BLOCK_CONDITION;
    }
    
    @Override
    public void registerBlock(String name, IConditionFactory<BlockInWorld> condition) {
        ResourceLocation id = Apugli.asResource(name);
        Registry.register(ApoliRegistries.BLOCK_CONDITION, id, new ConditionFactory<>(id, condition.getSerializableData(), condition::check));
    }
    
    @Override
    public boolean checkBlock(SerializableData.Instance data, String fieldName, Level level, BlockPos pos) {
        return !data.isPresent(fieldName) || ((Predicate<BlockInWorld>)data.get(fieldName)).test(new BlockInWorld(level, pos, true));
    }
    
    @Override
    @Nullable
    public Predicate<BlockInWorld> blockPredicate(SerializableData.Instance data, String fieldName) {
        return data.get(fieldName);
    }
    
    
    @Override
    public SerializableDataType<?> damageDataType() {
        return ApoliDataTypes.DAMAGE_CONDITION;
    }
    
    @Override
    public void registerDamage(String name, IConditionFactory<Tuple<DamageSource, Float>> condition) {
        ResourceLocation id = Apugli.asResource(name);
        Registry.register(ApoliRegistries.DAMAGE_CONDITION, id, new ConditionFactory<>(id, condition.getSerializableData(), condition::check));
    }
    
    @Override
    public boolean checkDamage(SerializableData.Instance data, String fieldName, DamageSource source, float amount) {
        return !data.isPresent(fieldName) || ((Predicate<Tuple<DamageSource, Float>>)data.get(fieldName)).test(new Tuple<>(source, amount));
    }
    
    @Override
    @Nullable
    public Predicate<Tuple<DamageSource, Float>> damagePredicate(SerializableData.Instance data, String fieldName) {
        return data.get(fieldName);
    }
    
    @Override
    public SerializableDataType<?> entityDataType() {
        return ApoliDataTypes.ENTITY_CONDITION;
    }
    
    @Override
    public void registerEntity(String name, IConditionFactory<Entity> condition) {
        ResourceLocation id = Apugli.asResource(name);
        Registry.register(ApoliRegistries.ENTITY_CONDITION, id, new ConditionFactory<>(id, condition.getSerializableData(), condition::check));
    }
    
    @Override
    public boolean checkEntity(SerializableData.Instance data, String fieldName, Entity entity) {
        return !data.isPresent(fieldName) || ((Predicate<Entity>)data.get(fieldName)).test(entity);
    }
    
    
    @Override
    @Nullable
    public Predicate<Entity> entityPredicate(SerializableData.Instance data, String fieldName) {
        return data.get(fieldName);
    }
    
    
    @Override
    public SerializableDataType<?> fluidDataType() {
        return ApoliDataTypes.FLUID_CONDITION;
    }
    
    @Override
    public void registerFluid(String name, IConditionFactory<FluidState> condition) {
        ResourceLocation id = Apugli.asResource(name);
        Registry.register(ApoliRegistries.FLUID_CONDITION, id, new ConditionFactory<>(id, condition.getSerializableData(), condition::check));
    }
    
    @Override
    public boolean checkFluid(SerializableData.Instance data, String fieldName, FluidState fluidState) {
        return !data.isPresent(fieldName) || ((Predicate<FluidState>)data.get(fieldName)).test(fluidState);
    }
    
    @Override
    @Nullable
    public Predicate<FluidState> fluidPredicate(SerializableData.Instance data, String fieldName) {
        return data.get(fieldName);
    }
    
    
    @Override
    public SerializableDataType<?> itemDataType() {
        return ApoliDataTypes.ITEM_CONDITION;
    }
    
    @Override
    public void registerItem(String name, IConditionFactory<ItemStack> condition) {
        ResourceLocation id = Apugli.asResource(name);
        Registry.register(ApoliRegistries.ITEM_CONDITION, id, new ConditionFactory<>(id, condition.getSerializableData(), condition::check));
    }
    
    @Override
    public boolean checkItem(SerializableData.Instance data, String fieldName, ItemStack stack) {
        return !data.isPresent(fieldName) || ((Predicate<ItemStack>)data.get(fieldName)).test(stack);
    }
    
    @Override
    @Nullable
    public Predicate<ItemStack> itemPredicate(SerializableData.Instance data, String fieldName) {
        return data.get(fieldName);
    }
    
}
