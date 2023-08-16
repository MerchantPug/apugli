package net.merchantpug.apugli.mixin.fabric.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.blaze3d.vertex.PoseStack;
import net.merchantpug.apugli.platform.Services;
import net.merchantpug.apugli.registry.power.ApugliPowers;
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

@Mixin(ElytraLayer.class)
public class ElytraFeatureRendererMixin<T extends LivingEntity, M extends EntityModel<T>> {
    private LivingEntity apugli$livingEntity;

    @Inject(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/LivingEntity;FFFFFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;is(Lnet/minecraft/world/item/Item;)Z"))
    private void captureLivingEntity(PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int i, T livingEntity, float f, float g, float h, float j, float k, float l, CallbackInfo ci) {
        apugli$livingEntity = livingEntity;
    }

    @ModifyExpressionValue(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/LivingEntity;FFFFFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;is(Lnet/minecraft/world/item/Item;)Z"))
    private boolean allowPowerRendering(boolean original) {
        boolean returnValue = (original || Services.POWER.getPowers(apugli$livingEntity, ApugliPowers.MODIFY_EQUIPPED_ITEM_RENDER.get()).stream().anyMatch(power -> power.getStack().is(Items.ELYTRA) && power.getSlot() == EquipmentSlot.CHEST)) && Services.POWER.getPowers(apugli$livingEntity, ApugliPowers.MODIFY_EQUIPPED_ITEM_RENDER.get()).stream().filter(power -> power.getSlot() == EquipmentSlot.CHEST).noneMatch(power -> power.shouldOverride() && !power.getStack().is(Items.ELYTRA));
        apugli$livingEntity = null;
        return returnValue;
    }
}
