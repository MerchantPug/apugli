package io.github.merchantpug.apugli.mixin;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.merchantpug.apugli.power.ModifyEquippedItemRenderPower;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Environment(EnvType.CLIENT)
@Mixin(HeldItemRenderer.class)
public abstract class HeldItemRendererMixin {
    @Shadow private ItemStack mainHand;

    @Shadow private ItemStack offHand;

    @Shadow protected abstract void renderFirstPersonItem(AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand hand, float swingProgress, ItemStack item, float equipProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light);

    @Shadow private float prevEquipProgressMainHand;

    @Shadow private float equipProgressMainHand;

    @Inject(method = "renderItem(FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;Lnet/minecraft/client/network/ClientPlayerEntity;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/HeldItemRenderer;renderFirstPersonItem(Lnet/minecraft/client/network/AbstractClientPlayerEntity;FFLnet/minecraft/util/Hand;FLnet/minecraft/item/ItemStack;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", ordinal = 0), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    private void renderModifiedItemFirstPersonMainhand(float tickDelta, MatrixStack matrices, VertexConsumerProvider.Immediate vertexConsumers, ClientPlayerEntity player, int light, CallbackInfo ci, float f, Hand hand, float g, HeldItemRenderer.HandRenderType handRenderType, float j, float k) {
        List<ModifyEquippedItemRenderPower> modifyEquippedItemRenderPowers = PowerHolderComponent.getPowers(player, ModifyEquippedItemRenderPower.class);
        if (modifyEquippedItemRenderPowers.stream().anyMatch(power -> power.slot == EquipmentSlot.MAINHAND)) {
            modifyEquippedItemRenderPowers.forEach(power -> {
                float handSwingProgress = hand == Hand.MAIN_HAND ? f : 0.0F;
                float equipProgress = 1.0F - MathHelper.lerp(tickDelta, this.prevEquipProgressMainHand, this.equipProgressMainHand);
                if (power.slot == EquipmentSlot.MAINHAND && this.mainHand.isEmpty() && !power.shouldOverride() || power.slot == EquipmentSlot.MAINHAND && power.shouldOverride()) this.renderFirstPersonItem(player, tickDelta, g, Hand.MAIN_HAND, handSwingProgress, power.stack, equipProgress, matrices, vertexConsumers, light);
            });
            if (modifyEquippedItemRenderPowers.stream().anyMatch(ModifyEquippedItemRenderPower::shouldOverride) || this.mainHand.isEmpty() && !modifyEquippedItemRenderPowers.isEmpty()) {
                vertexConsumers.draw();
                ci.cancel();
            }
        }
    }

    @Inject(method = "renderItem(FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;Lnet/minecraft/client/network/ClientPlayerEntity;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/HeldItemRenderer;renderFirstPersonItem(Lnet/minecraft/client/network/AbstractClientPlayerEntity;FFLnet/minecraft/util/Hand;FLnet/minecraft/item/ItemStack;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", ordinal = 1), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    private void renderModifiedItemFirstPersonOffhand(float tickDelta, MatrixStack matrices, VertexConsumerProvider.Immediate vertexConsumers, ClientPlayerEntity player, int light, CallbackInfo ci, float f, Hand hand, float g, float l, float m) {
        List<ModifyEquippedItemRenderPower> modifyEquippedItemRenderPowers = PowerHolderComponent.getPowers(player, ModifyEquippedItemRenderPower.class);
        if (modifyEquippedItemRenderPowers.stream().anyMatch(power -> power.slot == EquipmentSlot.OFFHAND)) {
            modifyEquippedItemRenderPowers.forEach(power -> {
                if (power.slot == EquipmentSlot.OFFHAND && this.offHand.isEmpty() && !power.shouldOverride() || power.slot == EquipmentSlot.OFFHAND && power.shouldOverride()) this.renderFirstPersonItem(player, tickDelta, g, Hand.OFF_HAND, l, power.stack, m, matrices, vertexConsumers, light);
            });
            if (modifyEquippedItemRenderPowers.stream().anyMatch(ModifyEquippedItemRenderPower::shouldOverride) || this.offHand.isEmpty() && !modifyEquippedItemRenderPowers.isEmpty()) {
                vertexConsumers.draw();
                ci.cancel();
            }
        }
    }
}
