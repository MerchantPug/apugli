package net.merchantpug.apugli.mixin.client;

import net.merchantpug.apugli.entity.feature.EnergySwirlOverlayFeatureRenderer;
import net.merchantpug.apugli.entity.feature.EntityTextureOverlayFeatureRenderer;
import net.merchantpug.apugli.power.EntityTextureOverlayPower;
import net.merchantpug.apugli.power.ModifyEquippedItemRenderPower;
import net.merchantpug.apugli.power.SetTexturePower;
import net.merchantpug.apugli.util.ArmOverlayUtil;
import io.github.apace100.apoli.component.PowerHolderComponent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.AbstractClientPlayerEntity;
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
        this.addFeature(new EntityTextureOverlayFeatureRenderer<>(this, slim, ctx.getModelLoader()));
    }

    @Inject(method = "renderArm", at = @At(value = "FIELD", target = "Lnet/minecraft/client/model/ModelPart;pitch:F", ordinal = 0), cancellable = true)
    private void renderOverlayWithCancel(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, AbstractClientPlayerEntity player, ModelPart arm, ModelPart sleeve, CallbackInfo ci) {
        if (PowerHolderComponent.getPowers(player, EntityTextureOverlayPower.class).stream().anyMatch(p -> !p.shouldRenderOriginalModelClient())) {
            arm.pitch = 0.0f;
            sleeve.pitch = 0.0f;
            PowerHolderComponent.getPowers(player, EntityTextureOverlayPower.class).forEach(power -> ArmOverlayUtil.renderArmOverlay(power, player, matrices, vertexConsumers, light, arm, sleeve));
            ci.cancel();
        }
    }

    @Inject(method = "renderArm", at = @At(value = "FIELD", target = "Lnet/minecraft/client/model/ModelPart;pitch:F", ordinal = 1), cancellable = true)
    private void renderOverlayWithCancelSleeve(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, AbstractClientPlayerEntity player, ModelPart arm, ModelPart sleeve, CallbackInfo ci) {
        List<EntityTextureOverlayPower> powers = PowerHolderComponent.getPowers(player, EntityTextureOverlayPower.class);
        if (powers.stream().allMatch(EntityTextureOverlayPower::shouldRenderOriginalModelClient) && powers.stream().anyMatch(p -> !p.shouldRenderPlayerOuterLayer())) {
            arm.pitch = 0.0f;
            sleeve.pitch = 0.0f;
            PowerHolderComponent.getPowers(player, EntityTextureOverlayPower.class).forEach(power -> ArmOverlayUtil.renderArmOverlay(power, player, matrices, vertexConsumers, light, arm, sleeve));
            ci.cancel();
        }
    }

    @Inject(method = "renderArm", at = @At(value = "TAIL"))
    private void renderOverlayOnArm(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, AbstractClientPlayerEntity player, ModelPart arm, ModelPart sleeve, CallbackInfo ci) {
        if (PowerHolderComponent.getPowers(player, EntityTextureOverlayPower.class).stream().allMatch(EntityTextureOverlayPower::shouldRenderOriginalModelClient)) return;
        PowerHolderComponent.getPowers(player, EntityTextureOverlayPower.class).forEach(power -> ArmOverlayUtil.renderArmOverlay(power, player, matrices, vertexConsumers, light, arm, sleeve));
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
