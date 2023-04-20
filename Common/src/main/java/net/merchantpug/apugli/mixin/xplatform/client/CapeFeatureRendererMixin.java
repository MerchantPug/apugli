package net.merchantpug.apugli.mixin.xplatform.client;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.apace100.apoli.component.PowerHolderComponent;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.CapeLayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import the.great.migration.merchantpug.apugli.power.ModifyEquippedItemRenderPower;

@Mixin(CapeLayer.class)
public class CapeFeatureRendererMixin {
    @Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/client/network/AbstractClientPlayerEntity;FFFFFF)V", at = @At(value = "HEAD"), cancellable = true)
    private void preventCapeRendering(PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int i, AbstractClientPlayer abstractClientPlayerEntity, float f, float g, float h, float j, float k, float l, CallbackInfo ci) {
        if(PowerHolderComponent.getPowers(abstractClientPlayerEntity, ModifyEquippedItemRenderPower.class).stream().anyMatch(power -> power.stack.getItem() == Items.ELYTRA && power.slot == EquipmentSlot.CHEST)) {
            ci.cancel();
        }
    }
}
