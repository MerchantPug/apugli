package net.merchantpug.apugli.mixin.xplatform.common;

import net.merchantpug.apugli.platform.Services;
import net.merchantpug.apugli.registry.power.ApugliPowers;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(BeehiveBlock.class)
public class BeehiveBlockMixin {

    @Inject(method = "angerNearbyBees", at = @At(value = "INVOKE", target = "Ljava/util/List;isEmpty()Z", ordinal = 1), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    private void apugli$dontAngerBees(Level level, BlockPos pos, CallbackInfo ci, AABB aABB, List<Bee> list, List<Player> list2) {
        if (!list2.isEmpty() && list2.stream().anyMatch(player -> Services.POWER.hasPower(player, ApugliPowers.PREVENT_BEE_ANGER.get()))) {
            ci.cancel();
        }
    }

}
