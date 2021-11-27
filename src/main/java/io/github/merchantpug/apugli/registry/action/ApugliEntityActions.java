package io.github.merchantpug.apugli.registry.action;

import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.apoli.registry.ApoliRegistries;
import io.github.merchantpug.apugli.action.entity.*;
import net.minecraft.entity.*;
import net.minecraft.util.registry.Registry;

public class ApugliEntityActions {
    public static void register() {
        register(ApugliExplodeAction.getFactory());
        register(DropItemAction.getFactory());
        register(FireProjectileAction.getFactory());
        register(RaycastAction.getFactory());
        register(SetNoGravityAction.getFactory());
        register(SpawnItemAction.getFactory());
        register(SwingHandAction.getFactory());
        register(ZombifyVillagerAction.getFactory());
    }

    private static void register(ActionFactory<Entity> actionFactory) {
        Registry.register(ApoliRegistries.ENTITY_ACTION, actionFactory.getSerializerId(), actionFactory);
    }
}
