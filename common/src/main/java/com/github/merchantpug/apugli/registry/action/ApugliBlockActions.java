package com.github.merchantpug.apugli.registry.action;

import com.github.merchantpug.apugli.action.block.BonemealAction;
import com.github.merchantpug.apugli.action.block.DestroyAction;
import dev.architectury.injectables.annotations.ExpectPlatform;
import io.github.apace100.origins.power.factory.action.ActionFactory;
import com.github.merchantpug.apugli.action.block.LightUpAction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Triple;

public class ApugliBlockActions {
    public static void register() {
        register(BonemealAction.getFactory());
        register(DestroyAction.getFactory());
        register(LightUpAction.getFactory());
    }

    @ExpectPlatform
    private static void register(ActionFactory<Triple<World, BlockPos, Direction>> actionFactory) {
        throw new AssertionError();
    }
}
