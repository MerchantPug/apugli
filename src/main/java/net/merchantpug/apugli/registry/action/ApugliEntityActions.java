package net.merchantpug.apugli.registry.action;

import com.github.merchantpug.apugli.action.entity.*;
import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.apoli.registry.ApoliRegistries;
import net.merchantpug.apugli.action.entity.*;
import net.minecraft.entity.*;
import net.minecraft.util.registry.Registry;

public class ApugliEntityActions {
    public static void register() {
        register(ApugliExplodeAction.getFactory());
        register(FireProjectileAction.getFactory());
        register(RaycastAction.getFactory());
        register(ResourceTransferAction.getFactory());
        register(SetNoGravityAction.getFactory());
        register(SpawnItemAction.getFactory());
        register(ZombifyVillagerAction.getFactory());
    }

    private static void register(ActionFactory<Entity> actionFactory) {
        Registry.register(ApoliRegistries.ENTITY_ACTION, actionFactory.getSerializerId(), actionFactory);
    }
}
