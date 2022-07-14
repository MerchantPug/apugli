package io.github.merchantpug.apugli.registry.action.fabric;

import io.github.apace100.origins.power.factory.action.ActionFactory;
import io.github.apace100.origins.registry.ModRegistries;
import net.minecraft.item.ItemStack;
import net.minecraft.util.registry.Registry;

public class ApugliItemActionsImpl {
    public static void register(ActionFactory<ItemStack> actionFactory) {
        Registry.register(ModRegistries.ITEM_ACTION, actionFactory.getSerializerId(), actionFactory);
    }
}
