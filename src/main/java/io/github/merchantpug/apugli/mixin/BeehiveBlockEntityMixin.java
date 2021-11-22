package io.github.merchantpug.apugli.mixin;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.merchantpug.apugli.power.PreventBeeAngerPower;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BeehiveBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BeehiveBlockEntity.class)
public class BeehiveBlockEntityMixin {
    @Inject(method = "angerBees", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/entity/BeehiveBlockEntity;tryReleaseBee(Lnet/minecraft/block/BlockState;Lnet/minecraft/block/entity/BeehiveBlockEntity$BeeState;)Ljava/util/List;", shift = At.Shift.AFTER), cancellable = true)
    private void dontAngerBees(PlayerEntity player, BlockState state, BeehiveBlockEntity.BeeState beeState, CallbackInfo ci) {
        if (PowerHolderComponent.hasPower(player, PreventBeeAngerPower.class)) {
            ci.cancel();
        }
    }
}
