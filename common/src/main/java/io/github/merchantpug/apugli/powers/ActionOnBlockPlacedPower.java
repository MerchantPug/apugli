package io.github.merchantpug.apugli.powers;

import io.github.apace100.origins.power.Power;
import io.github.apace100.origins.power.PowerType;
import io.github.apace100.origins.power.factory.PowerFactory;
import io.github.apace100.origins.power.factory.condition.ConditionFactory;
import io.github.apace100.origins.util.SerializableData;
import io.github.apace100.origins.util.SerializableDataType;
import io.github.merchantpug.apugli.Apugli;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Triple;

import java.util.Optional;
import java.util.function.Consumer;

@SuppressWarnings("deprecation")
public class ActionOnBlockPlacedPower extends Power {
    public final ConditionFactory<ItemStack>.Instance itemCondition;
    private final Consumer<Triple<World, BlockPos, Direction>> blockAction;

    public static PowerFactory<?> getFactory() {
        return new PowerFactory<ActionOnBlockPlacedPower>(Apugli.identifier("action_on_block_placed"),
                new SerializableData()
                        .add("block_action", SerializableDataType.BLOCK_ACTION, null)
                        .add("item_condition", SerializableDataType.ITEM_CONDITION),
                data ->
                        (type, player) ->
                                new ActionOnBlockPlacedPower(type, player, (ConditionFactory<ItemStack>.Instance) data.get("item_condition"), (Consumer<Triple<World, BlockPos, Direction>>) data.get("block_action")))
                .allowCondition();
    }

    public void executeAction(Optional<BlockPos> placedBlockPos) {
        if (!placedBlockPos.isPresent() || blockAction == null) return;
        blockAction.accept(Triple.of(player.world, placedBlockPos.get(), Direction.UP));
    }

    public ActionOnBlockPlacedPower(PowerType<?> type, PlayerEntity player, ConditionFactory<ItemStack>.Instance itemCondition, Consumer<Triple<World, BlockPos, Direction>> blockAction) {
        super(type, player);
        this.itemCondition = itemCondition;
        this.blockAction = blockAction;
    }
}

