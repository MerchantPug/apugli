package net.merchantpug.apugli.power;

import net.merchantpug.apugli.platform.Services;
import net.merchantpug.apugli.power.factory.SimplePowerFactory;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataType;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.tuple.Triple;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class ModifyBlockPlacedPower extends Power {
    private final List<BlockState> blockStates = new ArrayList<>();
    public final Predicate<Tuple<Level, ItemStack>> itemCondition;
    private final Consumer<Triple<Level, BlockPos, Direction>> blockAction;
    private int seed = (int)(Math.random() * Integer.MAX_VALUE);

    public ModifyBlockPlacedPower(PowerType<?> type, LivingEntity entity, Predicate<Tuple<Level, ItemStack>> itemCondition, Consumer<Triple<Level, BlockPos, Direction>> blockAction) {
        super(type, entity);
        this.itemCondition = itemCondition;
        this.blockAction = blockAction;
    }

    @Override
    public void fromTag(Tag tag) {
        if(!(tag instanceof CompoundTag)) return;
        this.seed = ((CompoundTag) tag).getInt("Seed");
    }

    @Override
    public Tag toTag() {
        CompoundTag nbt =  new CompoundTag();
        nbt.putInt("Seed", this.seed);
        return nbt;
    }

    public int getSeed() {
        return this.seed;
    }

    public void generateSeed() {
        if(!(entity instanceof Player)) return;
        if(!entity.level.isClientSide()) {
            this.seed = (int)(Math.random() * Integer.MAX_VALUE);
            Services.POWER.syncPower(entity, this.getType());
        }
    }

    public void executeAction(Optional<BlockPos> placedBlockPos) {
        if(placedBlockPos.isEmpty() || blockAction == null) return;
        blockAction.accept(Triple.of(entity.level, placedBlockPos.get(), Direction.UP));
    }

    public void addBlockState(BlockState blockState) {
        this.blockStates.add(blockState);
    }

    public List<BlockState> getBlockStates() {
        return blockStates;
    }

    public static class Factory extends SimplePowerFactory<ModifyBlockPlacedPower> {

        public Factory() {
            super("modify_block_placed",
                    new SerializableData()
                            .add("block", SerializableDataTypes.BLOCK, null)
                            .add("block_state", SerializableDataTypes.BLOCK_STATE, null)
                            .add("blocks", SerializableDataType.list(SerializableDataTypes.BLOCK), null)
                            .add("block_states", SerializableDataType.list(SerializableDataTypes.BLOCK_STATE), null)
                            .add("block_action", Services.ACTION.blockDataType(), null)
                            .add("item_condition", Services.CONDITION.itemDataType()),
                    data -> (type, entity) -> {
                                ModifyBlockPlacedPower power = new ModifyBlockPlacedPower(type, entity, Services.CONDITION.itemPredicate(data, "item_condition"), Services.ACTION.blockConsumer(data, "block_action"));
                                data.<Block>ifPresent("block", block -> power.addBlockState(block.defaultBlockState()));
                                data.ifPresent("block_state", power::addBlockState);
                                data.<List<Block>>ifPresent("blocks", blocks -> blocks.forEach(block -> power.addBlockState(block.defaultBlockState())));
                                data.<List<BlockState>>ifPresent("block_states", states -> states.forEach(power::addBlockState));
                        return power;
                            });
            allowCondition();
        }

        @Override
        public Class<ModifyBlockPlacedPower> getPowerClass() {
            return ModifyBlockPlacedPower.class;
        }

    }

}
