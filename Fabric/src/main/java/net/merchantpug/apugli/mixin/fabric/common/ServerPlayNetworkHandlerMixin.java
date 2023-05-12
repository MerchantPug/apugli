package net.merchantpug.apugli.mixin.fabric.common;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.merchantpug.apugli.networking.s2c.UpdateUrlTexturesPacket;
import net.merchantpug.apugli.platform.Services;
import net.merchantpug.apugli.registry.power.ApugliPowers;
import net.merchantpug.apugli.util.TextureUtil;
import net.minecraft.network.protocol.game.ServerboundResourcePackPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerGamePacketListenerImpl.class)
public class ServerPlayNetworkHandlerMixin {
    @Shadow public ServerPlayer player;

    @ModifyExpressionValue(method = "tick", at = @At(value = "FIELD", target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;clientVehicleIsFloating:Z", ordinal = 1))
    private boolean doNotKickIfVehicleUsingHoverPower(boolean original) {
        if (!(this.player.getRootVehicle() instanceof LivingEntity livingVehicle)) {
            return original;
        }
        return original && !Services.POWER.hasPower(livingVehicle, ApugliPowers.HOVER.get());
    }

}
