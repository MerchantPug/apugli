package net.merchantpug.apugli.mixin.xplatform.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.merchantpug.apugli.platform.Services;
import net.merchantpug.apugli.power.ModifyEquippedItemRenderPower;
import net.merchantpug.apugli.registry.power.ApugliPowers;
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

@Mixin(ItemInHandRenderer.class)
public abstract class HeldItemRendererMixin {
    @Shadow private ItemStack mainHandItem;

    @Shadow private ItemStack offHandItem;

    @Shadow protected abstract void renderArmWithItem(AbstractClientPlayer player, float tickDelta, float pitch, InteractionHand hand, float swingProgress, ItemStack item, float equipProgress, PoseStack matrices, MultiBufferSource vertexConsumers, int light);

    @Redirect(method = "renderHandsWithItems", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/ItemInHandRenderer;renderArmWithItem(Lnet/minecraft/client/player/AbstractClientPlayer;FFLnet/minecraft/world/InteractionHand;FLnet/minecraft/world/item/ItemStack;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", ordinal = 0))
    private void apugli$renderModifiedItemFirstPersonMainhand(ItemInHandRenderer instance, AbstractClientPlayer player, float tickDelta, float pitch, InteractionHand hand, float swingProgress, ItemStack item, float equipProgress, PoseStack matrices, MultiBufferSource vertexConsumers, int light) {
        List<ModifyEquippedItemRenderPower> modifyEquippedItemRenderPowers = Services.POWER.getPowers(player, ApugliPowers.MODIFY_EQUIPPED_ITEM_RENDER.get()).stream().filter(power -> power.getSlot() == EquipmentSlot.MAINHAND).toList();
        if(modifyEquippedItemRenderPowers.size() == 0) {
            this.renderArmWithItem(player, tickDelta, pitch, hand, swingProgress, item, equipProgress, matrices, vertexConsumers, light);
        } else {
            modifyEquippedItemRenderPowers.forEach(power -> {
                if(this.mainHandItem.isEmpty() && !power.shouldOverride() && !power.getStack().isEmpty() || power.shouldOverride()) {
                    this.renderArmWithItem(player, tickDelta, pitch, InteractionHand.MAIN_HAND, swingProgress, power.getStack(), equipProgress, matrices, vertexConsumers, light);
                }
            });
            if(modifyEquippedItemRenderPowers.stream().allMatch(power -> power.getStack().isEmpty() && !power.shouldOverride()) && this.mainHandItem.isEmpty()) {
                this.renderArmWithItem(player, tickDelta, pitch, InteractionHand.MAIN_HAND, swingProgress, ItemStack.EMPTY, equipProgress, matrices, vertexConsumers, light);
            }
            if(modifyEquippedItemRenderPowers.stream().noneMatch(ModifyEquippedItemRenderPower::shouldOverride) && !this.mainHandItem.isEmpty()) {
                this.renderArmWithItem(player, tickDelta, pitch, hand, swingProgress, item, equipProgress, matrices, vertexConsumers, light);
            }
        }
    }

    @Redirect(method = "renderHandsWithItems", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/ItemInHandRenderer;renderArmWithItem(Lnet/minecraft/client/player/AbstractClientPlayer;FFLnet/minecraft/world/InteractionHand;FLnet/minecraft/world/item/ItemStack;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", ordinal = 1))
    private void apugli$renderModifiedItemFirstPersonOffhand(ItemInHandRenderer instance, AbstractClientPlayer player, float tickDelta, float pitch, InteractionHand hand, float swingProgress, ItemStack item, float equipProgress, PoseStack matrices, MultiBufferSource vertexConsumers, int light) {
        List<ModifyEquippedItemRenderPower> modifyEquippedItemRenderPowers = Services.POWER.getPowers(player, ApugliPowers.MODIFY_EQUIPPED_ITEM_RENDER.get()).stream().filter(power -> power.getSlot() == EquipmentSlot.OFFHAND).toList();
        if(modifyEquippedItemRenderPowers.isEmpty()) {
            this.renderArmWithItem(player, tickDelta, pitch, hand, swingProgress, item, equipProgress, matrices, vertexConsumers, light);
        } else {
            modifyEquippedItemRenderPowers.forEach(power -> {
                if((this.offHandItem.isEmpty() && !power.shouldOverride() || power.shouldOverride()) && !power.getStack().isEmpty()) {
                    this.renderArmWithItem(player, tickDelta, pitch, InteractionHand.OFF_HAND, swingProgress, power.getStack(), equipProgress, matrices, vertexConsumers, light);
                }
            });
            if(modifyEquippedItemRenderPowers.stream().noneMatch(ModifyEquippedItemRenderPower::shouldOverride) && !this.offHandItem.isEmpty()) {
                this.renderArmWithItem(player, tickDelta, pitch, hand, swingProgress, item, equipProgress, matrices, vertexConsumers, light);
            }
        }
    }
}
