package net.merchantpug.apugli.mixin.xplatform.common.accessor;

import net.minecraft.world.level.block.entity.BrewingStandBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BrewingStandBlockEntity.class)
public interface BrewingStandBlockEntityAccessor {
    
    @Accessor("fuel")
    int apugli$getFuel();

    @Accessor("fuel")
    void apugli$setFuel(int value);
    
}
