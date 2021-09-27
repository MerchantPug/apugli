package io.github.merchantpug.apugli.mixin;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.merchantpug.apugli.power.ModifyEquippedItemRenderPower;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Environment(EnvType.CLIENT)
@Mixin(HeldItemRenderer.class)
public abstract class HeldItemRendererMixin {
    @Shadow protected abstract void renderFirstPersonItem(AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand hand, float swingProgress, ItemStack item, float equipProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light);

    @Shadow private ItemStack mainHand;

    @Shadow private ItemStack offHand;

    @Redirect(method = "renderItem(FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;Lnet/minecraft/client/network/ClientPlayerEntity;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/HeldItemRenderer;renderFirstPersonItem(Lnet/minecraft/client/network/AbstractClientPlayerEntity;FFLnet/minecraft/util/Hand;FLnet/minecraft/item/ItemStack;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", ordinal = 0))
    private void renderModifiedItemFirstPersonMainhand(HeldItemRenderer instance, AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand hand, float swingProgress, ItemStack item, float equipProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        List<ModifyEquippedItemRenderPower> modifyEquippedItemRenderPowers = PowerHolderComponent.getPowers(player, ModifyEquippedItemRenderPower.class);
        if (modifyEquippedItemRenderPowers.stream().anyMatch(power -> power.slot == EquipmentSlot.MAINHAND)) {
            modifyEquippedItemRenderPowers.forEach(power -> {
                if (power.slot == EquipmentSlot.MAINHAND && this.mainHand.isEmpty() || power.slot == EquipmentSlot.MAINHAND && !power.shouldRenderEquipped()) {
                    this.renderFirstPersonItem(player, tickDelta, pitch, Hand.MAIN_HAND, swingProgress, power.stack, equipProgress, matrices, vertexConsumers, light);
                }
            });
            if (!this.mainHand.isEmpty() && modifyEquippedItemRenderPowers.stream().noneMatch(power -> power.slot == EquipmentSlot.MAINHAND && !power.shouldRenderEquipped())) {
                this.renderFirstPersonItem(player, tickDelta, pitch, Hand.MAIN_HAND, swingProgress, this.mainHand, equipProgress, matrices, vertexConsumers, light);
            }
        } else {
            this.renderFirstPersonItem(player, tickDelta, pitch, Hand.MAIN_HAND, swingProgress, this.mainHand, equipProgress, matrices, vertexConsumers, light);
        }
    }

    @Redirect(method = "renderItem(FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;Lnet/minecraft/client/network/ClientPlayerEntity;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/HeldItemRenderer;renderFirstPersonItem(Lnet/minecraft/client/network/AbstractClientPlayerEntity;FFLnet/minecraft/util/Hand;FLnet/minecraft/item/ItemStack;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", ordinal = 1))
    private void renderModifiedItemFirstPersonOffhand(HeldItemRenderer instance, AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand hand, float swingProgress, ItemStack item, float equipProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        List<ModifyEquippedItemRenderPower> modifyEquippedItemRenderPowers = PowerHolderComponent.getPowers(player, ModifyEquippedItemRenderPower.class);
        if (modifyEquippedItemRenderPowers.stream().anyMatch(power -> power.slot == EquipmentSlot.OFFHAND)) {
            modifyEquippedItemRenderPowers.forEach(power -> {
                if (power.slot == EquipmentSlot.OFFHAND && this.offHand.isEmpty() || power.slot == EquipmentSlot.OFFHAND && !power.shouldRenderEquipped()) {
                    this.renderFirstPersonItem(player, tickDelta, pitch, Hand.OFF_HAND, swingProgress, power.stack, equipProgress, matrices, vertexConsumers, light);
                }
            });
            if (!this.offHand.isEmpty() && modifyEquippedItemRenderPowers.stream().noneMatch(power -> power.slot == EquipmentSlot.OFFHAND && !power.shouldRenderEquipped())) {
                this.renderFirstPersonItem(player, tickDelta, pitch, Hand.OFF_HAND, swingProgress, this.offHand, equipProgress, matrices, vertexConsumers, light);
            }
        } else {
                this.renderFirstPersonItem(player, tickDelta, pitch, Hand.OFF_HAND, swingProgress, this.offHand, equipProgress, matrices, vertexConsumers, light);
        }
    }
}
