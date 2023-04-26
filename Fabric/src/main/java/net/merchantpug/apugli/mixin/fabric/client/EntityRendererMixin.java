package net.merchantpug.apugli.mixin.fabric.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.merchantpug.apugli.platform.Services;
import net.merchantpug.apugli.registry.power.ApugliPowers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin<T extends Entity> {

    @Inject(method = "renderNameTag", at = @At("HEAD"), cancellable = true)
    private void cancelLabelRender(T entity, Component text, PoseStack matrices, MultiBufferSource vertexConsumers, int light, CallbackInfo ci) {
        if(!(entity instanceof Player player)) return;

        Player localPlayer = Minecraft.getInstance().player;
        if(Services.POWER.getPowers(player, ApugliPowers.PREVENT_LABEL_RENDER.get()).stream().anyMatch(power -> power.shouldHide(localPlayer))) {
            ci.cancel();
        }
    }

}
