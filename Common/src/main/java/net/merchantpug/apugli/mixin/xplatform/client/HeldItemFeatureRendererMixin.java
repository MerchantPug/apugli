package net.merchantpug.apugli.mixin.xplatform.client;

import the.great.migration.merchantpug.apugli.power.ModifyEquippedItemRenderPower;
import io.github.apace100.apoli.component.PowerHolderComponent;
import net.merchantpug.apugli.util.ModifyEquippedItemRenderUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Environment(EnvType.CLIENT)
@Mixin(ItemInHandLayer.class)
public abstract class HeldItemFeatureRendererMixin<T extends LivingEntity, M extends EntityModel<T> & ArmedModel> extends RenderLayer<T, M> {
    @Shadow @Final private ItemInHandRenderer heldItemRenderer;
    @Unique boolean isRightMainHand;

    public HeldItemFeatureRendererMixin(RenderLayerParent<T, M> context) {
        super(context);
    }

    @Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/LivingEntity;FFFFFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/feature/HeldItemFeatureRenderer;getContextModel()Lnet/minecraft/client/render/entity/model/EntityModel;"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void captureBoolean(PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int i, T livingEntity, float f, float g, float h, float j, float k, float l, CallbackInfo ci, boolean bl) {
        this.isRightMainHand = bl;
    }

    @Inject(method = "renderItem", at = @At("HEAD"), cancellable = true)
    private void modifyEquippedItemsHand(LivingEntity entity, ItemStack stack, ItemTransforms.TransformType transformationMode, HumanoidArm arm, PoseStack matrices, MultiBufferSource vertexConsumers, int light, CallbackInfo ci) {
        List<ModifyEquippedItemRenderPower> modifyEquippedItemRenderPowers = PowerHolderComponent.getPowers(entity, ModifyEquippedItemRenderPower.class);
        modifyEquippedItemRenderPowers.forEach(power -> {
            ModifyEquippedItemRenderUtil.chooseArm(this, matrices, vertexConsumers, light, entity, power, this.heldItemRenderer);
        });
        if(modifyEquippedItemRenderPowers.stream().anyMatch(power -> power.shouldOverride() && ((power.slot == EquipmentSlot.MAINHAND && this.isRightMainHand) || (power.slot == EquipmentSlot.OFFHAND && !this.isRightMainHand))) && arm == HumanoidArm.RIGHT) ci.cancel();
        if(modifyEquippedItemRenderPowers.stream().anyMatch(power -> power.shouldOverride() && ((power.slot == EquipmentSlot.MAINHAND && !this.isRightMainHand) || (power.slot == EquipmentSlot.OFFHAND && this.isRightMainHand))) && arm == HumanoidArm.LEFT) ci.cancel();
    }
}
