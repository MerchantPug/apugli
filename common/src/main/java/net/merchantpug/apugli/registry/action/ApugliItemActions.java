package net.merchantpug.apugli.registry.action;

import net.merchantpug.apugli.action.factory.IActionFactory;
import net.merchantpug.apugli.action.factory.item.CooldownAction;
import net.merchantpug.apugli.action.factory.item.DamageAction;
import net.merchantpug.apugli.platform.Services;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.level.Level;

public class ApugliItemActions {
    
    public static void registerAll() {
        register("cooldown", new CooldownAction());
        register("damage", new DamageAction());
    }
    
    private static void register(String name, IActionFactory<Tuple<Level, SlotAccess>> factory) {
        Services.ACTION.registerItem(name, factory);
    }
    
}
