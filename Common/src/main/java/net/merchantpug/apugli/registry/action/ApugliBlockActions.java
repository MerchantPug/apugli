package net.merchantpug.apugli.registry.action;

import net.merchantpug.apugli.action.factory.IActionFactory;
import net.merchantpug.apugli.action.factory.block.AreaOfEffectAction;
import net.merchantpug.apugli.action.factory.block.DestroyAction;
import net.merchantpug.apugli.action.factory.block.LightUpAction;
import net.merchantpug.apugli.platform.Services;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.tuple.Triple;

public class ApugliBlockActions {
    
    public static void registerAll() {
        register("area_of_effect", new AreaOfEffectAction());
        register("destroy", new DestroyAction());
        register("light_up", new LightUpAction());
    }
    
    private static void register(String name, IActionFactory<Triple<Level, BlockPos, Direction>> factory) {
        Services.ACTION.registerBlock(name, factory);
    }
    
}
