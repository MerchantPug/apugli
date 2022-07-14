package io.github.merchantpug.apugli.powers;

import io.github.apace100.origins.component.OriginComponent;
import io.github.apace100.origins.power.Power;
import io.github.apace100.origins.power.PowerType;
import io.github.apace100.origins.power.factory.PowerFactory;
import io.github.apace100.origins.power.factory.condition.ConditionFactory;
import io.github.apace100.origins.util.SerializableData;
import io.github.apace100.origins.util.SerializableDataType;
import io.github.merchantpug.apugli.Apugli;
import io.github.merchantpug.apugli.util.BackportedDataTypes;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Triple;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

@SuppressWarnings("deprecation")
public class ModifyBlockPlacedPower extends Power {
    private final List<BlockState> blockStates = new ArrayList<>();
    public final ConditionFactory<ItemStack>.Instance itemCondition;
    private final Consumer<Triple<World, BlockPos, Direction>> blockAction;
    private int seed = (int)(Math.random() * Integer.MAX_VALUE);

    public static PowerFactory<?> getFactory() {
        return new PowerFactory<ModifyBlockPlacedPower>(Apugli.identifier("modify_block_placed"),
                new SerializableData()
                        .add("block", SerializableDataType.BLOCK, null)
                        .add("block_state", BackportedDataTypes.BLOCK_STATE, null)
                        .add("blocks", SerializableDataType.list(SerializableDataType.BLOCK), null)
                        .add("block_states", SerializableDataType.list(BackportedDataTypes.BLOCK_STATE), null)
                        .add("block_action", SerializableDataType.BLOCK_ACTION, null)
                        .add("item_condition", SerializableDataType.ITEM_CONDITION),
                data ->
                        (type, player) -> {
                            ModifyBlockPlacedPower power = new ModifyBlockPlacedPower(type, player, (ConditionFactory<ItemStack>.Instance)data.get("item_condition"), (Consumer<Triple<World, BlockPos, Direction>>)data.get("block_action"));
                            if(data.isPresent("block")) {
                                power.addBlockState(((Block)data.get("block")).getDefaultState());
                            }
                            if(data.isPresent("block_state")) {
                                power.addBlockState((BlockState)data.get("block_state"));
                            }
                            if(data.isPresent("blocks")) {
                                ((List<Block>)data.get("blocks")).forEach(block -> power.addBlockState(block.getDefaultState()));
                            }
                            if(data.isPresent("block_states")) {
                                ((List<BlockState>)data.get("block_states")).forEach(power::addBlockState);
                            }
                            return power;
                        })
                .allowCondition();
    }

    @Override
    public void fromTag(Tag tag) {
        if (!(tag instanceof CompoundTag)) return;
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
        if (!(player instanceof PlayerEntity)) return;
        if (!player.world.isClient()) {
            this.seed = (int)(Math.random() * Integer.MAX_VALUE);
            OriginComponent.sync(player);
        }
    }

    public void executeAction(Optional<BlockPos> placedBlockPos) {
        if (!placedBlockPos.isPresent() || blockAction == null) return;
        blockAction.accept(Triple.of(player.world, placedBlockPos.get(), Direction.UP));
    }

    public void addBlockState(BlockState blockState) {
        this.blockStates.add(blockState);
    }

    public List<BlockState> getBlockStates() {
        return blockStates;
    }

    public ModifyBlockPlacedPower(PowerType<?> type, PlayerEntity player, ConditionFactory<ItemStack>.Instance itemCondition, Consumer<Triple<World, BlockPos, Direction>> blockAction) {
        super(type, player);
        this.itemCondition = itemCondition;
        this.blockAction = blockAction;
    }
}
