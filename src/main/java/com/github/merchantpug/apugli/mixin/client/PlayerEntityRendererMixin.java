package com.github.merchantpug.apugli.mixin.client;

import com.github.merchantpug.apugli.power.EntityTextureOverlayPower;
import com.github.merchantpug.apugli.power.ModifyEquippedItemRenderPower;
import com.github.merchantpug.apugli.power.SetTexturePower;
import com.github.merchantpug.apugli.util.TextureUtil;
import io.github.apace100.apoli.component.PowerHolderComponent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Environment(EnvType.CLIENT)
@Mixin(PlayerEntityRenderer.class)
public abstract class PlayerEntityRendererMixin extends LivingEntityRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {

    @Shadow public abstract void render(AbstractClientPlayerEntity abstractClientPlayerEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i);

    public PlayerEntityRendererMixin(EntityRendererFactory.Context ctx, PlayerEntityModel<AbstractClientPlayerEntity> model, float shadowRadius) {
        super(ctx, model, shadowRadius);
    }

    @Inject(method = "renderArm", at = @At(value = "FIELD", target = "Lnet/minecraft/client/model/ModelPart;pitch:F", ordinal = 1), cancellable = true)
    private void renderOverlayOnArmWithHiddenEntityModel(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, AbstractClientPlayerEntity player, ModelPart arm, ModelPart sleeve, CallbackInfo ci) {
        if (PowerHolderComponent.getPowers(player, EntityTextureOverlayPower.class).stream().anyMatch(power -> power.hideEntityModel)) {
            if (PowerHolderComponent.hasPower(player, EntityTextureOverlayPower.class)) {
                arm.pitch = 0.0f;
                sleeve.pitch = 0.0f;
            }

            PowerHolderComponent.getPowers(player, EntityTextureOverlayPower.class).forEach(power -> {
                if (!power.showFirstPerson) return;
                if (power.textureUrl != null) {
                    TextureUtil.registerEntityTextureOverlayTexture(power.getUrlTextureIdentifier(), power.textureUrl);
                    arm.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntitySolid(power.getUrlTextureIdentifier())), light, OverlayTexture.DEFAULT_UV);
                    sleeve.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(power.getUrlTextureIdentifier())), light, OverlayTexture.DEFAULT_UV);
                } else if (power.textureLocation != null) {
                    arm.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntitySolid(power.textureLocation)), light, OverlayTexture.DEFAULT_UV);
                    sleeve.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(power.textureLocation)), light, OverlayTexture.DEFAULT_UV);
                }
            });
            ci.cancel();
        }
    }

    @Inject(method = "renderArm", at = @At(value = "TAIL"))
    private void renderOverlayOnArm(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, AbstractClientPlayerEntity player, ModelPart arm, ModelPart sleeve, CallbackInfo ci) {
        if (PowerHolderComponent.getPowers(player, EntityTextureOverlayPower.class).stream().noneMatch(power -> power.hideEntityModel)) {
            PowerHolderComponent.getPowers(player, EntityTextureOverlayPower.class).forEach(power -> {
                if (!power.showFirstPerson) return;
                if (power.textureUrl != null) {
                    TextureUtil.registerEntityTextureOverlayTexture(power.getUrlTextureIdentifier(), power.textureUrl);
                    arm.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntitySolid(power.getUrlTextureIdentifier())), light, OverlayTexture.DEFAULT_UV);
                    sleeve.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(power.getUrlTextureIdentifier())), light, OverlayTexture.DEFAULT_UV);
                } else if (power.textureLocation != null) {
                    arm.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntitySolid(power.textureLocation)), light, OverlayTexture.DEFAULT_UV);
                    sleeve.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(power.textureLocation)), light, OverlayTexture.DEFAULT_UV);
                }
            });
        }
    }

    @ModifyArgs(method = "renderArm", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/RenderLayer;getEntitySolid(Lnet/minecraft/util/Identifier;)Lnet/minecraft/client/render/RenderLayer;"))
    private void modifyEntityLayerSolid(Args args, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, AbstractClientPlayerEntity player, ModelPart arm, ModelPart sleeve) {
        if (PowerHolderComponent.hasPower(player, SetTexturePower.class)) {
            SetTexturePower texturePower = PowerHolderComponent.getPowers(player, SetTexturePower.class).get(0);
            if (texturePower.textureLocation != null) args.set(0, texturePower.textureLocation);
        }
    }

    @ModifyArgs(method = "renderArm", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/RenderLayer;getEntityTranslucent(Lnet/minecraft/util/Identifier;)Lnet/minecraft/client/render/RenderLayer;"))
    private void modifyEntityLayerTranslucent(Args args, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, AbstractClientPlayerEntity player, ModelPart arm, ModelPart sleeve) {
        if (PowerHolderComponent.hasPower(player, SetTexturePower.class)) {
            SetTexturePower texturePower = PowerHolderComponent.getPowers(player, SetTexturePower.class).get(0);
            if (texturePower.textureLocation != null) args.set(0, texturePower.textureLocation);
        }
    }

    @Inject(method = "getArmPose", at = @At("HEAD"), cancellable = true)
    private static void setArmPoseWhenModifiedItem(AbstractClientPlayerEntity player, Hand hand, CallbackInfoReturnable<BipedEntityModel.ArmPose> cir) {
        if (PowerHolderComponent.getPowers(player, ModifyEquippedItemRenderPower.class)
                .stream()
                .anyMatch(power -> power.shouldOverride() && (power.slot == EquipmentSlot.MAINHAND && hand == Hand.MAIN_HAND && power.shouldOverride() && !power.stack.isEmpty() || power.slot == EquipmentSlot.OFFHAND && hand == Hand.OFF_HAND && power.shouldOverride() && !power.stack.isEmpty()))) {
            cir.setReturnValue(BipedEntityModel.ArmPose.ITEM);
        }

        if (PowerHolderComponent.getPowers(player, ModifyEquippedItemRenderPower.class)
                .stream()
                .anyMatch(power -> power.shouldOverride() && (power.slot == EquipmentSlot.MAINHAND && hand == Hand.MAIN_HAND && power.stack.isEmpty() || power.slot == EquipmentSlot.OFFHAND && hand == Hand.OFF_HAND && power.stack.isEmpty()))) {
            cir.setReturnValue(BipedEntityModel.ArmPose.EMPTY);
        }
    }
}
