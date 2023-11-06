package net.merchantpug.apugli.mixin.fabric.integration.pehkui;

import net.merchantpug.apugli.integration.pehkui.PehkuiUtil;
import net.merchantpug.apugli.platform.Services;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerEntity.class)
public class ServerEntityMixin {
    @Shadow @Final private Entity entity;

    @Inject(method = "addPairing", at = @At("TAIL"))
    private void sendScaleRemovalPacketBeforeRemoval(ServerPlayer player, CallbackInfo ci) {
        if (!Services.PLATFORM.isModLoaded("pehkui") || !(this.entity instanceof LivingEntity living)) return;
        // PehkuiUtil.onStartTracking(living, player);
    }
}
