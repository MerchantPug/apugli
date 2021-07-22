package io.github.merchantpug.apugli.mixin;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.merchantpug.apugli.Apugli;
import io.github.merchantpug.apugli.power.ItemAttributePower;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerInventory.class)
public class PlayerInventoryMixin {
    @Shadow @Final public PlayerEntity player;

    @Inject(method = "setStack", at = @At("TAIL"))
    private void setStack(int slot, ItemStack stack, CallbackInfo ci) {
         PowerHolderComponent.getPowers(player, ItemAttributePower.class).forEach(power -> {
            if (power.predicate.test(stack)) {
                power.removeModifiersFromItem(stack);
                power.addModifiersToItem(stack);
            }
        });
        Apugli.LOGGER.info("Cool");
    }
}
