package net.merchantpug.apugli.mixin.xplatform.common.accessor;

import net.minecraft.world.level.block.entity.BrewingStandBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BrewingStandBlockEntity.class)
public interface BrewingStandBlockEntityAccessor {
    
    @Accessor
    int getFuel();

    @Accessor
    void setFuel(int value);
    
}
