package io.github.merchantpug.apugli.registry.action;

import dev.architectury.injectables.annotations.ExpectPlatform;
import io.github.apace100.origins.power.factory.action.ActionFactory;
import io.github.merchantpug.apugli.action.entity.*;
import io.github.merchantpug.apugli.action.item.DamageAction;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;

public class ApugliItemActions {
    public static void register() {
        register(DamageAction.getFactory());
    }

    @ExpectPlatform
    private static void register(ActionFactory<ItemStack> actionFactory) {
        throw new AssertionError();
    }
}
