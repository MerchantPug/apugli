package net.merchantpug.apugli.mixin.xplatform.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.merchantpug.apugli.client.util.TextureUtilClient;
import net.merchantpug.apugli.platform.Services;
import net.merchantpug.apugli.power.EntityTextureOverlayPower;
import net.merchantpug.apugli.power.SetTexturePower;
import net.merchantpug.apugli.registry.power.ApugliPowers;
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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(PlayerRenderer.class)
public abstract class PlayerEntityRendererMixin extends LivingEntityRenderer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

    @Shadow public abstract void render(AbstractClientPlayer abstractClientPlayer, float f, float g, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int i);

    public PlayerEntityRendererMixin(EntityRendererProvider.Context ctx, PlayerModel<AbstractClientPlayer> model, float shadowRadius) {
        super(ctx, model, shadowRadius);
    }

    @Inject(method = "renderHand", at = @At(value = "FIELD", target = "Lnet/minecraft/client/model/geom/ModelPart;xRot:F", ordinal = 0), cancellable = true)
    private void renderOverlayWithCancel(PoseStack matrices, MultiBufferSource vertexConsumers, int light, AbstractClientPlayer player, ModelPart arm, ModelPart sleeve, CallbackInfo ci) {
        List<EntityTextureOverlayPower> powers = Services.POWER.getPowers(player, ApugliPowers.ENTITY_TEXTURE_OVERLAY.get());
        if (powers.stream().anyMatch(p -> !p.shouldRenderOriginalModelClient())) {
            arm.xRot = 0.0f;
            sleeve.xRot = 0.0f;
            powers.forEach(power -> apugli$renderArmOverlay(power, player, matrices, vertexConsumers, light, arm, sleeve));
            ci.cancel();
        }
    }

    @Inject(method = "renderHand", at = @At(value = "FIELD", target = "Lnet/minecraft/client/model/geom/ModelPart;xRot:F", ordinal = 1), cancellable = true)
    private void renderOverlayWithCancelSleeve(PoseStack matrices, MultiBufferSource vertexConsumers, int light, AbstractClientPlayer player, ModelPart arm, ModelPart sleeve, CallbackInfo ci) {
        List<EntityTextureOverlayPower> powers = Services.POWER.getPowers(player, ApugliPowers.ENTITY_TEXTURE_OVERLAY.get());
        if (powers.stream().allMatch(EntityTextureOverlayPower::shouldRenderOriginalModelClient) && powers.stream().anyMatch(p -> !p.shouldRenderPlayerOuterLayer())) {
            arm.xRot = 0.0f;
            sleeve.xRot = 0.0f;
            powers.forEach(power -> apugli$renderArmOverlay(power, player, matrices, vertexConsumers, light, arm, sleeve));
            ci.cancel();
        }
    }

    @Inject(method = "renderHand", at = @At(value = "TAIL"))
    private void renderOverlayOnArm(PoseStack matrices, MultiBufferSource vertexConsumers, int light, AbstractClientPlayer player, ModelPart arm, ModelPart sleeve, CallbackInfo ci) {
        List<EntityTextureOverlayPower> powers = Services.POWER.getPowers(player, ApugliPowers.ENTITY_TEXTURE_OVERLAY.get());
        if (powers.stream().allMatch(EntityTextureOverlayPower::shouldRenderOriginalModelClient)) return;
        powers.forEach(power -> apugli$renderArmOverlay(power, player, matrices, vertexConsumers, light, arm, sleeve));
    }

    @Unique
    private AbstractClientPlayer apugli$capturedPlayer;

    @Inject(method = "renderHand", at = @At("HEAD"))
    private void capturePlayer(PoseStack matrices, MultiBufferSource vertexConsumers, int light, AbstractClientPlayer player, ModelPart arm, ModelPart sleeve, CallbackInfo ci) {
        apugli$capturedPlayer = player;
    }

    @ModifyArg(method = "renderHand", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderType;entitySolid(Lnet/minecraft/resources/ResourceLocation;)Lnet/minecraft/client/renderer/RenderType;"), index = 0)
    private ResourceLocation apugli$capturedPlayer(ResourceLocation location) {
        if(Services.POWER.hasPower(apugli$capturedPlayer, ApugliPowers.SET_TEXTURE.get())) {
            SetTexturePower texturePower = Services.POWER.getPowers(apugli$capturedPlayer, ApugliPowers.SET_TEXTURE.get()).get(0);
            if(texturePower.getTextureLocation() != null) {
                return texturePower.getTextureLocation();
            }
        }
        return location;
    }

    @ModifyArg(method = "renderHand", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderType;entityTranslucent(Lnet/minecraft/resources/ResourceLocation;)Lnet/minecraft/client/renderer/RenderType;"), index = 0)
    private ResourceLocation modifyEntityLayerTranslucent(ResourceLocation location) {
        if(Services.POWER.hasPower(apugli$capturedPlayer, ApugliPowers.SET_TEXTURE.get())) {
            SetTexturePower texturePower = Services.POWER.getPowers(apugli$capturedPlayer, ApugliPowers.SET_TEXTURE.get()).get(0);
            this.apugli$capturedPlayer = null;
            if (texturePower.getTextureLocation() != null) {
                return texturePower.getTextureLocation();
            }
        }
        return location;
    }

    @Inject(method = "getArmPose", at = @At("HEAD"), cancellable = true)
    private static void setArmPoseWhenModifiedItem(AbstractClientPlayer player, InteractionHand hand, CallbackInfoReturnable<HumanoidModel.ArmPose> cir) {
        if(Services.POWER.getPowers(player, ApugliPowers.MODIFY_EQUIPPED_ITEM_RENDER.get())
                .stream()
                .anyMatch(power -> power.shouldOverride() && (power.getSlot() == EquipmentSlot.MAINHAND && hand == InteractionHand.MAIN_HAND && power.shouldOverride() && !power.getStack().isEmpty() || power.getSlot() == EquipmentSlot.OFFHAND && hand == InteractionHand.OFF_HAND && power.shouldOverride() && !power.getStack().isEmpty()))) {
            cir.setReturnValue(HumanoidModel.ArmPose.ITEM);
        }

        if(Services.POWER.getPowers(player, ApugliPowers.MODIFY_EQUIPPED_ITEM_RENDER.get())
                .stream()
                .anyMatch(power -> power.shouldOverride() && (power.getSlot() == EquipmentSlot.MAINHAND && hand == InteractionHand.MAIN_HAND && power.getStack().isEmpty() || power.getSlot() == EquipmentSlot.OFFHAND && hand == InteractionHand.OFF_HAND && power.getStack().isEmpty()))) {
            cir.setReturnValue(HumanoidModel.ArmPose.EMPTY);
        }
    }

    @Unique
    public void apugli$renderArmOverlay(EntityTextureOverlayPower power, AbstractClientPlayer player, PoseStack matrices, MultiBufferSource vertexConsumers, int light, ModelPart arm, ModelPart sleeve) {
        if (!power.shouldShowFirstPerson()) return;
        float red = 1.0F;
        float green = 1.0F;
        float blue = 1.0F;
        float alpha = 1.0F;

        if (power.shouldUseRenderingPowers()) {
            float[] rgba = Services.PLATFORM.getColorPowerRgba(player);
            red = rgba[0];
            green = rgba[1];
            blue = rgba[2];
            alpha = rgba[3];
        }

        RenderType renderLayer;
        if (TextureUtilClient.getUrls().containsKey(power.getUrlTextureIdentifier())) {
            renderLayer = alpha == 1.0 ? RenderType.entityCutoutNoCull(power.getUrlTextureIdentifier()) : RenderType.entityTranslucent(power.getUrlTextureIdentifier());

            arm.render(matrices, vertexConsumers.getBuffer(renderLayer), light, OverlayTexture.NO_OVERLAY, red, green, blue, alpha);
            sleeve.render(matrices, vertexConsumers.getBuffer(renderLayer), light, OverlayTexture.NO_OVERLAY, red, green, blue, alpha);
        } else if (power.getTextureLocation() != null) {
            renderLayer = alpha == 1.0 ? RenderType.entityCutoutNoCull(power.getTextureLocation()) : RenderType.entityTranslucent(power.getTextureLocation());

            arm.render(matrices, vertexConsumers.getBuffer(renderLayer), light, OverlayTexture.NO_OVERLAY, red, green, blue, alpha);
            sleeve.render(matrices, vertexConsumers.getBuffer(renderLayer), light, OverlayTexture.NO_OVERLAY, red, green, blue, alpha);
        }
    }
}
