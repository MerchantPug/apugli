package net.merchantpug.apugli.mixin.xplatform.common;

import io.github.apace100.apoli.component.PowerHolderComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import the.great.migration.merchantpug.apugli.power.PreventBeeAngerPower;

@Mixin(BeehiveBlockEntity.class)
public class BeehiveBlockEntityMixin {
    @Inject(method = "angerBees", at = @At(value = "HEAD"), cancellable = true)
    private void dontAngerBees(Player player, BlockState state, BeehiveBlockEntity.BeeReleaseStatus beeState, CallbackInfo ci) {
        if(PowerHolderComponent.hasPower(player, PreventBeeAngerPower.class)) {
            ci.cancel();
        }
    }
}
