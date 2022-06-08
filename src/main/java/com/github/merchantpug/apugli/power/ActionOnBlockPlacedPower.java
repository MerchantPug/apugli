package com.github.merchantpug.apugli.power;

import com.github.merchantpug.apugli.Apugli;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.calio.data.SerializableData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Triple;

import java.util.Optional;
import java.util.function.Consumer;

public class ActionOnBlockPlacedPower extends Power {
    public final ConditionFactory<ItemStack>.Instance itemCondition;
    private final Consumer<Triple<World, BlockPos, Direction>> blockAction;

    public static PowerFactory<?> getFactory() {
        return new PowerFactory<ActionOnBlockPlacedPower>(Apugli.identifier("action_on_block_placed"),
                new SerializableData()
                        .add("block_action", ApoliDataTypes.BLOCK_ACTION, null)
                        .add("item_condition", ApoliDataTypes.ITEM_CONDITION),
                data ->
                        (type, entity) ->
                                new ActionOnBlockPlacedPower(type, entity, (ConditionFactory<ItemStack>.Instance) data.get("item_condition"), (Consumer<Triple<World, BlockPos, Direction>>) data.get("block_action")))
                .allowCondition();
    }

    public void executeAction(Optional<BlockPos> placedBlockPos) {
        if (placedBlockPos.isEmpty() || blockAction == null) return;
        blockAction.accept(Triple.of(entity.world, placedBlockPos.get(), Direction.UP));
    }

    public ActionOnBlockPlacedPower(PowerType<?> type, LivingEntity entity, ConditionFactory<ItemStack>.Instance itemCondition, Consumer<Triple<World, BlockPos, Direction>> blockAction) {
        super(type, entity);
        this.itemCondition = itemCondition;
        this.blockAction = blockAction;
    }
}

