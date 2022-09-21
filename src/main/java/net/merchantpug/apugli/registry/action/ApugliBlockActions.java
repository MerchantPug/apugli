package net.merchantpug.apugli.registry.action;

import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.apoli.registry.ApoliRegistries;
import net.merchantpug.apugli.action.block.DestroyAction;
import net.merchantpug.apugli.action.block.LightUpAction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Triple;

public class ApugliBlockActions {
    public static void register() {
        register(DestroyAction.getFactory());
        register(LightUpAction.getFactory());
    }

    private static void register(ActionFactory<Triple<World, BlockPos, Direction>> actionFactory) {
        Registry.register(ApoliRegistries.BLOCK_ACTION, actionFactory.getSerializerId(), actionFactory);
    }
}
