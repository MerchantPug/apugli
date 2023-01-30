package net.merchantpug.apugli.mixin;

import io.github.apace100.apoli.component.PowerHolderComponent;
import net.merchantpug.apugli.power.HoverPower;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {
    @Shadow public ServerPlayerEntity player;

    @ModifyVariable(method = "onPlayerMove", at = @At(value = "FIELD", target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;floating:Z"))
    private boolean doNotKickIfUsingHoverPower(boolean value) {
        if (PowerHolderComponent.hasPower(this.player, HoverPower.class)) {
            return false;
        }
        return value;
    }
}
