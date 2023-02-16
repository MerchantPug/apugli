package net.merchantpug.apugli.mixin;

import net.merchantpug.apugli.networking.ApugliPackets;
import net.merchantpug.apugli.networking.s2c.UpdateUrlTexturesPacket;
import net.merchantpug.apugli.util.TextureUtil;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {
    @Shadow @Final private MinecraftServer server;

    @Inject(method = "onPlayerConnect", at = @At("TAIL"))
    private void handleOnConnectResources(ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci) {
        if (this.server.getResourcePackProperties().isPresent()) return;
        ApugliPackets.sendS2C(new UpdateUrlTexturesPacket(TextureUtil.getTexturePowers()), player);
    }
}
