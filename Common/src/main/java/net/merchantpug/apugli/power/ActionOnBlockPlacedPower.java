package net.merchantpug.apugli.power;

import net.merchantpug.apugli.platform.Services;
import net.merchantpug.apugli.power.factory.SimplePowerFactory;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.calio.data.SerializableData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.tuple.Triple;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Predicate;


public class ActionOnBlockPlacedPower extends Power {
    @Nullable private final Consumer<Triple<Level, BlockPos, Direction>> blockAction;
    public final Predicate<ItemStack> itemCondition;
    
    public ActionOnBlockPlacedPower(PowerType<?> type, LivingEntity entity,
                                    @Nullable Consumer<Triple<Level, BlockPos, Direction>> blockAction,
                                    Predicate<ItemStack> itemCondition) {
        super(type, entity);
        this.itemCondition = itemCondition;
        this.blockAction = blockAction;
    }
    
    public void executeAction(@Nullable BlockPos pos) {
        if(pos == null || blockAction == null) return;
        blockAction.accept(Triple.of(entity.level, pos, Direction.UP));
    }
    
    public static class Factory extends SimplePowerFactory<ActionOnBlockPlacedPower> {
        
        public Factory() {
            super("action_on_block_placed",
                new SerializableData()
                    .add("block_action", Services.ACTION.blockDataType(), null)
                    .add("item_condition", Services.CONDITION.itemDataType()),
                data -> (type, entity) -> new ActionOnBlockPlacedPower(type, entity,
                    Services.ACTION.blockConsumer(data, "block_action"),
                    Services.CONDITION.itemPredicate(data, "item_condition")
                ));
            allowCondition();
        }
        
        @Override
        public Class<ActionOnBlockPlacedPower> getPowerClass() {
            return ActionOnBlockPlacedPower.class;
        }
        
    }
    
}

