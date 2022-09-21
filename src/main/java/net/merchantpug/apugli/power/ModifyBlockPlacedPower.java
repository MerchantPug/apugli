package net.merchantpug.apugli.power;

import net.merchantpug.apugli.Apugli;
import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataType;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Triple;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class ModifyBlockPlacedPower extends Power {
    private final List<BlockState> blockStates = new ArrayList<>();
    public final ConditionFactory<ItemStack>.Instance itemCondition;
    private final Consumer<Triple<World, BlockPos, Direction>> blockAction;
    private int seed = (int)(Math.random() * Integer.MAX_VALUE);

    public static PowerFactory<?> getFactory() {
        return new PowerFactory<ModifyBlockPlacedPower>(Apugli.identifier("modify_block_placed"),
                new SerializableData()
                        .add("block", SerializableDataTypes.BLOCK, null)
                        .add("block_state", SerializableDataTypes.BLOCK_STATE, null)
                        .add("blocks", SerializableDataType.list(SerializableDataTypes.BLOCK), null)
                        .add("block_states", SerializableDataType.list(SerializableDataTypes.BLOCK_STATE), null)
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

    @Override
    public void fromTag(NbtElement tag) {
        if (!(tag instanceof NbtCompound)) return;
        this.seed = ((NbtCompound) tag).getInt("Seed");
    }

    @Override
    public NbtElement toTag() {
        NbtCompound nbt =  new NbtCompound();
        nbt.putInt("Seed", this.seed);
        return nbt;
    }

    public int getSeed() {
        return this.seed;
    }

    public void generateSeed() {
        if (!(entity instanceof PlayerEntity)) return;
        if (!entity.world.isClient()) {
            this.seed = (int)(Math.random() * Integer.MAX_VALUE);
            PowerHolderComponent.syncPower(entity, this.getType());
        }
    }

    public void executeAction(Optional<BlockPos> placedBlockPos) {
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
