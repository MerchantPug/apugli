package io.github.merchantpug.apugli.registry.action.forge;

import io.github.apace100.origins.power.factory.action.ActionFactory;
import io.github.apace100.origins.registry.ModRegistriesArchitectury;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Triple;

public class ApugliBlockActionsImpl {
    public static void register(ActionFactory<Triple<World, BlockPos, Direction>> actionFactory) {
        ModRegistriesArchitectury.BLOCK_ACTION.register(actionFactory.getSerializerId(), () -> actionFactory);
    }
}
