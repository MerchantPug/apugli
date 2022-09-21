package net.merchantpug.apugli.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Item.class)
public interface ItemAccessor {
    @Invoker
    static BlockHitResult callRaycast(World world, PlayerEntity playerEntity, RaycastContext.FluidHandling fluidHandling) {
        throw new IllegalStateException();
    }
}
