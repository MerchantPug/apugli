package net.merchantpug.apugli.power;

import net.merchantpug.apugli.Apugli;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.calio.data.SerializableData;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
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
                        .add("block_action", ApoliDataTypes.BLOCK_ACTION)
                        .add("self_action", ApoliDataTypes.ENTITY_ACTION, null)
                        .add("block_condition", ApoliDataTypes.BLOCK_CONDITION, null),
                data ->
                        (type, entity) ->
                                new ActionOnBoneMealPower(type, entity,
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
            entityAction.accept(entity);
        }
    }

    public ActionOnBoneMealPower(PowerType<?> type, LivingEntity entity, Consumer<Triple<World, BlockPos, Direction>> blockAction, Consumer<Entity> entityAction, Predicate<CachedBlockPosition> blockCondition) {
        super(type, entity);
        this.blockAction = blockAction;
        this.entityAction = entityAction;
        this.blockCondition = blockCondition;
    }
}
