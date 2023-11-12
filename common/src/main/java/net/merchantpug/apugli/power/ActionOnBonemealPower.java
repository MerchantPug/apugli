package net.merchantpug.apugli.power;

import net.merchantpug.apugli.platform.Services;
import net.merchantpug.apugli.power.factory.SimplePowerFactory;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.calio.data.SerializableData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import org.apache.commons.lang3.tuple.Triple;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class ActionOnBonemealPower extends Power {
    @Nullable private final Consumer<Triple<Level, BlockPos, Direction>> blockAction;
    @Nullable private final Consumer<Entity> entityAction;
    @Nullable private final Predicate<BlockInWorld> blockCondition;
    
    public ActionOnBonemealPower(PowerType<?> type, LivingEntity entity,
                                 @Nullable Consumer<Triple<Level, BlockPos, Direction>> blockAction,
                                 @Nullable Consumer<Entity> entityAction,
                                 @Nullable Predicate<BlockInWorld> blockCondition) {
        super(type, entity);
        this.blockAction = blockAction;
        this.entityAction = entityAction;
        this.blockCondition = blockCondition;
    }
    
    public boolean doesApply(BlockInWorld cachedBlockPosition) {
        return blockCondition == null || blockCondition.test(cachedBlockPosition);
    }
    
    public void executeActions(Level world, BlockPos blockPos, Direction direction) {
        if(blockAction != null) blockAction.accept(Triple.of(world, blockPos, direction));
        if(entityAction != null) entityAction.accept(entity);
    }
    
    public static class Factory extends SimplePowerFactory<ActionOnBonemealPower> {
        
        public Factory() {
            super("action_on_bonemeal",
                new SerializableData()
                    .add("block_action", Services.ACTION.blockDataType(), null)
                    .add("self_action", Services.ACTION.entityDataType(), null)
                    .add("block_condition", Services.CONDITION.biomeDataType(), null),
                data -> (type, entity) -> new ActionOnBonemealPower(type, entity,
                    Services.ACTION.blockConsumer(data, "block_action"),
                    Services.ACTION.entityConsumer(data, "self_action"),
                    Services.CONDITION.blockPredicate(data, "block_condition")
                ));
            allowCondition();
        }
        
        @Override
        public Class<ActionOnBonemealPower> getPowerClass() {
            return ActionOnBonemealPower.class;
        }
        
    }
    
}
