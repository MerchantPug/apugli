package io.github.merchantpug.apugli.mixin.client;

import io.github.apace100.origins.component.OriginComponent;
import io.github.merchantpug.apugli.entity.renderer.StackArmorFeatureRenderer;
import io.github.merchantpug.apugli.entity.renderer.StackHeadFeatureRenderer;
import io.github.merchantpug.apugli.entity.renderer.StackHeldItemFeatureRenderer;
import io.github.merchantpug.apugli.powers.ModifyEquippedItemRenderPower;
import io.github.merchantpug.apugli.powers.SetTexturePower;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Environment(EnvType.CLIENT)
@Mixin(PlayerEntityRenderer.class)
public abstract class PlayerEntityRendererMixin extends LivingEntityRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {

    public PlayerEntityRendererMixin(EntityRenderDispatcher dispatcher, PlayerEntityModel<AbstractClientPlayerEntity> model, float shadowRadius) {
        super(dispatcher, model, shadowRadius);
    }

    @Inject(method = "<init>(Lnet/minecraft/client/render/entity/EntityRenderDispatcher;Z)V", at = @At("RETURN"))
    private void construct(EntityRenderDispatcher dispatcher, boolean slim, CallbackInfo ci) {
        this.addFeature(new StackHeadFeatureRenderer<>(this));
        this.addFeature(new StackArmorFeatureRenderer(this, new BipedEntityModel(0.5F), new BipedEntityModel(1.0F)));
        this.addFeature(new StackHeldItemFeatureRenderer<>(this));
    }

    @ModifyArgs(method = "renderArm", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/RenderLayer;getEntitySolid(Lnet/minecraft/util/Identifier;)Lnet/minecraft/client/render/RenderLayer;"))
    private void modifyEntityLayerSolid(Args args, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, AbstractClientPlayerEntity player, ModelPart arm, ModelPart sleeve) {
        if (OriginComponent.hasPower(player, SetTexturePower.class)) {
            SetTexturePower texturePower = OriginComponent.getPowers(player, SetTexturePower.class).get(0);
            if (texturePower.textureLocation != null) args.set(0, texturePower.textureLocation);
        }
    }

    @ModifyArgs(method = "renderArm", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/RenderLayer;getEntityTranslucent(Lnet/minecraft/util/Identifier;)Lnet/minecraft/client/render/RenderLayer;"))
    private void modifyEntityLayerTranslucent(Args args, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, AbstractClientPlayerEntity player, ModelPart arm, ModelPart sleeve) {
        if (OriginComponent.hasPower(player, SetTexturePower.class)) {
            SetTexturePower texturePower = OriginComponent.getPowers(player, SetTexturePower.class).get(0);
            if (texturePower.textureLocation != null) args.set(0, texturePower.textureLocation);
        }
    }

    @Inject(method = "getArmPose", at = @At("HEAD"), cancellable = true)
    private static void setArmPoseWhenModifiedItem(AbstractClientPlayerEntity player, Hand hand, CallbackInfoReturnable<BipedEntityModel.ArmPose> cir) {
        if (OriginComponent.getPowers(player, ModifyEquippedItemRenderPower.class)
                .stream()
                .anyMatch(power -> (power.slot == EquipmentSlot.MAINHAND && hand == Hand.MAIN_HAND && power.shouldOverride() && !power.stack.isEmpty() || power.slot == EquipmentSlot.OFFHAND && hand == Hand.OFF_HAND && power.shouldOverride() && !power.stack.isEmpty())) ||
        OriginComponent.getPowers(player, ModifyEquippedItemRenderPower.class)
                .stream()
                .anyMatch(power -> (power.slot == EquipmentSlot.MAINHAND && hand == Hand.MAIN_HAND && !power.stack.isEmpty() && player.getEquippedStack(EquipmentSlot.MAINHAND).isEmpty() && !power.shouldMergeWithHeld() && !power.shouldOverride()) || (power.slot == EquipmentSlot.OFFHAND && hand == Hand.OFF_HAND && !power.stack.isEmpty() && player.getEquippedStack(EquipmentSlot.OFFHAND).isEmpty() && !power.shouldMergeWithHeld() && !power.shouldOverride()))) {
            cir.setReturnValue(BipedEntityModel.ArmPose.ITEM);
        }

        if (OriginComponent.getPowers(player, ModifyEquippedItemRenderPower.class)
                .stream()
                .anyMatch(power -> power.shouldOverride() && (power.slot == EquipmentSlot.MAINHAND && hand == Hand.MAIN_HAND && power.stack.isEmpty() || power.slot == EquipmentSlot.OFFHAND && hand == Hand.OFF_HAND && power.stack.isEmpty()))) {
            cir.setReturnValue(BipedEntityModel.ArmPose.EMPTY);
        }
    }
}
