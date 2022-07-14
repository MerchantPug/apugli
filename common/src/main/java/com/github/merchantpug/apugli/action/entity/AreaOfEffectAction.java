package com.github.merchantpug.apugli.action.entity;

import com.github.merchantpug.apugli.Apugli;
import io.github.apace100.origins.power.factory.action.ActionFactory;
import io.github.apace100.origins.util.SerializableData;
import io.github.apace100.origins.util.SerializableDataType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Box;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class AreaOfEffectAction {
    public static void action(SerializableData.Instance data, Entity entity) {
        List<Consumer<Entity>> actions = new ArrayList<>();

        if (data.isPresent("action")) {
            actions.add(data.get("action"));
        }

        if (data.isPresent("actions")) {
            actions.addAll(data.get("actions"));
        }

        Predicate<LivingEntity> predicate = data.isPresent("condition") ? data.get("condition") : x -> true;
        boolean includeTarget = data.get("include_target");
        double radius = data.get("radius");
        double diameter = radius * 2;

        for (LivingEntity check : entity.world.getNonSpectatingEntities(LivingEntity.class, Box.method_30048(diameter, diameter, diameter).offset(entity.method_30950(1F)))) {
            if (check == entity && !includeTarget)
                continue;
            if (predicate.test(check) && check.squaredDistanceTo(entity) < radius * radius)
                actions.forEach(x -> x.accept(check));
        }
    }

    public static ActionFactory<Entity> getFactory() {
        return new ActionFactory<Entity>(Apugli.identifier("area_of_effect"),
            new SerializableData()
                .add("radius", SerializableDataType.DOUBLE, 16D)
                .add("action", SerializableDataType.ENTITY_ACTION, null)
                .add("actions", SerializableDataType.ENTITY_ACTIONS, null)
                .add("condition", SerializableDataType.ENTITY_CONDITION, null)
                .add("include_target", SerializableDataType.BOOLEAN, false),
                AreaOfEffectAction::action
        );
    }
}