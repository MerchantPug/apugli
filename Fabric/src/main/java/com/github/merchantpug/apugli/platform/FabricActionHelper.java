package com.github.merchantpug.apugli.platform;

import com.github.merchantpug.apugli.Apugli;
import com.github.merchantpug.apugli.action.factory.IActionFactory;
import com.github.merchantpug.apugli.platform.services.IActionHelper;
import com.google.auto.service.AutoService;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.apoli.registry.ApoliRegistries;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.mutable.Mutable;
import org.apache.commons.lang3.mutable.MutableObject;
import org.apache.commons.lang3.tuple.Triple;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

@SuppressWarnings("unchecked")
@AutoService(IActionHelper.class)
public class FabricActionHelper implements IActionHelper {
    
    @Override
    public SerializableDataType<?> biEntityDataType() {
        return ApoliDataTypes.BIENTITY_ACTION;
    }
    
    @Override
    public void registerBiEntity(String name, IActionFactory<Tuple<Entity, Entity>> action) {
        ResourceLocation id = Apugli.asResource(name);
        Registry.register(ApoliRegistries.BIENTITY_ACTION, id, new ActionFactory<>(id, action.getSerializableData(), action::execute));
    }
    
    @Override
    public void executeBiEntity(SerializableData.Instance data, String fieldName, Entity actor, Entity target) {
        if(data.isPresent(fieldName)) ((Consumer<Tuple<Entity, Entity>>)data.get(fieldName)).accept(new Tuple<>(actor, target));
    }
    
    @Override
    @Nullable
    public Consumer<Tuple<Entity, Entity>> biEntityConsumer(SerializableData.Instance data, String fieldName) {
        return data.get(fieldName);
    }
    
    @Override
    public SerializableDataType<?> blockDataType() {
        return ApoliDataTypes.BLOCK_ACTION;
    }
    
    @Override
    public void registerBlock(String name, IActionFactory<Triple<Level, BlockPos, Direction>> action) {
        ResourceLocation id = Apugli.asResource(name);
        Registry.register(ApoliRegistries.BLOCK_ACTION, id, new ActionFactory<>(id, action.getSerializableData(), action::execute));
    }
    
    @Override
    public void executeBlock(SerializableData.Instance data, String fieldName, Level level, BlockPos pos, Direction direction) {
        if(data.isPresent(fieldName)) ((Consumer<Triple<Level, BlockPos, Direction>>)data.get(fieldName)).accept(Triple.of(level, pos, direction));
    }
    
    
    @Override
    public Consumer<Triple<Level, BlockPos, Direction>> blockConsumer(SerializableData.Instance data, String fieldName) {
        return data.get(fieldName);
    }
    
    @Override
    public SerializableDataType<?> entityDataType() {
        return ApoliDataTypes.ENTITY_ACTION;
    }
    
    @Override
    public void registerEntity(String name, IActionFactory<Entity> action) {
        ResourceLocation id = Apugli.asResource(name);
        Registry.register(ApoliRegistries.ENTITY_ACTION, id, new ActionFactory<>(id, action.getSerializableData(), action::execute));
    }
    
    @Override
    public void executeEntity(SerializableData.Instance data, String fieldName, Entity entity) {
        if(data.isPresent(fieldName)) ((Consumer<Entity>)data.get(fieldName)).accept(entity);
    }
    
    
    @Override
    public Consumer<Entity> entityConsumer(SerializableData.Instance data, String fieldName) {
        return data.get(fieldName);
    }
    
    @Override
    public SerializableDataType<?> itemDataType() {
        return ApoliDataTypes.ITEM_ACTION;
    }
    
    @Override
    public void registerItem(String name, IActionFactory<Tuple<Level, Mutable<ItemStack>>> action) {
        ResourceLocation id = Apugli.asResource(name);
        Registry.register(ApoliRegistries.ITEM_ACTION, id, new ActionFactory<>(
            id, action.getSerializableData(),
            (data, pair) -> action.execute(data, new Tuple<>(pair.getA(), new MutableObject<>(pair.getB())))
        ));
    }
    
    @Override
    public void executeItem(SerializableData.Instance data, String fieldName, Level level, Mutable<ItemStack> mutable) {
        if(data.isPresent(fieldName)) ((Consumer<Tuple<Level, ItemStack>>)data.get(fieldName)).accept(new Tuple<>(level, mutable.getValue()));
    }
    
    @Override
    public Consumer<Tuple<Level, Mutable<ItemStack>>> itemConsumer(SerializableData.Instance data, String fieldName) {
        return (levelAndStack) -> ((Consumer<ItemStack>)data.get(fieldName)).accept(levelAndStack.getB().getValue());
    }
    
}
