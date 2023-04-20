package net.merchantpug.apugli.registry.action;

import net.merchantpug.apugli.action.factory.IActionFactory;
import net.merchantpug.apugli.action.factory.item.DamageAction;
import net.merchantpug.apugli.platform.Services;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.mutable.Mutable;

public class ApugliItemActions {
    
    public static void registerAll() {
        register("damage", new DamageAction());
    }
    
    private static void register(String name, IActionFactory<Tuple<Level, Mutable<ItemStack>>> factory) {
        Services.ACTION.registerItem(name, factory);
    }
    
}
