package io.github.merchantpug.apugli.registry.action.forge;

import io.github.apace100.origins.power.factory.action.ActionFactory;
import io.github.merchantpug.apugli.registry.forge.ApugliRegistriesArchitectury;
import net.minecraft.entity.Entity;

public class ApugliEntityActionsImpl {
    public static void register(ActionFactory<Entity> actionFactory) {
        ApugliRegistriesArchitectury.ENTITY_ACTION.register(actionFactory.getSerializerId(), () -> actionFactory);
    }
}
