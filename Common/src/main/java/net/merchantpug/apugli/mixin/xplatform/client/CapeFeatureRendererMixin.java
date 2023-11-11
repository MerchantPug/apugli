package net.merchantpug.apugli.mixin.xplatform.client;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.apace100.apoli.component.PowerHolderComponent;
import net.merchantpug.apugli.platform.Services;
import net.merchantpug.apugli.registry.power.ApugliPowers;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.CapeLayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CapeLayer.class)
public class CapeFeatureRendererMixin {
    @Inject(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/player/AbstractClientPlayer;FFFFFF)V", at = @At(value = "HEAD"), cancellable = true)
    private void apugli$preventCapeRendering(PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int i, AbstractClientPlayer abstractClientPlayerEntity, float f, float g, float h, float j, float k, float l, CallbackInfo ci) {
        if(Services.POWER.getPowers(abstractClientPlayerEntity, ApugliPowers.MODIFY_EQUIPPED_ITEM_RENDER.get()).stream().anyMatch(power -> power.getStack().getItem() == Items.ELYTRA && power.getSlot() == EquipmentSlot.CHEST)) {
            ci.cancel();
        }
    }
}
