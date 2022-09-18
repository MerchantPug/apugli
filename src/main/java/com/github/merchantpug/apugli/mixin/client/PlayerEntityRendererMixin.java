package com.github.merchantpug.apugli.mixin.client;

import com.github.merchantpug.apugli.entity.feature.EnergySwirlOverlayFeatureRenderer;
import com.github.merchantpug.apugli.entity.feature.EntityTextureOverlayFeatureRenderer;
import com.github.merchantpug.apugli.power.EntityTextureOverlayPower;
import com.github.merchantpug.apugli.power.ModifyEquippedItemRenderPower;
import com.github.merchantpug.apugli.power.SetTexturePower;
import com.github.merchantpug.apugli.util.TextureUtil;
import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.power.ModelColorPower;
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
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.List;

@Environment(EnvType.CLIENT)
@Mixin(PlayerEntityRenderer.class)
public abstract class PlayerEntityRendererMixin extends LivingEntityRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {

    @Shadow public abstract void render(AbstractClientPlayerEntity abstractClientPlayerEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i);

    public PlayerEntityRendererMixin(EntityRendererFactory.Context ctx, PlayerEntityModel<AbstractClientPlayerEntity> model, float shadowRadius) {
        super(ctx, model, shadowRadius);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void addFeatures(EntityRendererFactory.Context ctx, boolean slim, CallbackInfo ci) {
        // This is separated from LivingEntityRendererMixin as it breaks certain mods if it's part of that.
        this.addFeature(new EnergySwirlOverlayFeatureRenderer<>(this));
        this.addFeature(new EntityTextureOverlayFeatureRenderer<>(this));
    }

    @Inject(method = "renderArm", at = @At(value = "TAIL"))
    private void renderOverlayOnArm(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, AbstractClientPlayerEntity player, ModelPart arm, ModelPart sleeve, CallbackInfo ci) {
        PowerHolderComponent.getPowers(player, EntityTextureOverlayPower.class).forEach(power -> apugli$renderArmOverlay(power, player, matrices, vertexConsumers, light, arm, sleeve));
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

    @Unique
    public void apugli$renderArmOverlay(EntityTextureOverlayPower power, AbstractClientPlayerEntity player, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, ModelPart arm, ModelPart sleeve) {
        if (!power.showFirstPerson) return;
        float red = 1.0F;
        float green = 1.0F;
        float blue = 1.0F;
        float alpha = 1.0F;

        if (power.usesRenderingPowers) {
            List<ModelColorPower> modelColorPowers = PowerHolderComponent.getPowers(player, ModelColorPower.class);
            if (modelColorPowers.size() > 0) {
                red = modelColorPowers.stream().map(ModelColorPower::getRed).reduce((a, b) -> a * b).get();
                green = modelColorPowers.stream().map(ModelColorPower::getGreen).reduce((a, b) -> a * b).get();
                blue = modelColorPowers.stream().map(ModelColorPower::getBlue).reduce((a, b) -> a * b).get();
                alpha = modelColorPowers.stream().map(ModelColorPower::getAlpha).min(Float::compare).get();
            }
        }
        RenderLayer renderLayer;
        if (power.textureUrl != null) {
            TextureUtil.registerEntityTextureOverlayTexture(power.getUrlTextureIdentifier(), power.textureUrl);

            renderLayer = alpha == 1.0 ? RenderLayer.getEntityCutoutNoCull(power.getUrlTextureIdentifier()) : RenderLayer.getEntityTranslucent(power.getUrlTextureIdentifier());

            arm.render(matrices, vertexConsumers.getBuffer(renderLayer), light, OverlayTexture.DEFAULT_UV, red, green, blue, alpha);
            sleeve.render(matrices, vertexConsumers.getBuffer(renderLayer), light, OverlayTexture.DEFAULT_UV, red, green, blue, alpha);
        } else if (power.textureLocation != null) {
            renderLayer = alpha == 1.0 ? RenderLayer.getEntityCutoutNoCull(power.textureLocation) : RenderLayer.getEntityTranslucent(power.textureLocation);

            arm.render(matrices, vertexConsumers.getBuffer(renderLayer), light, OverlayTexture.DEFAULT_UV, red, green, blue, alpha);
            sleeve.render(matrices, vertexConsumers.getBuffer(renderLayer), light, OverlayTexture.DEFAULT_UV, red, green, blue, alpha);
        }
    }
}
