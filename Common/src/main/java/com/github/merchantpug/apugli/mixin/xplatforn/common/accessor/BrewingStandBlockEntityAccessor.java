<<<<<<<< HEAD:src/main/java/net/merchantpug/apugli/mixin/BrewingStandBlockEntityAccessor.java
package net.merchantpug.apugli.mixin;
========
package com.github.merchantpug.apugli.mixin.xplatforn.common.accessor;
>>>>>>>> pr/25:Common/src/main/java/com/github/merchantpug/apugli/mixin/xplatforn/common/accessor/BrewingStandBlockEntityAccessor.java

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
