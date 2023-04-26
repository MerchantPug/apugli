package net.merchantpug.apugli.mixin.xplatform.client;

import net.merchantpug.apugli.platform.Services;
import net.merchantpug.apugli.power.ModifyEquippedItemRenderPower;
import net.merchantpug.apugli.registry.power.ApugliPowers;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(ItemInHandLayer.class)
public abstract class HeldItemFeatureRendererMixin<T extends LivingEntity, M extends EntityModel<T> & ArmedModel> extends RenderLayer<T, M> {
    @Unique boolean isRightMainHand;

    public HeldItemFeatureRendererMixin(RenderLayerParent<T, M> context) {
        super(context);
    }

    @Inject(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/LivingEntity;FFFFFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/layers/ItemInHandLayer;getParentModel()Lnet/minecraft/client/model/EntityModel;"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void captureBoolean(PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int i, T livingEntity, float f, float g, float h, float j, float k, float l, CallbackInfo ci, boolean bl) {
        this.isRightMainHand = bl;
    }

    @Inject(method = "renderArmWithItem", at = @At("HEAD"), cancellable = true)
    private void modifyEquippedItemsHand(LivingEntity entity, ItemStack stack, ItemTransforms.TransformType transformationMode, HumanoidArm arm, PoseStack matrices, MultiBufferSource vertexConsumers, int light, CallbackInfo ci) {
        List<ModifyEquippedItemRenderPower> powers = Services.POWER.getPowers(entity, ApugliPowers.MODIFY_EQUIPPED_ITEM_RENDER.get());
        if(powers.stream().anyMatch(power -> power.shouldOverride() && ((power.getSlot() == EquipmentSlot.MAINHAND && this.isRightMainHand) || (power.getSlot() == EquipmentSlot.OFFHAND && !this.isRightMainHand))) && arm == HumanoidArm.RIGHT) {
            ci.cancel();
        }
        if(powers.stream().anyMatch(power -> power.shouldOverride() && ((power.getSlot() == EquipmentSlot.MAINHAND && !this.isRightMainHand) || (power.getSlot() == EquipmentSlot.OFFHAND && this.isRightMainHand))) && arm == HumanoidArm.LEFT) {
            ci.cancel();
        }
    }
}
