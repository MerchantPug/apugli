<<<<<<<< HEAD:src/main/java/net/merchantpug/apugli/mixin/client/HeldItemRendererMixin.java
package net.merchantpug.apugli.mixin.client;

import net.merchantpug.apugli.power.ModifyEquippedItemRenderPower;
========
package com.github.merchantpug.apugli.mixin.xplatforn.client;

import the.great.migration.merchantpug.apugli.power.ModifyEquippedItemRenderPower;
import com.mojang.blaze3d.vertex.PoseStack;
>>>>>>>> pr/25:Common/src/main/java/com/github/merchantpug/apugli/mixin/xplatforn/client/HeldItemRendererMixin.java
import io.github.apace100.apoli.component.PowerHolderComponent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;
import java.util.stream.Collectors;

@Environment(EnvType.CLIENT)
@Mixin(ItemInHandRenderer.class)
public abstract class HeldItemRendererMixin {
    @Shadow private ItemStack mainHand;

    @Shadow private ItemStack offHand;

    @Shadow protected abstract void renderFirstPersonItem(AbstractClientPlayer player, float tickDelta, float pitch, InteractionHand hand, float swingProgress, ItemStack item, float equipProgress, PoseStack matrices, MultiBufferSource vertexConsumers, int light);

    @Redirect(method = "renderItem(FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;Lnet/minecraft/client/network/ClientPlayerEntity;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/HeldItemRenderer;renderFirstPersonItem(Lnet/minecraft/client/network/AbstractClientPlayerEntity;FFLnet/minecraft/util/Hand;FLnet/minecraft/item/ItemStack;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", ordinal = 0))
    private void renderModifiedItemFirstPersonMainhand(ItemInHandRenderer instance, AbstractClientPlayer player, float tickDelta, float pitch, InteractionHand hand, float swingProgress, ItemStack item, float equipProgress, PoseStack matrices, MultiBufferSource vertexConsumers, int light) {
        List<ModifyEquippedItemRenderPower> modifyEquippedItemRenderPowers = PowerHolderComponent.getPowers(player, ModifyEquippedItemRenderPower.class).stream().filter(power -> power.slot == EquipmentSlot.MAINHAND).collect(Collectors.toList());
        if(modifyEquippedItemRenderPowers.size() == 0) {
            this.renderFirstPersonItem(player, tickDelta, pitch, hand, swingProgress, item, equipProgress, matrices, vertexConsumers, light);
        } else {
            modifyEquippedItemRenderPowers.forEach(power -> {
                if(this.mainHand.isEmpty() && !power.shouldOverride() && !power.stack.isEmpty() || power.shouldOverride()) {
                    this.renderFirstPersonItem(player, tickDelta, pitch, InteractionHand.MAIN_HAND, swingProgress, power.stack, equipProgress, matrices, vertexConsumers, light);
                }
            });
            if(modifyEquippedItemRenderPowers.stream().allMatch(power -> power.stack.isEmpty() && !power.shouldOverride()) && this.mainHand.isEmpty()) {
                this.renderFirstPersonItem(player, tickDelta, pitch, InteractionHand.MAIN_HAND, swingProgress, ItemStack.EMPTY, equipProgress, matrices, vertexConsumers, light);
            }
            if(modifyEquippedItemRenderPowers.stream().noneMatch(ModifyEquippedItemRenderPower::shouldOverride) && !this.mainHand.isEmpty()) {
                this.renderFirstPersonItem(player, tickDelta, pitch, hand, swingProgress, item, equipProgress, matrices, vertexConsumers, light);
            }
        }
    }

    @Redirect(method = "renderItem(FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;Lnet/minecraft/client/network/ClientPlayerEntity;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/HeldItemRenderer;renderFirstPersonItem(Lnet/minecraft/client/network/AbstractClientPlayerEntity;FFLnet/minecraft/util/Hand;FLnet/minecraft/item/ItemStack;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", ordinal = 1))
    private void renderModifiedItemFirstPersonOffhand(ItemInHandRenderer instance, AbstractClientPlayer player, float tickDelta, float pitch, InteractionHand hand, float swingProgress, ItemStack item, float equipProgress, PoseStack matrices, MultiBufferSource vertexConsumers, int light) {
        List<ModifyEquippedItemRenderPower> modifyEquippedItemRenderPowers = PowerHolderComponent.getPowers(player, ModifyEquippedItemRenderPower.class).stream().filter(power -> power.slot == EquipmentSlot.OFFHAND).collect(Collectors.toList());
        if(modifyEquippedItemRenderPowers.size() == 0) {
            this.renderFirstPersonItem(player, tickDelta, pitch, hand, swingProgress, item, equipProgress, matrices, vertexConsumers, light);
        } else {
            modifyEquippedItemRenderPowers.forEach(power -> {
                if((this.offHand.isEmpty() && !power.shouldOverride() || power.shouldOverride()) && !power.stack.isEmpty()) {
                    this.renderFirstPersonItem(player, tickDelta, pitch, InteractionHand.OFF_HAND, swingProgress, power.stack, equipProgress, matrices, vertexConsumers, light);
                }
            });
            if(modifyEquippedItemRenderPowers.stream().noneMatch(ModifyEquippedItemRenderPower::shouldOverride) && !this.offHand.isEmpty()) {
                this.renderFirstPersonItem(player, tickDelta, pitch, hand, swingProgress, item, equipProgress, matrices, vertexConsumers, light);
            }
        }
    }
}
