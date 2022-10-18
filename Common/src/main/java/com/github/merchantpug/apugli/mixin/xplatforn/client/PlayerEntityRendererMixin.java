package com.github.merchantpug.apugli.mixin.xplatforn.client;

import the.great.migration.merchantpug.apugli.power.EntityTextureOverlayPower;
import the.great.migration.merchantpug.apugli.power.ModifyEquippedItemRenderPower;
import the.great.migration.merchantpug.apugli.power.SetTexturePower;
import com.github.merchantpug.apugli.util.TextureUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.power.ModelColorPower;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
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
@Mixin(PlayerRenderer.class)
public abstract class PlayerEntityRendererMixin extends LivingEntityRenderer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

    @Shadow public abstract void render(AbstractClientPlayer abstractClientPlayerEntity, float f, float g, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int i);

    public PlayerEntityRendererMixin(EntityRendererProvider.Context ctx, PlayerModel<AbstractClientPlayer> model, float shadowRadius) {
        super(ctx, model, shadowRadius);
    }

    @Inject(method = "renderArm", at = @At(value = "TAIL"))
    private void renderOverlayOnArm(PoseStack matrices, MultiBufferSource vertexConsumers, int light, AbstractClientPlayer player, ModelPart arm, ModelPart sleeve, CallbackInfo ci) {
        PowerHolderComponent.getPowers(player, EntityTextureOverlayPower.class).forEach(power -> apugli$renderArmOverlay(power, player, matrices, vertexConsumers, light, arm, sleeve));
    }

    @ModifyArgs(method = "renderArm", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/RenderLayer;getEntitySolid(Lnet/minecraft/util/Identifier;)Lnet/minecraft/client/render/RenderLayer;"))
    private void modifyEntityLayerSolid(Args args, PoseStack matrices, MultiBufferSource vertexConsumers, int light, AbstractClientPlayer player, ModelPart arm, ModelPart sleeve) {
        if(PowerHolderComponent.hasPower(player, SetTexturePower.class)) {
            SetTexturePower texturePower = PowerHolderComponent.getPowers(player, SetTexturePower.class).get(0);
            if(texturePower.textureLocation != null) args.set(0, texturePower.textureLocation);
        }
    }

    @ModifyArgs(method = "renderArm", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/RenderLayer;getEntityTranslucent(Lnet/minecraft/util/Identifier;)Lnet/minecraft/client/render/RenderLayer;"))
    private void modifyEntityLayerTranslucent(Args args, PoseStack matrices, MultiBufferSource vertexConsumers, int light, AbstractClientPlayer player, ModelPart arm, ModelPart sleeve) {
        if(PowerHolderComponent.hasPower(player, SetTexturePower.class)) {
            SetTexturePower texturePower = PowerHolderComponent.getPowers(player, SetTexturePower.class).get(0);
            if(texturePower.textureLocation != null) args.set(0, texturePower.textureLocation);
        }
    }

    @Inject(method = "getArmPose", at = @At("HEAD"), cancellable = true)
    private static void setArmPoseWhenModifiedItem(AbstractClientPlayer player, InteractionHand hand, CallbackInfoReturnable<HumanoidModel.ArmPose> cir) {
        if(PowerHolderComponent.getPowers(player, ModifyEquippedItemRenderPower.class)
                .stream()
                .anyMatch(power -> power.shouldOverride() && (power.slot == EquipmentSlot.MAINHAND && hand == InteractionHand.MAIN_HAND && power.shouldOverride() && !power.stack.isEmpty() || power.slot == EquipmentSlot.OFFHAND && hand == InteractionHand.OFF_HAND && power.shouldOverride() && !power.stack.isEmpty()))) {
            cir.setReturnValue(HumanoidModel.ArmPose.ITEM);
        }

        if(PowerHolderComponent.getPowers(player, ModifyEquippedItemRenderPower.class)
                .stream()
                .anyMatch(power -> power.shouldOverride() && (power.slot == EquipmentSlot.MAINHAND && hand == InteractionHand.MAIN_HAND && power.stack.isEmpty() || power.slot == EquipmentSlot.OFFHAND && hand == InteractionHand.OFF_HAND && power.stack.isEmpty()))) {
            cir.setReturnValue(HumanoidModel.ArmPose.EMPTY);
        }
    }

    @Unique
    public void apugli$renderArmOverlay(EntityTextureOverlayPower power, AbstractClientPlayer player, PoseStack matrices, MultiBufferSource vertexConsumers, int light, ModelPart arm, ModelPart sleeve) {
        if(!power.showFirstPerson) return;
        float red = 1.0F;
        float green = 1.0F;
        float blue = 1.0F;
        float alpha = 1.0F;

        if(power.usesRenderingPowers) {
            List<ModelColorPower> modelColorPowers = PowerHolderComponent.getPowers(player, ModelColorPower.class);
            if(modelColorPowers.size() > 0) {
                red = modelColorPowers.stream().map(ModelColorPower::getRed).reduce((a, b) -> a * b).get();
                green = modelColorPowers.stream().map(ModelColorPower::getGreen).reduce((a, b) -> a * b).get();
                blue = modelColorPowers.stream().map(ModelColorPower::getBlue).reduce((a, b) -> a * b).get();
                alpha = modelColorPowers.stream().map(ModelColorPower::getAlpha).min(Float::compare).get();
            }
        }
        RenderType renderLayer;
        if(power.textureUrl != null) {
            TextureUtil.registerEntityTextureOverlayTexture(power.getUrlTextureIdentifier(), power.textureUrl);

            renderLayer = alpha == 1.0 ? RenderType.entityCutoutNoCull(power.getUrlTextureIdentifier()) : RenderType.entityTranslucent(power.getUrlTextureIdentifier());

            arm.render(matrices, vertexConsumers.getBuffer(renderLayer), light, OverlayTexture.NO_OVERLAY, red, green, blue, alpha);
            sleeve.render(matrices, vertexConsumers.getBuffer(renderLayer), light, OverlayTexture.NO_OVERLAY, red, green, blue, alpha);
        } else if(power.textureLocation != null) {
            renderLayer = alpha == 1.0 ? RenderType.entityCutoutNoCull(power.textureLocation) : RenderType.entityTranslucent(power.textureLocation);

            arm.render(matrices, vertexConsumers.getBuffer(renderLayer), light, OverlayTexture.NO_OVERLAY, red, green, blue, alpha);
            sleeve.render(matrices, vertexConsumers.getBuffer(renderLayer), light, OverlayTexture.NO_OVERLAY, red, green, blue, alpha);
        }
    }
}
