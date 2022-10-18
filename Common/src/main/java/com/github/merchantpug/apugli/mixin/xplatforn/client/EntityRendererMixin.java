package com.github.merchantpug.apugli.mixin.xplatforn.client;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.apace100.apoli.component.PowerHolderComponent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
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
import the.great.migration.merchantpug.apugli.power.PreventLabelRenderPower;

@Environment(EnvType.CLIENT)
@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin<T extends Entity> {
    @Inject(method = "renderLabelIfPresent", at = @At("HEAD"), cancellable = true)
    private void cancelLabelRender(T entity, Component text, PoseStack matrices, MultiBufferSource vertexConsumers, int light, CallbackInfo ci) {
        if(!(entity instanceof Player)) return;

        Player localPlayer = Minecraft.getInstance().player;
        if(PowerHolderComponent.getPowers(entity, PreventLabelRenderPower.class).stream().anyMatch(power -> power.shouldHide(localPlayer))) {
            ci.cancel();
        }
    }
}