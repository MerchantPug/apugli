package net.merchantpug.apugli.registry.action;

import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.apoli.registry.ApoliRegistries;
import net.merchantpug.apugli.action.bientity.ChangeHitsOnTargetAction;
import net.minecraft.entity.Entity;
import net.minecraft.util.Pair;
import net.minecraft.util.registry.Registry;

public class ApugliBiEntityActions {
    public static void register() {
        register(ChangeHitsOnTargetAction.getFactory());
    }

    private static void register(ActionFactory<Pair<Entity, Entity>> actionFactory) {
        Registry.register(ApoliRegistries.BIENTITY_ACTION, actionFactory.getSerializerId(), actionFactory);
    }
}
