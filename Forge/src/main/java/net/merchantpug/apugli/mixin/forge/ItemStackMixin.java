package net.merchantpug.apugli.mixin.forge;

import net.merchantpug.apugli.access.ItemStackLevelAccess;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemStack.class)
@Implements(@Interface(iface = ItemStackLevelAccess.class, prefix = "apugli$"))
public class ItemStackMixin {
    public Level apugli$level;

    @Inject(method = "inventoryTick", at = @At("HEAD"))
    private void getLevelFromInventory(Level level, Entity entity, int slot, boolean selected, CallbackInfo ci) {
        apugli$level = level;
    }

    public Level apugli$getLevel() {
        return apugli$level;
    }

    public void apugli$setLevel(Level value) {
        apugli$level = value;
    }
}
