package net.merchantpug.apugli.mixin.xplatform.common;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import io.github.apace100.apoli.component.PowerHolderComponent;
import net.merchantpug.apugli.networking.s2c.UpdateUrlTexturesPacket;
import net.merchantpug.apugli.power.HoverPower;
import net.merchantpug.apugli.util.TextureUtil;
import net.minecraft.network.protocol.game.ServerboundResourcePackPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerGamePacketListenerImpl.class)
public class ServerPlayNetworkHandlerMixin {
    @Shadow public ServerPlayer player;

    @Shadow @Final private MinecraftServer server;

    @ModifyExpressionValue(method = "tick", at = @At(value = "FIELD", target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;clientIsFloating:Z", ordinal = 0))
    private boolean doNotKickIfUsingHoverPower(boolean original) {
        return original && !PowerHolderComponent.hasPower(this.player, HoverPower.class);
    }

    @ModifyExpressionValue(method = "tick", at = @At(value = "FIELD", target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;clientVehicleIsFloating:Z", ordinal = 1))
    private boolean doNotKickIfVehicleUsingHoverPower(boolean original) {
        return original && !PowerHolderComponent.hasPower(this.player.getRootVehicle(), HoverPower.class);
    }

    @Inject(method = "handleResourcePackResponse", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/protocol/game/ServerboundResourcePackPacket;getAction()Lnet/minecraft/network/protocol/game/ServerboundResourcePackPacket$Action;"))
    private void sendUrlTexturesToPlayerAfterResourceLoad(ServerboundResourcePackPacket packet, CallbackInfo ci) {
        if (packet.getAction() == ServerboundResourcePackPacket.Action.DECLINED && this.server.isResourcePackRequired()) return;
        ApugliPackets.sendS2C(new UpdateUrlTexturesPacket(TextureUtil.getTexturePowers()), player);
    }
}
