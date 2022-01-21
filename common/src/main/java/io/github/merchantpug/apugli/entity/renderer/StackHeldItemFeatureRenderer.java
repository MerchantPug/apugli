package io.github.merchantpug.apugli.entity.renderer;

import io.github.apace100.origins.component.OriginComponent;
import io.github.merchantpug.apugli.powers.ModifyEquippedItemRenderPower;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.ModelWithArms;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;

public class StackHeldItemFeatureRenderer<T extends LivingEntity, M extends EntityModel<T> & ModelWithArms> extends FeatureRenderer<T, M> {
    public StackHeldItemFeatureRenderer(FeatureRendererContext<T, M> featureRendererContext) {
        super(featureRendererContext);
    }

    public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T livingEntity, float f, float g, float h, float j, float k, float l) {
        OriginComponent.getPowers(livingEntity, ModifyEquippedItemRenderPower.class).forEach(power -> {
            this.chooseArm(matrixStack, vertexConsumerProvider, i, livingEntity, power);
        });
    }

    public void chooseArm(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T livingEntity, ModifyEquippedItemRenderPower power) {
        boolean bl = livingEntity.getMainArm() == Arm.RIGHT;
        ItemStack leftHandStack = ItemStack.EMPTY;
        ItemStack rightHandStack = ItemStack.EMPTY;
        if (bl) {
            if (power.slot == EquipmentSlot.OFFHAND) {
                leftHandStack = power.stack;
            }
            if (power.slot == EquipmentSlot.MAINHAND) {
                rightHandStack = power.stack;
            }
        } else {
            if (power.slot == EquipmentSlot.MAINHAND) {
                leftHandStack = power.stack;
            }
            if (power.slot == EquipmentSlot.OFFHAND) {
                rightHandStack = power.stack;
            }
        }
        if (!leftHandStack.isEmpty() || !rightHandStack.isEmpty()) {
            matrixStack.push();
            if (this.getContextModel().child) {
                float m = 0.5F;
                matrixStack.translate(0.0D, 0.75D, 0.0D);
                matrixStack.scale(0.5F, 0.5F, 0.5F);
            }

            this.renderItem(livingEntity, rightHandStack, ModelTransformation.Mode.THIRD_PERSON_RIGHT_HAND, Arm.RIGHT, matrixStack, vertexConsumerProvider, i, power.shouldOverride(), power.shouldMergeWithHeld());
            this.renderItem(livingEntity, leftHandStack, ModelTransformation.Mode.THIRD_PERSON_LEFT_HAND, Arm.LEFT, matrixStack, vertexConsumerProvider, i, power.shouldOverride(), power.shouldMergeWithHeld());
            matrixStack.pop();
        }
    }

    protected void renderItem(LivingEntity entity, ItemStack stack, ModelTransformation.Mode transformationMode, Arm arm, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, boolean shouldOverride, boolean shouldMergeWithHeld) {
        boolean isMainArm = entity.getMainArm() == arm;
        if (!stack.isEmpty() && (shouldOverride || ((entity.getEquippedStack(EquipmentSlot.MAINHAND).isEmpty() || shouldMergeWithHeld) && isMainArm || (entity.getEquippedStack(EquipmentSlot.OFFHAND).isEmpty() || shouldMergeWithHeld) && !isMainArm))) {
            matrices.push();
            (this.getContextModel()).setArmAngle(arm, matrices);
            matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(-90.0F));
            matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(180.0F));
            boolean bl = arm == Arm.LEFT;
            matrices.translate(((float)(bl ? -1 : 1) / 16.0F), 0.125D, -0.625D);
            MinecraftClient.getInstance().getHeldItemRenderer().renderItem(entity, stack, transformationMode, bl, matrices, vertexConsumers, light);
            matrices.pop();
        }
    }
}
