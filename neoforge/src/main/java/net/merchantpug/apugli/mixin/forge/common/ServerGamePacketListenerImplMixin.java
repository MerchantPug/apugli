package net.merchantpug.apugli.mixin.forge.common;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.merchantpug.apugli.platform.Services;
import net.merchantpug.apugli.registry.power.ApugliPowers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerGamePacketListenerImpl.class)
public class ServerGamePacketListenerImplMixin {
    @Shadow public ServerPlayer player;

    @ModifyExpressionValue(method = "tick", at = @At(value = "FIELD", target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;clientVehicleIsFloating:Z", ordinal = 0))
    private boolean apugli$doNotKickIfVehicleUsingHoverPower(boolean original) {
        if (!(this.player.getRootVehicle() instanceof LivingEntity livingVehicle)) {
            return original;
        }
        return original && !Services.POWER.hasPower(livingVehicle, ApugliPowers.HOVER.get());
    }

}
