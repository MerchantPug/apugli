package net.merchantpug.apugli.platform;

import net.merchantpug.apugli.action.FabricEntityAction;
import net.merchantpug.apugli.action.FabricItemAction;
import net.merchantpug.apugli.action.factory.IActionFactory;
import net.merchantpug.apugli.data.ApoliForgeDataTypes;
import net.merchantpug.apugli.platform.services.IActionHelper;
import net.merchantpug.apugli.registry.ApugliRegisters;
import com.google.auto.service.AutoService;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataType;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredBiEntityAction;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredBlockAction;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredEntityAction;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredItemAction;
import net.merchantpug.apugli.action.FabricBiEntityAction;
import net.merchantpug.apugli.action.FabricBlockAction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.mutable.Mutable;
import org.apache.commons.lang3.tuple.Triple;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

@AutoService(IActionHelper.class)
public class ForgeActionHelper implements IActionHelper {
    
    @Override
    public SerializableDataType<?> biEntityDataType() {
        return ApoliForgeDataTypes.BIENTITY_ACTION;
    }
    
    @Override
    public void registerBiEntity(String name, IActionFactory<Tuple<Entity, Entity>> action) {
        ApugliRegisters.BIENTITY_ACTIONS.register(name, () -> new FabricBiEntityAction(action.getSerializableData(), action::execute));
    }
    
    @Override
    public void executeBiEntity(SerializableData.Instance data, String fieldName, Entity actor, Entity target) {
        if(data.isPresent(fieldName)) ((ConfiguredBiEntityAction<?, ?>)data.get(fieldName)).execute(actor, target);
    }
    
    @Override
    @Nullable
    public Consumer<Tuple<Entity, Entity>> biEntityConsumer(SerializableData.Instance data, String fieldName) {
        if(!data.isPresent(fieldName)) {
            return null;
        }
        ConfiguredBiEntityAction<?, ?> action = data.get(fieldName);
        return pair -> action.execute(pair.getA(), pair.getB());
    }
    
    
    @Override
    public SerializableDataType<?> blockDataType() {
        return ApoliForgeDataTypes.BLOCK_ACTION;
    }
    
    @Override
    public void registerBlock(String name, IActionFactory<Triple<Level, BlockPos, Direction>> action) {
        ApugliRegisters.BLOCK_ACTIONS.register(name, () -> new FabricBlockAction(action.getSerializableData(), action::execute));
    }
    
    @Override
    public void executeBlock(SerializableData.Instance data, String fieldName, Level level, BlockPos pos, Direction direction) {
        if(data.isPresent(fieldName)) ((ConfiguredBlockAction<?, ?>)data.get(fieldName)).execute(level, pos, direction);
    }
    
    @Override
    public Consumer<Triple<Level, BlockPos, Direction>> blockConsumer(SerializableData.Instance data, String fieldName) {
        if(!data.isPresent(fieldName)) {
            return null;
        }
        ConfiguredBlockAction<?, ?> action = data.get(fieldName);
        return triple -> action.execute(triple.getLeft(), triple.getMiddle(), triple.getRight());
    }
    
    
    @Override
    public SerializableDataType<?> entityDataType() {
        return ApoliForgeDataTypes.ENTITY_ACTION;
    }
    
    @Override
    public void registerEntity(String name, IActionFactory<Entity> action) {
        ApugliRegisters.ENTITY_ACTIONS.register(name, () -> new FabricEntityAction(action.getSerializableData(), action::execute));
    }
    
    @Override
    public void executeEntity(SerializableData.Instance data, String fieldName, Entity entity) {
        if(data.isPresent(fieldName)) ((ConfiguredEntityAction<?, ?>)data.get(fieldName)).execute(entity);
    }
    
    @Override
    public Consumer<Entity> entityConsumer(SerializableData.Instance data, String fieldName) {
        if(!data.isPresent(fieldName)) {
            return null;
        }
        ConfiguredEntityAction<?, ?> action = data.get(fieldName);
        return action::execute;
    }
    
    
    @Override
    public SerializableDataType<?> itemDataType() {
        return ApoliForgeDataTypes.ITEM_ACTION;
    }
    
    @Override
    public void registerItem(String name, IActionFactory<Tuple<Level, Mutable<ItemStack>>> action) {
        ApugliRegisters.ITEM_ACTIONS.register(name, () -> new FabricItemAction(action.getSerializableData(), action::execute));
    }
    
    @Override
    public void executeItem(SerializableData.Instance data, String fieldName, Level level, Mutable<ItemStack> mutable) {
        if(data.isPresent(fieldName)) ((ConfiguredItemAction<?, ?>)data.get(fieldName)).execute(level, mutable);
    }
    
    @Override
    public Consumer<Tuple<Level, Mutable<ItemStack>>> itemConsumer(SerializableData.Instance data, String fieldName) {
        if(!data.isPresent(fieldName)) {
            return null;
        }
        ConfiguredItemAction<?, ?> action = data.get(fieldName);
        return (levelAndStack) -> {
            Level level = levelAndStack.getA();
            Mutable<ItemStack> stack = levelAndStack.getB();
            action.execute(level, stack);
        };
    }
    
}
