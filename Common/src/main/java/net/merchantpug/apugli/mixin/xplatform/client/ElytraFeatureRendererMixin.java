package net.merchantpug.apugli.mixin.xplatform.client;

<<<<<<<< HEAD:src/main/java/net/merchantpug/apugli/mixin/client/ElytraFeatureRendererMixin.java
package net.merchantpug.apugli.mixin.client;

import net.merchantpug.apugli.power.ModifyEquippedItemRenderPower;
========
package com.github.merchantpug.apugli.mixin.xplatforn.client;

>>>>>>>> pr/25:Common/src/main/java/com/github/merchantpug/apugli/mixin/xplatforn/client/ElytraFeatureRendererMixin.java
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.blaze3d.vertex.PoseStack;
import io.github.apace100.apoli.component.PowerHolderComponent;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import the.great.migration.merchantpug.apugli.power.ModifyEquippedItemRenderPower;

@Mixin(ElytraLayer.class)
public class ElytraFeatureRendererMixin<T extends LivingEntity, M extends EntityModel<T>> {
    private LivingEntity apugli$livingEntity;

    @Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/LivingEntity;FFFFFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isOf(Lnet/minecraft/item/Item;)Z"))
    private void captureLivingEntity(PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int i, T livingEntity, float f, float g, float h, float j, float k, float l, CallbackInfo ci) {
        apugli$livingEntity = livingEntity;
    }

    @ModifyExpressionValue(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/LivingEntity;FFFFFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isOf(Lnet/minecraft/item/Item;)Z"))
    private boolean allowPowerRendering(boolean original) {
        return original || PowerHolderComponent.getPowers(apugli$livingEntity, ModifyEquippedItemRenderPower.class).stream().anyMatch(power -> power.stack.getItem() == Items.ELYTRA && power.slot == EquipmentSlot.CHEST);
    }
}
