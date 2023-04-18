<<<<<<<< HEAD:src/main/java/net/merchantpug/apugli/mixin/BucketItemAccessor.java
package net.merchantpug.apugli.mixin;
========
package com.github.merchantpug.apugli.mixin.xplatforn.common.accessor;
>>>>>>>> pr/25:Common/src/main/java/com/github/merchantpug/apugli/mixin/xplatforn/common/accessor/BucketItemAccessor.java

import net.minecraft.world.item.BucketItem;
import net.minecraft.world.level.material.Fluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BucketItem.class)
public interface BucketItemAccessor {
    @Accessor
    Fluid getFluid();
}
