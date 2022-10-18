package com.github.merchantpug.apugli.util;

import the.great.migration.merchantpug.apugli.mixin.client.ArmorFeatureRendererAccessor;
import the.great.migration.merchantpug.apugli.mixin.client.HeadFeatureRendererAccessor;
import the.great.migration.merchantpug.apugli.power.ModifyEquippedItemRenderPower;
import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.SkullModelBase;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.core.Direction;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeableArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.AbstractSkullBlock;
import net.minecraft.world.level.block.SkullBlock;

public class ModifyEquippedItemRenderUtil {

    public static void renderArmor(HumanoidArmorLayer<?, ?, ?> renderer, ItemStack stack, PoseStack matrices, MultiBufferSource vertexConsumers, LivingEntity entity, EquipmentSlot armorSlot, int light, HumanoidModel model) {
        if(stack.getItem() instanceof ArmorItem armorItem) {
            if(armorItem.getSlot() == armorSlot) {
                renderer.getParentModel().copyPropertiesTo(model);
                ((ArmorFeatureRendererAccessor)renderer).invokeSetVisible(model, armorSlot);
                boolean bl = armorSlot == EquipmentSlot.LEGS;
                boolean bl2 = stack.hasFoil();
                if(armorItem instanceof DyeableArmorItem) {
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

    public static void renderIndividualStackOnHead(CustomHeadLayer<?, ?> renderer, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int i, LivingEntity livingEntity, float f, float scaleX, float scaleY, float scaleZ, ItemStack stack, ItemInHandRenderer heldItemRenderer) {
        if(!stack.isEmpty()) {
            Item item = stack.getItem();
            matrixStack.pushPose();
            matrixStack.scale(scaleX, scaleY, scaleZ);
            boolean bl = livingEntity instanceof Villager || livingEntity instanceof ZombieVillager;
            float o;
            if(livingEntity.isBaby() && !(livingEntity instanceof Villager)) {
                o = 2.0F;
                float n = 1.4F;
                matrixStack.translate(0.0D, 0.03125D, 0.0D);
                matrixStack.scale(0.7F, 0.7F, 0.7F);
                matrixStack.translate(0.0D, 1.0D, 0.0D);
            }
            renderer.getParentModel().getHead().translateAndRotate(matrixStack);
            if(item instanceof BlockItem && ((BlockItem) item).getBlock() instanceof AbstractSkullBlock) {
                o = 1.1875F;
                matrixStack.scale(1.1875F, -1.1875F, -1.1875F);
                if(bl) matrixStack.translate(0.0D, 0.0625D, 0.0D);
                GameProfile gameProfile = null;
                if(stack.hasTag()) {
                    CompoundTag nbtCompound = stack.getTag();
                    if(nbtCompound.contains("SkullOwner", 10)) {
                        gameProfile = NbtUtils.readGameProfile(nbtCompound.getCompound("SkullOwner"));
                    }
                }
                matrixStack.translate(-0.5D, 0.0D, -0.5D);
                SkullBlock.Type skullType = ((AbstractSkullBlock) ((BlockItem) item).getBlock()).getType();
                SkullModelBase skullBlockEntityModel = (SkullModelBase)((HeadFeatureRendererAccessor)renderer).getHeadModels().get(skullType);
                RenderType renderLayer = SkullBlockRenderer.getRenderType(skullType, gameProfile);
                SkullBlockRenderer.renderSkull((Direction) null, 180.0F, f, matrixStack, vertexConsumerProvider, i, skullBlockEntityModel, renderLayer);
            } else if(!(item instanceof ArmorItem) || ((ArmorItem) item).getSlot() != EquipmentSlot.HEAD) {
                ModifyEquippedItemRenderUtil.translateStackOnHead(matrixStack, bl);
                heldItemRenderer.renderItem(livingEntity, stack, ItemTransforms.TransformType.HEAD, false, matrixStack, vertexConsumerProvider, i);
            }
            matrixStack.popPose();
        }
    }

    public static void translateStackOnHead(PoseStack matrices, boolean villager) {
        float f = 0.625F;
        matrices.translate(0.0D, -0.25D, 0.0D);
        matrices.mulPose(Vector3f.YP.rotationDegrees(180.0F));
        matrices.scale(0.625F, -0.625F, -0.625F);
        if(villager) {
            matrices.translate(0.0D, 0.1875D, 0.0D);
        }
    }

    public static void chooseArm(RenderLayer<?, ?> renderer, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int i, LivingEntity livingEntity, ModifyEquippedItemRenderPower power, ItemInHandRenderer heldItemRenderer) {
        boolean bl = livingEntity.getMainArm() == HumanoidArm.RIGHT;
        ItemStack leftHandStack = ItemStack.EMPTY;
        ItemStack rightHandStack = ItemStack.EMPTY;
        if(bl) {
            if(power.slot == EquipmentSlot.OFFHAND) {
                leftHandStack = power.stack;
            }
            if(power.slot == EquipmentSlot.MAINHAND) {
                rightHandStack = power.stack;
            }
        } else {
            if(power.slot == EquipmentSlot.MAINHAND) {
                leftHandStack = power.stack;
            }
            if(power.slot == EquipmentSlot.OFFHAND) {
                rightHandStack = power.stack;
            }
        }
        if(!leftHandStack.isEmpty() || !rightHandStack.isEmpty()) {
            matrixStack.pushPose();
            if(renderer.getParentModel().young) {
                float m = 0.5F;
                matrixStack.translate(0.0D, 0.75D, 0.0D);
                matrixStack.scale(0.5F, 0.5F, 0.5F);
            }

            ModifyEquippedItemRenderUtil.renderItem(renderer, livingEntity, rightHandStack, ItemTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, HumanoidArm.RIGHT, matrixStack, vertexConsumerProvider, i, power.shouldOverride(), power.shouldMergeWithHeld(), heldItemRenderer);
            ModifyEquippedItemRenderUtil.renderItem(renderer, livingEntity, leftHandStack, ItemTransforms.TransformType.THIRD_PERSON_LEFT_HAND, HumanoidArm.LEFT, matrixStack, vertexConsumerProvider, i, power.shouldOverride(), power.shouldMergeWithHeld(), heldItemRenderer);
            matrixStack.popPose();
        }
    }

    protected static void renderItem(RenderLayer<?, ?> renderer, LivingEntity entity, ItemStack stack, ItemTransforms.TransformType transformationMode, HumanoidArm arm, PoseStack matrices, MultiBufferSource vertexConsumers, int light, boolean shouldOverride, boolean shouldMergeWithHeld, ItemInHandRenderer heldItemRenderer) {
        boolean isMainArm = entity.getMainArm() == arm;
        if(!stack.isEmpty() && (shouldOverride || ((entity.getItemBySlot(EquipmentSlot.MAINHAND).isEmpty() || shouldMergeWithHeld) && isMainArm || (entity.getItemBySlot(EquipmentSlot.OFFHAND).isEmpty() || shouldMergeWithHeld) && !isMainArm))) {
            matrices.pushPose();
            ((ArmedModel)renderer.getParentModel()).translateToHand(arm, matrices);
            matrices.mulPose(Vector3f.XP.rotationDegrees(-90.0F));
            matrices.mulPose(Vector3f.YP.rotationDegrees(180.0F));
            boolean bl = arm == HumanoidArm.LEFT;
            matrices.translate(((float)(bl ? -1 : 1) / 16.0F), 0.125D, -0.625D);
            heldItemRenderer.renderItem(entity, stack, transformationMode, bl, matrices, vertexConsumers, light);
            matrices.popPose();
        }
    }
}
