package net.merchantpug.apugli.mixin.xplatform.common;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.merchantpug.apugli.platform.Services;
import net.merchantpug.apugli.registry.power.ApugliPowers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(ServerGamePacketListenerImpl.class)
public class ServerPlayNetworkHandlerMixin {
    @Shadow public ServerPlayer player;

    @ModifyExpressionValue(method = "tick", at = @At(value = "FIELD", target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;clientIsFloating:Z", ordinal = 0))
    private boolean apugli$doNotKickIfUsingHoverPower(boolean original) {
        return original && !Services.POWER.hasPower(this.player, ApugliPowers.HOVER.get());
    }

    @ModifyExpressionValue(method = "handleMovePlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;isChangingDimension()Z", ordinal = 0))
    private boolean apugli$cancelMovedTooQuicklyCheck(boolean original) {
        return original || Services.POWER.hasPower(this.player, ApugliPowers.PREVENT_MOVEMENT_CHECKS.get());
    }

    @ModifyExpressionValue(method = "handleMovePlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;isChangingDimension()Z", ordinal = 1))
    private boolean apugli$cancelMovedWronglyCheck(boolean original) {
        return original || Services.POWER.hasPower(this.player, ApugliPowers.PREVENT_MOVEMENT_CHECKS.get());
    }

    @ModifyExpressionValue(method = "handleMoveVehicle", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;isSingleplayerOwner()Z"))
    private boolean apugli$cancelVehicleMovedTooQuicklyCheck(boolean original) {
        return original || Services.POWER.hasPower(this.player, ApugliPowers.PREVENT_MOVEMENT_CHECKS.get());
    }

    @ModifyConstant(method = "handleMoveVehicle", constant = @Constant(doubleValue = 0.0625))
    private double apugli$cancelVehicleMovedWronglyCheck(double original) {
        if (Services.POWER.hasPower(this.player, ApugliPowers.PREVENT_MOVEMENT_CHECKS.get())) {
            return Double.MAX_VALUE;
        }
        return original;
    }

}
