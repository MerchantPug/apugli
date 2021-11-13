package io.github.merchantpug.apugli.power;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.merchantpug.apugli.Apugli;
import io.github.merchantpug.apugli.access.PlayerEntityAccess;
import io.github.merchantpug.apugli.util.ApugliDataTypes;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Triple;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.Consumer;

public class ModifyBlockPlacedPower extends Power {
    private final List<BlockState> blockStates = new ArrayList<>();
    public final ConditionFactory<ItemStack>.Instance itemCondition;
    private final Consumer<Triple<World, BlockPos, Direction>> blockAction;
    private int seed;

    public static PowerFactory<?> getFactory() {
        return new PowerFactory<ModifyBlockPlacedPower>(Apugli.identifier("modify_block_placed"),
                new SerializableData()
                        .add("block", SerializableDataTypes.BLOCK, null)
                        .add("block_state", SerializableDataTypes.BLOCK_STATE, null)
                        .add("blocks", ApugliDataTypes.BLOCKS, null)
                        .add("block_states", ApugliDataTypes.BLOCK_STATES, null)
                        .add("block_action", ApoliDataTypes.BLOCK_ACTION, null)
                        .add("item_condition", ApoliDataTypes.ITEM_CONDITION),
                data ->
                        (type, entity) -> {
                            ModifyBlockPlacedPower power = new ModifyBlockPlacedPower(type, entity, (ConditionFactory<ItemStack>.Instance)data.get("item_condition"), (Consumer<Triple<World, BlockPos, Direction>>)data.get("block_action"));
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

    public int getSeed() {
        return ((PlayerEntityAccess)(Object)entity).getSeed();
    }

    public void generateSeed() {
        if (!(entity instanceof PlayerEntity)) return;
        if (!entity.world.isClient()) {
            seed = (int)(Math.random() * Integer.MAX_VALUE);
            PowerHolderComponent.syncPower(entity, this.getType());
        }
        ((PlayerEntityAccess)(Object)entity).setSeed(seed);
    }

    public void executeActions(Optional<BlockPos> placedBlockPos) {
        if (placedBlockPos.isEmpty() || blockAction == null) return;
        blockAction.accept(Triple.of(entity.world, placedBlockPos.get(), Direction.UP));
    }

    public void addBlockState(BlockState blockState) {
        this.blockStates.add(blockState);
    }

    public List<BlockState> getBlockStates() {
        return blockStates;
    }

    public ModifyBlockPlacedPower(PowerType<?> type, LivingEntity entity, ConditionFactory<ItemStack>.Instance itemCondition, Consumer<Triple<World, BlockPos, Direction>> blockAction) {
        super(type, entity);
        this.itemCondition = itemCondition;
        this.blockAction = blockAction;
    }
}
