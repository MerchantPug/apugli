package net.merchantpug.apugli.util;

import net.merchantpug.apugli.mixin.client.ArmorFeatureRendererAccessor;
import net.merchantpug.apugli.mixin.client.HeadFeatureRendererAccessor;
import net.merchantpug.apugli.power.ModifyEquippedItemRenderPower;
import com.mojang.authlib.GameProfile;
import net.minecraft.block.AbstractSkullBlock;
import net.minecraft.block.SkullBlock;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.SkullBlockEntityModel;
import net.minecraft.client.render.block.entity.SkullBlockEntityRenderer;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.HeadFeatureRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.ModelWithArms;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.ZombieVillagerEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.util.Arm;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;

public class ModifyEquippedItemRenderUtil {

    public static void renderArmor(ArmorFeatureRenderer<?, ?, ?> renderer, ItemStack stack, MatrixStack matrices, VertexConsumerProvider vertexConsumers, LivingEntity entity, EquipmentSlot armorSlot, int light, BipedEntityModel model) {
        if (stack.getItem() instanceof ArmorItem armorItem) {
            if (armorItem.getSlotType() == armorSlot) {
                renderer.getContextModel().copyBipedStateTo(model);
                ((ArmorFeatureRendererAccessor)renderer).invokeSetVisible(model, armorSlot);
                boolean bl = armorSlot == EquipmentSlot.LEGS;
                boolean bl2 = stack.hasGlint();
                if (armorItem instanceof DyeableArmorItem) {
                    int i = ((DyeableArmorItem) armorItem).getColor(stack);
                    float f = (float) (i >> 16 & 255) / 255.0F;
                    float g = (float) (i >> 8 & 255) / 255.0F;
                    float h = (float) (i & 255) / 255.0F;
                    ((ArmorFeatureRendererAccessor)renderer).invokeRenderArmorParts(matrices, vertexConsumers, light, armorItem, bl2, model, bl, f, g, h, null);
                    ((ArmorFeatureRendererAccessor)renderer).invokeRenderArmorParts(matrices, vertexConsumers, light, armorItem, bl2, model, bl, 1.0F, 1.0F, 1.0F, "overlay");
                } else {
                    ((ArmorFeatureRendererAccessor)renderer).invokeRenderArmorParts(matrices, vertexConsumers, light, armorItem, bl2, model, bl, 1.0F, 1.0F, 1.0F, null);
                }
            }
        }
    }

    public static void renderIndividualStackOnHead(HeadFeatureRenderer<?, ?> renderer, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, LivingEntity livingEntity, float f, float scaleX, float scaleY, float scaleZ, ItemStack stack, HeldItemRenderer heldItemRenderer) {
        if (!stack.isEmpty()) {
            Item item = stack.getItem();
            matrixStack.push();
            matrixStack.scale(scaleX, scaleY, scaleZ);
            boolean bl = livingEntity instanceof VillagerEntity || livingEntity instanceof ZombieVillagerEntity;
            float o;
            if (livingEntity.isBaby() && !(livingEntity instanceof VillagerEntity)) {
                o = 2.0F;
                float n = 1.4F;
                matrixStack.translate(0.0D, 0.03125D, 0.0D);
                matrixStack.scale(0.7F, 0.7F, 0.7F);
                matrixStack.translate(0.0D, 1.0D, 0.0D);
            }
            renderer.getContextModel().getHead().rotate(matrixStack);
            if (item instanceof BlockItem && ((BlockItem) item).getBlock() instanceof AbstractSkullBlock) {
                o = 1.1875F;
                matrixStack.scale(1.1875F, -1.1875F, -1.1875F);
                if (bl) matrixStack.translate(0.0D, 0.0625D, 0.0D);
                GameProfile gameProfile = null;
                if (stack.hasNbt()) {
                    NbtCompound nbtCompound = stack.getNbt();
                    if (nbtCompound.contains("SkullOwner", 10)) {
                        gameProfile = NbtHelper.toGameProfile(nbtCompound.getCompound("SkullOwner"));
                    }
                }
                matrixStack.translate(-0.5D, 0.0D, -0.5D);
                SkullBlock.SkullType skullType = ((AbstractSkullBlock) ((BlockItem) item).getBlock()).getSkullType();
                SkullBlockEntityModel skullBlockEntityModel = (SkullBlockEntityModel)((HeadFeatureRendererAccessor)renderer).getHeadModels().get(skullType);
                RenderLayer renderLayer = SkullBlockEntityRenderer.getRenderLayer(skullType, gameProfile);
                SkullBlockEntityRenderer.renderSkull((Direction) null, 180.0F, f, matrixStack, vertexConsumerProvider, i, skullBlockEntityModel, renderLayer);
            } else if (!(item instanceof ArmorItem) || ((ArmorItem) item).getSlotType() != EquipmentSlot.HEAD) {
                ModifyEquippedItemRenderUtil.translateStackOnHead(matrixStack, bl);
                heldItemRenderer.renderItem(livingEntity, stack, ModelTransformation.Mode.HEAD, false, matrixStack, vertexConsumerProvider, i);
            }
            matrixStack.pop();
        }
    }

