package com.github.merchantpug.apugli.mixin.xplatforn.client;

import the.great.migration.merchantpug.apugli.power.ModifyEquippedItemRenderPower;
import io.github.apace100.apoli.component.PowerHolderComponent;
import com.github.merchantpug.apugli.util.ModifyEquippedItemRenderUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Environment(EnvType.CLIENT)
@Mixin(CustomHeadLayer.class)
public abstract class HeadFeatureRendererMixin<T extends LivingEntity, M extends EntityModel<T> & HeadedModel> extends RenderLayer<T, M> {
    @Shadow @Final private float scaleX;

    @Shadow @Final private float scaleY;

    @Shadow @Final private float scaleZ;

    @Shadow @Final private ItemInHandRenderer heldItemRenderer;

    public HeadFeatureRendererMixin(RenderLayerParent<T, M> context) {
        super(context);
    }

    @Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/LivingEntity;FFFFFF)V", at = @At("HEAD"), cancellable = true)
    private void preventHeadRender(PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int i, T livingEntity, float f, float g, float h, float j, float k, float l, CallbackInfo ci) {
        List<ModifyEquippedItemRenderPower> modifyEquippedItemRenderPowers = PowerHolderComponent.getPowers(livingEntity, ModifyEquippedItemRenderPower.class);
        modifyEquippedItemRenderPowers.forEach(power -> {
            if(power.slot == EquipmentSlot.HEAD) ModifyEquippedItemRenderUtil.renderIndividualStackOnHead((CustomHeadLayer<?, ?>)(Object) this, matrixStack, vertexConsumerProvider, i, livingEntity, f, power.scale * this.scaleX, power.scale * this.scaleY, power.scale * this.scaleZ, power.stack, this.heldItemRenderer);
        });
        if(modifyEquippedItemRenderPowers.stream().anyMatch(power -> power.shouldOverride() && power.slot == EquipmentSlot.HEAD)) ci.cancel();
    }
}
