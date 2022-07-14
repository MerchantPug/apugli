package com.github.merchantpug.apugli.registry.action;

import com.github.merchantpug.apugli.action.item.DamageAction;
import dev.architectury.injectables.annotations.ExpectPlatform;
import io.github.apace100.origins.power.factory.action.ActionFactory;
import com.github.merchantpug.apugli.action.entity.*;
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
