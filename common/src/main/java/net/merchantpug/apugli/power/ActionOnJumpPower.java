package net.merchantpug.apugli.power;

import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.calio.data.SerializableData;
import net.merchantpug.apugli.platform.Services;
import net.merchantpug.apugli.power.factory.SimplePowerFactory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class ActionOnJumpPower extends Power {
    @Nullable private final Consumer<Entity> entityAction;

    public ActionOnJumpPower(PowerType<?> type, LivingEntity entity,
                             @Nullable Consumer<Entity> entityAction) {
        super(type, entity);
        this.entityAction = entityAction;
    }
    
    public void executeAction() {
        if (entityAction == null) return;
        entityAction.accept(entity);
    }
    
    public static class Factory extends SimplePowerFactory<ActionOnJumpPower> {
        
        public Factory() {
            super("action_on_jump",
                new SerializableData()
                    .add("entity_action", Services.ACTION.entityDataType(), null),
                data -> (type, entity) -> new ActionOnJumpPower(type, entity,
                        Services.ACTION.entityConsumer(data, "entity_action")
                ));
            allowCondition();
        }
        
        @Override
        public Class<ActionOnJumpPower> getPowerClass() {
            return ActionOnJumpPower.class;
        }
        
    }
    
}

