package net.merchantpug.apugli.platform;

import com.google.auto.service.AutoService;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.apoli.registry.ApoliRegistries;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataType;
import io.netty.buffer.Unpooled;
import net.merchantpug.apugli.Apugli;
import net.merchantpug.apugli.action.factory.IActionFactory;
import net.merchantpug.apugli.platform.services.IActionHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
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
    public <A> void executeBiEntity(A action, Entity actor, Entity target) {
        if (action != null) ((Consumer<Tuple<Entity, Entity>>)action).accept(new Tuple<>(actor, target));
    }

    @Override
    @Nullable
    public Consumer<Tuple<Entity, Entity>> biEntityConsumer(SerializableData.Instance data, String fieldName) {
        return data.get(fieldName);
    }

    @Override
    public <T> void writeBiEntityActionToNbt(CompoundTag tag, String path, T object) {
        if (object == getBiEntityDefault()) return;

        ActionFactory<Triple<Level, BlockPos, Direction>>.Instance instance = (ActionFactory<Triple<Level, BlockPos, Direction>>.Instance) object;

        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        ApoliDataTypes.BIENTITY_ACTION.send(buf, instance);

        tag.putByteArray(path, buf.array());
    }

    @Override
    public <T> T readBiEntityActionFromNbt(CompoundTag tag, String path) {
        if (!tag.contains(path, Tag.TAG_COMPOUND)) {
            return null;
        }

        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.setBytes(0, tag.getByteArray(path));

        return (T) ApoliDataTypes.BIENTITY_ACTION.receive(buf);
    }

    @Override
    public <T> T getBiEntityDefault() {
        return null;
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
    public <A> void executeBlock(A action, Level level, BlockPos pos, Direction direction) {
        if (action != null)
            ((Consumer<Triple<Level, BlockPos, Direction>>) action).accept(Triple.of(level, pos, direction));
    }

    @Override
    public Consumer<Triple<Level, BlockPos, Direction>> blockConsumer(SerializableData.Instance data, String fieldName) {
        return data.get(fieldName);
    }

    @Override
    public <T> void writeBlockActionToNbt(CompoundTag tag, String path, T object) {
        if (object == getBlockDefault()) return;

        ActionFactory<Triple<Level, BlockPos, Direction>>.Instance instance = (ActionFactory<Triple<Level, BlockPos, Direction>>.Instance) object;

        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        ApoliDataTypes.BLOCK_ACTION.send(buf, instance);

        tag.putByteArray(path, buf.array());
    }

    @Override
    public <T> T readBlockActionFromNbt(CompoundTag tag, String path) {
        if (!tag.contains(path, Tag.TAG_COMPOUND)) {
            return null;
        }

        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.setBytes(0, tag.getByteArray(path));

        return (T) ApoliDataTypes.BLOCK_ACTION.receive(buf);
    }

    @Override
    public <T> T getBlockDefault() {
        return null;
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
    public <A> void executeEntity(A action, Entity entity) {
        if (action != null) ((Consumer<Entity>)action).accept(entity);
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
    public <A> void executeEntity(A action, Level level, Mutable<ItemStack> mutable) {
        if (action != null) ((Consumer<Tuple<Level, ItemStack>>)action).accept(new Tuple<>(level, mutable.getValue()));
    }

    @Override
    public Consumer<Tuple<Level, Mutable<ItemStack>>> itemConsumer(SerializableData.Instance data, String fieldName) {
        return (data.isPresent(fieldName)) ? (levelAndStack) -> ((Consumer<Tuple<Level, ItemStack>>)data.get(fieldName)).accept(new Tuple<>(levelAndStack.getA(), levelAndStack.getB().getValue())) : null;
    }
    
}
