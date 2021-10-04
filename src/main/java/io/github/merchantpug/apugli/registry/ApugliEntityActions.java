package io.github.merchantpug.apugli.registry;

import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.apoli.registry.ApoliRegistries;
import io.github.merchantpug.apugli.action.entity.*;
import net.minecraft.entity.*;
import net.minecraft.util.registry.Registry;

public class ApugliEntityActions {
    public static void register() {
        register(ApugliExplodeAction.getFactory());
        register(RaycastAction.getFactory());
        register(SwingHandAction.getFactory());
        register(ZombifyVillagerAction.getFactory());
        register(DropItemAction.getFactory());
    }

    private static void register(ActionFactory<Entity> actionFactory) {
        Registry.register(ApoliRegistries.ENTITY_ACTION, actionFactory.getSerializerId(), actionFactory);
    }
}
