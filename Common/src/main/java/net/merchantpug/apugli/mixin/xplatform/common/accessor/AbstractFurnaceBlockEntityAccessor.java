package net.merchantpug.apugli.mixin.xplatform.common.accessor;

import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractFurnaceBlockEntity.class)
public interface AbstractFurnaceBlockEntityAccessor {
    
    @Accessor
    int getCookingProgress();
    
    @Accessor
    void setCookingProgress(int value);
    
    @Accessor
    void setLitTime(int value);

}
