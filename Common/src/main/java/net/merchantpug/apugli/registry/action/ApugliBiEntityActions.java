package net.merchantpug.apugli.registry.action;

import net.merchantpug.apugli.action.factory.IActionFactory;
import net.merchantpug.apugli.action.factory.bientity.ChangeHitsOnTargetAction;
import net.merchantpug.apugli.platform.Services;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;

public class ApugliBiEntityActions {
    
    public static void registerAll() {
        register("change_hits_on_target", new ChangeHitsOnTargetAction());
    }
    
    private static void register(String name, IActionFactory<Tuple<Entity, Entity>> factory) {
        Services.ACTION.registerBiEntity(name, factory);
    }
    
}
