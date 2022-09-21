package net.merchantpug.apugli.mixin;

import net.merchantpug.apugli.power.PreventBeeAngerPower;
import io.github.apace100.apoli.component.PowerHolderComponent;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BeehiveBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BeehiveBlockEntity.class)
public class BeehiveBlockEntityMixin {
    @Inject(method = "angerBees", at = @At(value = "HEAD"), cancellable = true)
    private void dontAngerBees(PlayerEntity player, BlockState state, BeehiveBlockEntity.BeeState beeState, CallbackInfo ci) {
        if (PowerHolderComponent.hasPower(player, PreventBeeAngerPower.class)) {
            ci.cancel();
        }
    }
}
