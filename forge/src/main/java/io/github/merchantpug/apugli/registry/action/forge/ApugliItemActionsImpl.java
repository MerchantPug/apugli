package io.github.merchantpug.apugli.registry.action.forge;

import io.github.apace100.origins.power.factory.action.ActionFactory;
import io.github.apace100.origins.registry.ModRegistriesArchitectury;
import net.minecraft.item.ItemStack;

public class ApugliItemActionsImpl {
    public static void register(ActionFactory<ItemStack> actionFactory) {
        ModRegistriesArchitectury.ITEM_ACTION.register(actionFactory.getSerializerId(), () -> actionFactory);
    }
}
