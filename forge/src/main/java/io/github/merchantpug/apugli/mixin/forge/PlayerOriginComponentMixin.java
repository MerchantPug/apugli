package io.github.merchantpug.apugli.mixin.forge;

import io.github.apace100.origins.component.PlayerOriginComponent;
import io.github.apace100.origins.origin.Origin;
import io.github.apace100.origins.origin.OriginLayer;
import io.github.merchantpug.apugli.networking.ApugliPackets;
import io.netty.buffer.Unpooled;
import me.shedaniel.architectury.networking.NetworkManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = PlayerOriginComponent.class, remap = false)
public class PlayerOriginComponentMixin {
    @Shadow private PlayerEntity player;

    @Inject(method = "setOrigin", at = @At("HEAD"))
    private void resetKeysToCheckWhenRemoved(OriginLayer layer, Origin origin, CallbackInfo ci) {
        if (!(player instanceof ServerPlayerEntity)) return;
        NetworkManager.sendToPlayer((ServerPlayerEntity) player, ApugliPackets.REMOVE_KEYS_TO_CHECK, new PacketByteBuf(Unpooled.buffer()));
    }
}
