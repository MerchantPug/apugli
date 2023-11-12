package net.merchantpug.apugli.platform;

import com.google.auto.service.AutoService;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.apoli.registry.ApoliRegistries;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataType;
import net.merchantpug.apugli.Apugli;
import net.merchantpug.apugli.condition.factory.IConditionFactory;
import net.merchantpug.apugli.platform.services.IConditionHelper;
import net.merchantpug.apugli.util.ConditionFactoryWrapperCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
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

import java.util.Optional;
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
    public <C> boolean checkBiEntity(C condition, Entity actor, Entity target) {
        return condition == null || ((Predicate<Tuple<Entity, Entity>>)condition).test(new Tuple<>(actor, target));
    }

    @Override
    @Nullable
    public Predicate<Tuple<Entity, Entity>> biEntityPredicate(SerializableData.Instance data, String fieldName) {
        return data.get(fieldName);
    }

    @Override
    public <T> void writeBiEntityConditionToNbt(CompoundTag tag, String path, T object) {
        if (object ==  null) return;

        ConditionFactory<Tuple<Entity, Entity>>.Instance instance = (ConditionFactory<Tuple<Entity, Entity>>.Instance) object;
        Codec<ConditionFactory<Tuple<Entity, Entity>>.Instance> codec = new ConditionFactoryWrapperCodec<>(ApoliRegistries.BIENTITY_CONDITION);

        Optional<Tag> tagOptional = codec.encodeStart(NbtOps.INSTANCE, instance)
                .resultOrPartial(s -> Apugli.LOG.warn("Could only partially encode bi-entity condition to tag: {}", s));
        if (tagOptional.isEmpty()) {
            Apugli.LOG.error("Failed to serialize bi-entity condition to tag.");
            return;
        }
        tag.put(path, tagOptional.get());
    }

    @Override
    public <T> T readBiEntityConditionFromNbt(CompoundTag tag, String path) {
        if (!tag.contains(path, Tag.TAG_COMPOUND)) {
            return null;
        }

        Codec<ConditionFactory<Tuple<Entity, Entity>>.Instance> codec = new ConditionFactoryWrapperCodec<>(ApoliRegistries.BIENTITY_CONDITION);
        Optional<ConditionFactory<Tuple<Entity, Entity>>.Instance> instanceOptional = codec.decode(NbtOps.INSTANCE, tag.getCompound(path))
                .resultOrPartial(s -> Apugli.LOG.warn("Could only partially decode bi-entity condition from tag: {}", s)).map(Pair::getFirst);

        if (instanceOptional.isEmpty()) {
            Apugli.LOG.error("Failed to deserialize bi-entity condition from tag.");
            return null;
        }

        return (T) instanceOptional.get();
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
    public <C> boolean checkBiome(C condition, Holder<Biome> biome) {
        return condition == null || ((Predicate<Holder<Biome>>)condition).test(biome);
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
    public <C> boolean checkBlock(C condition, Level level, BlockPos pos) {
        return condition == null || ((Predicate<BlockInWorld>)condition).test(new BlockInWorld(level, pos, true));
    }

    @Override
    public <T> void writeBlockConditionToNbt(CompoundTag tag, String path, T object) {
        if (object == null) return;

        ConditionFactory<BlockInWorld>.Instance instance = (ConditionFactory<BlockInWorld>.Instance) object;
        Codec<ConditionFactory<BlockInWorld>.Instance> codec = new ConditionFactoryWrapperCodec<>(ApoliRegistries.BLOCK_CONDITION);

        Optional<Tag> tagOptional = codec.encodeStart(NbtOps.INSTANCE, instance)
                .resultOrPartial(s -> Apugli.LOG.warn("Could only partially encode block condition to tag: {}", s));
        if (tagOptional.isEmpty()) {
            Apugli.LOG.error("Failed to serialize block condition to tag.");
            return;
        }
        tag.put(path, tagOptional.get());
    }

    @Override
    public <T> T readBlockConditionFromNbt(CompoundTag tag, String path) {
        if (!tag.contains(path, Tag.TAG_COMPOUND)) {
            return null;
        }

        Codec<ConditionFactory<BlockInWorld>.Instance> codec = new ConditionFactoryWrapperCodec<>(ApoliRegistries.BLOCK_CONDITION);
        Optional<ConditionFactory<BlockInWorld>.Instance> instanceOptional = codec.decode(NbtOps.INSTANCE, tag.getCompound(path))
                .resultOrPartial(s -> Apugli.LOG.warn("Could only partially decode block condition from tag: {}", s)).map(Pair::getFirst);

        if (instanceOptional.isEmpty()) {
            Apugli.LOG.error("Failed to deserialize block condition from tag.");
            return null;
        }

        return (T) instanceOptional.get();
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
    public <C> boolean checkDamage(C condition, DamageSource source, float amount) {
        return condition == null || ((Predicate<Tuple<DamageSource, Float>>)condition).test(new Tuple<>(source, amount));
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
    public <C> boolean checkEntity(C condition, Entity entity) {
        return condition == null || ((Predicate<Entity>)condition).test(entity);
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
    public <C> boolean checkFluid(C condition, FluidState fluidState) {
        return condition == null || ((Predicate<FluidState>)condition).test(fluidState);
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
    public boolean checkItem(SerializableData.Instance data, String fieldName, Level level, ItemStack stack) {
        return !data.isPresent(fieldName) || ((Predicate<ItemStack>)data.get(fieldName)).test(stack);
    }

    @Override
    public <C> boolean checkItem(C condition, Level level, ItemStack stack) {
        return condition == null || ((Predicate<ItemStack>)condition).test(stack);
    }

    @Override
    @javax.annotation.Nullable
    public Predicate<Tuple<Level, ItemStack>> itemPredicate(SerializableData.Instance data, String fieldName) {
        if (!data.isPresent(fieldName)) {
            return null;
        }
        return levelAndStack -> ((Predicate<ItemStack>)data.get(fieldName)).test(levelAndStack.getB());
    }

}
