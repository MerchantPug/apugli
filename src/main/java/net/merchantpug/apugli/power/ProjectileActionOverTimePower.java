package net.merchantpug.apugli.power;

import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.merchantpug.apugli.Apugli;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.util.Pair;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class ProjectileActionOverTimePower extends Power {
    private final int interval;
    private final Consumer<Pair<Entity, Entity>> biEntityAction;
    private final Consumer<Pair<Entity, Entity>> risingAction;
    private final Consumer<Pair<Entity, Entity>> fallingAction;
    private final Predicate<Pair<Entity, Entity>> biEntityCondition;

    private boolean wasActive = false;

    private final Map<ProjectileEntity, Integer> initialTicks = new HashMap<>();

    public ProjectileActionOverTimePower(PowerType<?> type, LivingEntity entity, int interval, Consumer<Pair<Entity, Entity>> biEntityAction, Consumer<Pair<Entity, Entity>> risingAction, Consumer<Pair<Entity, Entity>> fallingAction, Predicate<Pair<Entity, Entity>> biEntityCondition) {
        super(type, entity);
        this.interval = interval;
        this.biEntityAction = biEntityAction;
        this.risingAction = risingAction;
        this.fallingAction = fallingAction;
        this.biEntityCondition = biEntityCondition;
    }

    public boolean doesApply(ProjectileEntity projectile) {
        return this.biEntityCondition == null || this.biEntityCondition.test(new Pair<>(entity, projectile));
    }

    public void projectileTick(ProjectileEntity projectile) {
        if (!initialTicks.containsKey(projectile)) {
            initialTicks.put(projectile, projectile.age % interval);
        }
        if (entity.age % interval == initialTicks.get(projectile)) {
            if (isActive() && doesApply(projectile)) {
                if (!wasActive && risingAction != null) {
                    risingAction.accept(new Pair<>(entity, projectile));
                }
                if (biEntityAction != null) {
                    biEntityAction.accept(new Pair<>(entity, projectile));
                }
                wasActive = true;
            } else {
                if (wasActive && fallingAction != null) {
                    fallingAction.accept(new Pair<>(entity, projectile));
                }
                wasActive = false;
            }
        }
        if (projectile.isRemoved()) {
            initialTicks.remove(projectile);
        }
    }

    public static PowerFactory<?> getFactory() {
        return new PowerFactory<ProjectileActionOverTimePower>(
                Apugli.identifier("projectile_action_over_time"),
                new SerializableData()
                        .add("interval", SerializableDataTypes.INT, 20)
                        .add("bientity_action", ApoliDataTypes.BIENTITY_ACTION, null)
                        .add("rising_action", ApoliDataTypes.BIENTITY_ACTION, null)
                        .add("falling_action", ApoliDataTypes.BIENTITY_ACTION, null)
                        .add("bientity_condition", ApoliDataTypes.BIENTITY_CONDITION, null),
                data -> (type, entity) -> new ProjectileActionOverTimePower(type, entity, data.getInt("interval"), data.get("bientity_action"), data.get("rising_action"), data.get("falling_action"), data.get("bientity_condition"))
        ).allowCondition();
    }
}
