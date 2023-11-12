package net.merchantpug.apugli.mixin.xplatform.client;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.merchantpug.apugli.platform.Services;
import net.merchantpug.apugli.power.EntityTextureOverlayPower;
import net.merchantpug.apugli.power.SetTexturePower;
import net.merchantpug.apugli.registry.power.ApugliPowers;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity, M extends EntityModel<T>> extends EntityRenderer<T> implements RenderLayerParent<T, M> {

    @Shadow public abstract M getModel();

    @Shadow protected M model;

    @Unique
    private T apugli$capturedEntity;

    protected LivingEntityRendererMixin(EntityRendererProvider.Context ctx) {
        super(ctx);
    }

    @Inject(method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/EntityModel;renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;IIFFFF)V"))
    private void apugli$captureEntity(T livingEntity, float f, float g, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int i, CallbackInfo ci) {
        apugli$capturedEntity = livingEntity;
    }

    @WrapWithCondition(method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/EntityModel;renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;IIFFFF)V"))
    private boolean apugli$wrapRenderOriginalModel(EntityModel<T> entityModel, PoseStack matrixStack, VertexConsumer vertexConsumer, int i, int p, float red, float green, float blue, float alpha) {
        boolean returnValue = Services.POWER.getPowers(apugli$capturedEntity, ApugliPowers.ENTITY_TEXTURE_OVERLAY.get()).stream().allMatch(EntityTextureOverlayPower::shouldRenderOriginalModelClient);
        this.apugli$capturedEntity = null;
        return returnValue;
    }

    @ModifyVariable(method = "getRenderType", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/client/renderer/entity/LivingEntityRenderer;getTextureLocation(Lnet/minecraft/world/entity/Entity;)Lnet/minecraft/resources/ResourceLocation;"))
    private ResourceLocation apugli$changeTexture(ResourceLocation identifier, T entity) {
        if(Services.POWER.hasPower(entity, ApugliPowers.SET_TEXTURE.get())) {
            SetTexturePower texturePower = Services.POWER.getPowers(entity, ApugliPowers.SET_TEXTURE.get()).get(0);
            if(texturePower.getTextureLocation() != null) {
                return texturePower.getTextureLocation();
            }
        }
        return identifier;
    }
}
