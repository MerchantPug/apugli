package net.merchantpug.apugli.mixin.xplatform.common;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.merchantpug.apugli.network.s2c.UpdateUrlTexturesPacket;
import net.merchantpug.apugli.platform.Services;
import net.merchantpug.apugli.registry.power.ApugliPowers;
import net.merchantpug.apugli.util.TextureUtil;
import net.minecraft.network.protocol.game.ServerboundResourcePackPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerGamePacketListenerImpl.class)
public class ServerPlayNetworkHandlerMixin {
    @Shadow public ServerPlayer player;

    @Shadow @Final private MinecraftServer server;

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

    @ModifyVariable(method = "handleMoveVehicle", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/server/level/ServerLevel;noCollision(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/AABB;)Z", ordinal = 0))
    private boolean apugli$cancelVehicleMovedWronglyCheck(boolean original) {
        if (Services.POWER.hasPower(this.player, ApugliPowers.PREVENT_MOVEMENT_CHECKS.get())) {
            return false;
        }
        return original;
    }

    @Inject(method = "handleResourcePackResponse", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/protocol/game/ServerboundResourcePackPacket;getAction()Lnet/minecraft/network/protocol/game/ServerboundResourcePackPacket$Action;"))
    private void apugli$sendUrlTexturesToPlayerAfterResourceLoad(ServerboundResourcePackPacket packet, CallbackInfo ci) {
        if (packet.getAction() == ServerboundResourcePackPacket.Action.DECLINED && this.server.isResourcePackRequired()) return;
        Services.PLATFORM.sendS2C(new UpdateUrlTexturesPacket(TextureUtil.getCache()), player);
    }

}
