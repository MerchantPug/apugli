package net.merchantpug.apugli.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import io.github.apace100.apoli.component.PowerHolderComponent;
import net.merchantpug.apugli.networking.ApugliPackets;
import net.merchantpug.apugli.networking.s2c.UpdateUrlTexturesPacket;
import net.merchantpug.apugli.power.HoverPower;
import net.merchantpug.apugli.util.TextureUtil;
import net.minecraft.network.packet.c2s.play.ResourcePackStatusC2SPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {
    @Shadow public ServerPlayerEntity player;

    @Shadow @Final private MinecraftServer server;

    @ModifyExpressionValue(method = "tick", at = @At(value = "FIELD", target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;floating:Z", ordinal = 0))
    private boolean doNotKickIfUsingHoverPower(boolean original) {
        return original && !PowerHolderComponent.hasPower(this.player, HoverPower.class);
    }

    @ModifyExpressionValue(method = "tick", at = @At(value = "FIELD", target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;vehicleFloating:Z", ordinal = 1))
    private boolean doNotKickIfVehicleUsingHoverPower(boolean original) {
        return original && !PowerHolderComponent.hasPower(this.player.getRootVehicle(), HoverPower.class);
    }

    @Inject(method = "onResourcePackStatus", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/packet/c2s/play/ResourcePackStatusC2SPacket;getStatus()Lnet/minecraft/network/packet/c2s/play/ResourcePackStatusC2SPacket$Status;"))
    private void sendUrlTexturesToPlayerAfterResourceLoad(ResourcePackStatusC2SPacket packet, CallbackInfo ci) {
        if (packet.getStatus() == ResourcePackStatusC2SPacket.Status.DECLINED && this.server.requireResourcePack()) return;
        ApugliPackets.sendS2C(new UpdateUrlTexturesPacket(TextureUtil.getTexturePowers()), player);
    }
}
