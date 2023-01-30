package net.merchantpug.apugli.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import io.github.apace100.apoli.component.PowerHolderComponent;
import net.merchantpug.apugli.power.HoverPower;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {
    @Shadow public ServerPlayerEntity player;

    @ModifyExpressionValue(method = "tick", at = @At(value = "FIELD", target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;floating:Z", ordinal = 0))
    private boolean doNotKickIfUsingHoverPower(boolean original) {
        return original && !PowerHolderComponent.hasPower(this.player, HoverPower.class);
    }

    @ModifyExpressionValue(method = "tick", at = @At(value = "FIELD", target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;vehicleFloating:Z", ordinal = 1))
    private boolean doNotKickIfVehicleUsingHoverPower(boolean original) {
        return original && !PowerHolderComponent.hasPower(this.player.getRootVehicle(), HoverPower.class);
    }
}
