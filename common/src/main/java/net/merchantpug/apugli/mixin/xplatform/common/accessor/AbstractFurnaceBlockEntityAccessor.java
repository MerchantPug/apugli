package net.merchantpug.apugli.mixin.xplatform.common.accessor;

import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractFurnaceBlockEntity.class)
public interface AbstractFurnaceBlockEntityAccessor {

    @Accessor("litTime")
    int apugli$getLitTime();

    @Accessor("litTime")
    void apugli$setLitTime(int value);

    @Accessor("litDuration")
    void apugli$setLitDuration(int value);

}
