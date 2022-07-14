package com.github.merchantpug.apugli.registry.action;

import com.github.merchantpug.apugli.action.entity.*;
import dev.architectury.injectables.annotations.ExpectPlatform;
import io.github.apace100.origins.power.factory.action.ActionFactory;
import com.github.merchantpug.apugli.action.entity.*;
import net.minecraft.entity.Entity;

public class ApugliEntityActions {
    public static void register() {
        register(ApugliExplodeAction.getFactory());
        register(AreaOfEffectAction.getFactory());
        register(CraftingTableAction.getFactory());
        register(DropItemAction.getFactory());
        register(EnderChestAction.getFactory());
        register(FireProjectileAction.getFactory());
        register(RaycastAction.getFactory());
        register(ResourceTransferAction.getFactory());
        register(SetNoGravityAction.getFactory());
        register(SetResourceAction.getFactory());
        register(SpawnItemAction.getFactory());
        register(SpawnParticlesAction.getFactory());
        register(SwingHandAction.getFactory());
        register(ToggleAction.getFactory());
        register(ZombifyVillagerAction.getFactory());
    }

    @ExpectPlatform
    private static void register(ActionFactory<Entity> actionFactory) {
        throw new AssertionError();
    }
}
