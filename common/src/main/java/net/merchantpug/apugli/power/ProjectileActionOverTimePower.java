package net.merchantpug.apugli.power;

import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.merchantpug.apugli.platform.Services;
import net.merchantpug.apugli.power.factory.SimplePowerFactory;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class ProjectileActionOverTimePower extends Power {
    private final int interval;
    private final Consumer<Tuple<Entity, Entity>> biEntityAction;
    private final Consumer<Tuple<Entity, Entity>> risingAction;
    private final Consumer<Tuple<Entity, Entity>> fallingAction;
    private final Predicate<Tuple<Entity, Entity>> biEntityCondition;

    private boolean wasActive = false;

    private final Map<Projectile, Integer> initialTicks = new HashMap<>();

    public ProjectileActionOverTimePower(PowerType<?> type, LivingEntity entity, int interval, Consumer<Tuple<Entity, Entity>> biEntityAction, Consumer<Tuple<Entity, Entity>> risingAction, Consumer<Tuple<Entity, Entity>> fallingAction, Predicate<Tuple<Entity, Entity>> biEntityCondition) {
        super(type, entity);
        this.interval = interval;
        this.biEntityAction = biEntityAction;
        this.risingAction = risingAction;
        this.fallingAction = fallingAction;
        this.biEntityCondition = biEntityCondition;
    }

    public boolean doesApply(Projectile projectile) {
        return this.biEntityCondition == null || this.biEntityCondition.test(new Tuple<>(entity, projectile));
    }

    public void projectileTick(Projectile projectile) {
        if (!initialTicks.containsKey(projectile)) {
            initialTicks.put(projectile, projectile.tickCount % interval);
        }
        if (entity.tickCount % interval == initialTicks.get(projectile)) {
            if (isActive() && doesApply(projectile)) {
                if (!wasActive && risingAction != null) {
                    risingAction.accept(new Tuple<>(entity, projectile));
                }
                if (biEntityAction != null) {
                    biEntityAction.accept(new Tuple<>(entity, projectile));
                }
                wasActive = true;
            } else {
                if (wasActive && fallingAction != null) {
                    fallingAction.accept(new Tuple<>(entity, projectile));
                }
                wasActive = false;
            }
        }
        if (projectile.isRemoved()) {
            initialTicks.remove(projectile);
        }
    }

    public static class Factory extends SimplePowerFactory<ProjectileActionOverTimePower> {

        public Factory() {
            super("projectile_action_over_time",
                    new SerializableData()
                            .add("interval", SerializableDataTypes.INT, 20)
                            .add("bientity_action", Services.ACTION.biEntityDataType(), null)
                            .add("rising_action", Services.ACTION.biEntityDataType(), null)
                            .add("falling_action", Services.ACTION.biEntityDataType(), null)
                            .add("bientity_condition", Services.CONDITION.biEntityDataType(), null),
                    data -> (type, entity) -> new ProjectileActionOverTimePower(type, entity, data.getInt("interval"), Services.ACTION.biEntityConsumer(data, "bientity_action"), Services.ACTION.biEntityConsumer(data, "rising_action"), Services.ACTION.biEntityConsumer(data, "falling_action"), Services.CONDITION.biEntityPredicate(data, "bientity_condition")));
            allowCondition();
        }

        @Override
        public Class<ProjectileActionOverTimePower> getPowerClass() {
            return ProjectileActionOverTimePower.class;
        }

    }
}