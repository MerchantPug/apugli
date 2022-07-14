package com.github.merchantpug.apugli.powers;

import io.github.apace100.origins.power.Power;
import io.github.apace100.origins.power.PowerType;
import io.github.apace100.origins.power.factory.PowerFactory;
import io.github.apace100.origins.power.factory.action.ActionFactory;
import io.github.apace100.origins.power.factory.condition.ConditionFactory;
import io.github.apace100.origins.util.SerializableData;
import io.github.apace100.origins.util.SerializableDataType;
import com.github.merchantpug.apugli.Apugli;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Triple;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class ActionOnBoneMealPower extends Power {
    private final Consumer<Triple<World, BlockPos, Direction>> blockAction;
    private final Consumer<Entity> entityAction;
    private final Predicate<CachedBlockPosition> blockCondition;

    public static PowerFactory<?> getFactory() {
        return new PowerFactory<ActionOnBoneMealPower>(Apugli.identifier("action_on_bonemeal"),
                new SerializableData()
                        .add("block_action", SerializableDataType.BLOCK_ACTION)
                        .add("self_action", SerializableDataType.ENTITY_ACTION, null)
                        .add("block_condition", SerializableDataType.BLOCK_CONDITION, null),
                data ->
                        (type, player) ->
                                new ActionOnBoneMealPower(type, player,
                                        (ActionFactory<Triple<World, BlockPos, Direction>>.Instance)data.get("block_action"),
                                        (ActionFactory<Entity>.Instance)data.get("self_action"),
                                        data.isPresent("block_condition") ? (ConditionFactory<CachedBlockPosition>.Instance)data.get("block_condition") : cachedBlockPosition -> true))
                .allowCondition();
    }

    public boolean doesApply(CachedBlockPosition cachedBlockPosition) {
        return blockCondition.test(cachedBlockPosition);
    }

    public void executeActions(World world, BlockPos blockPos, Direction direction) {
        blockAction.accept(Triple.of(world, blockPos, direction));
        if (entityAction != null) {
            entityAction.accept(player);
        }
    }

    public ActionOnBoneMealPower(PowerType<?> type, PlayerEntity player, Consumer<Triple<World, BlockPos, Direction>> blockAction, Consumer<Entity> entityAction, Predicate<CachedBlockPosition> blockCondition) {
        super(type, player);
        this.blockAction = blockAction;
        this.entityAction = entityAction;
        this.blockCondition = blockCondition;
    }
}
