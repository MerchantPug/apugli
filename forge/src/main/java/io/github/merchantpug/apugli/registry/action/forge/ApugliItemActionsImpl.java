package io.github.merchantpug.apugli.registry.action.forge;

import io.github.apace100.origins.power.factory.action.ActionFactory;
import io.github.merchantpug.apugli.registry.forge.ApugliRegistriesArchitectury;
import net.minecraft.item.ItemStack;

public class ApugliItemActionsImpl {
    public static void register(ActionFactory<ItemStack> actionFactory) {
        ApugliRegistriesArchitectury.ITEM_ACTION.register(actionFactory.getSerializerId(), () -> actionFactory);
    }
}
