package net.merchantpug.apugli.mixin.xplatform.common;

import net.merchantpug.apugli.platform.Services;
import net.merchantpug.apugli.registry.power.ApugliPowers;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BeehiveBlockEntity.class)
public class BeehiveBlockEntityMixin {

    @Inject(method = "emptyAllLivingFromHive", at = @At(value = "HEAD", shift = At.Shift.AFTER), cancellable = true)
    private void apugli$dontAngerBees(Player player, BlockState state, BeehiveBlockEntity.BeeReleaseStatus beeState, CallbackInfo ci) {
        if(Services.POWER.hasPower(player, ApugliPowers.PREVENT_BEE_ANGER.get())) {
            ci.cancel();
        }
    }

}