    public static void translateStackOnHead(MatrixStack matrices, boolean villager) {
        float f = 0.625F;
        matrices.translate(0.0D, -0.25D, 0.0D);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F));
        matrices.scale(0.625F, -0.625F, -0.625F);
        if (villager) {
            matrices.translate(0.0D, 0.1875D, 0.0D);
        }
    }

    public static void chooseArm(FeatureRenderer<?, ?> renderer, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, LivingEntity livingEntity, ModifyEquippedItemRenderPower power, HeldItemRenderer heldItemRenderer) {
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
            if (renderer.getContextModel().child) {
                float m = 0.5F;
                matrixStack.translate(0.0D, 0.75D, 0.0D);
                matrixStack.scale(0.5F, 0.5F, 0.5F);
            }

            ModifyEquippedItemRenderUtil.renderItem(renderer, livingEntity, rightHandStack, ModelTransformation.Mode.THIRD_PERSON_RIGHT_HAND, Arm.RIGHT, matrixStack, vertexConsumerProvider, i, power.shouldOverride(), power.shouldMergeWithHeld(), heldItemRenderer);
            ModifyEquippedItemRenderUtil.renderItem(renderer, livingEntity, leftHandStack, ModelTransformation.Mode.THIRD_PERSON_LEFT_HAND, Arm.LEFT, matrixStack, vertexConsumerProvider, i, power.shouldOverride(), power.shouldMergeWithHeld(), heldItemRenderer);
            matrixStack.pop();
        }
    }

    protected static void renderItem(FeatureRenderer<?, ?> renderer, LivingEntity entity, ItemStack stack, ModelTransformation.Mode transformationMode, Arm arm, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, boolean shouldOverride, boolean shouldMergeWithHeld, HeldItemRenderer heldItemRenderer) {
        boolean isMainArm = entity.getMainArm() == arm;
        if (!stack.isEmpty() && (shouldOverride || ((entity.getEquippedStack(EquipmentSlot.MAINHAND).isEmpty() || shouldMergeWithHeld) && isMainArm || (entity.getEquippedStack(EquipmentSlot.OFFHAND).isEmpty() || shouldMergeWithHeld) && !isMainArm))) {
            matrices.push();
            ((ModelWithArms)renderer.getContextModel()).setArmAngle(arm, matrices);
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-90.0F));
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F));
            boolean bl = arm == Arm.LEFT;
            matrices.translate(((float)(bl ? -1 : 1) / 16.0F), 0.125D, -0.625D);
            heldItemRenderer.renderItem(entity, stack, transformationMode, bl, matrices, vertexConsumers, light);
            matrices.pop();
        }
    }
}
