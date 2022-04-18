package io.github.merchantpug.apugli.util;

import com.mojang.authlib.GameProfile;
import io.github.merchantpug.apugli.mixin.client.ArmorFeatureRendererAccessor;
import io.github.merchantpug.apugli.mixin.client.ElytraFeatureRendererAccessor;
import io.github.merchantpug.apugli.powers.ModifyEquippedItemRenderPower;
import net.minecraft.block.AbstractSkullBlock;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.SkullBlockEntityRenderer;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.feature.ElytraFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.HeadFeatureRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.ModelWithArms;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.ZombieVillagerEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import org.apache.commons.lang3.StringUtils;

import java.util.UUID;

public class ModifyEquippedItemRenderUtil {

    public static void renderElytra(ElytraFeatureRenderer<?, ?> renderer, ItemStack stack, MatrixStack matrices, VertexConsumerProvider vertexConsumers, LivingEntity entity, int light, float f, float g, float h, float j, float k, float l) {
        Object abstractClientPlayerEntity;

        Identifier identifier = entity instanceof AbstractClientPlayerEntity ? (((AbstractClientPlayerEntity)(abstractClientPlayerEntity = (AbstractClientPlayerEntity)entity)).canRenderElytraTexture() && ((AbstractClientPlayerEntity)abstractClientPlayerEntity).getElytraTexture() != null ? ((AbstractClientPlayerEntity)abstractClientPlayerEntity).getElytraTexture() : (((AbstractClientPlayerEntity)abstractClientPlayerEntity).canRenderCapeTexture() && ((AbstractClientPlayerEntity)abstractClientPlayerEntity).getCapeTexture() != null && ((PlayerEntity)abstractClientPlayerEntity).isPartVisible(PlayerModelPart.CAPE) ? ((AbstractClientPlayerEntity)abstractClientPlayerEntity).getCapeTexture() : ((ElytraFeatureRendererAccessor)renderer).getSKIN())) : ((ElytraFeatureRendererAccessor)renderer).getSKIN();

        matrices.push();
        matrices.translate(0.0, 0.0, 0.125);
        ((EntityModel)renderer.getContextModel()).copyStateTo(((ElytraFeatureRendererAccessor)renderer).getElytra());
        ((ElytraFeatureRendererAccessor)renderer).getElytra().setAngles(entity, f, g, j, k, l);
        abstractClientPlayerEntity = ItemRenderer.getArmorGlintConsumer(vertexConsumers, RenderLayer.getArmorCutoutNoCull(identifier), false, stack.hasGlint());
        ((ElytraFeatureRendererAccessor)renderer).getElytra().render(matrices, (VertexConsumer)abstractClientPlayerEntity, light, OverlayTexture.DEFAULT_UV, 1.0f, 1.0f, 1.0f, 1.0f);
        matrices.pop();
    }

    public static void renderArmor(ArmorFeatureRenderer<?, ?, ?> renderer, ItemStack stack, MatrixStack matrices, VertexConsumerProvider vertexConsumers, LivingEntity entity, EquipmentSlot armorSlot, int light, BipedEntityModel model) {
        if (stack.getItem() instanceof ArmorItem) {
            ArmorItem armorItem = (ArmorItem)stack.getItem();
            if (armorItem.getSlotType() == armorSlot) {
                renderer.getContextModel().setAttributes(model);
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

    public static void renderIndividualStackOnHead(HeadFeatureRenderer<?, ?> renderer, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, LivingEntity livingEntity, float f, float scaleX, float scaleY, float scaleZ, ItemStack itemStack) {
        if (!itemStack.isEmpty()) {
            Item item = itemStack.getItem();
            matrixStack.push();
            matrixStack.scale(scaleX, scaleY, scaleZ);
            boolean bl = livingEntity instanceof VillagerEntity || livingEntity instanceof ZombieVillagerEntity;
            float p;
            if (livingEntity.isBaby() && !(livingEntity instanceof VillagerEntity)) {
                p = 2.0F;
                float n = 1.4F;
                matrixStack.translate(0.0D, 0.03125D, 0.0D);
                matrixStack.scale(0.7F, 0.7F, 0.7F);
                matrixStack.translate(0.0D, 1.0D, 0.0D);
            }

            renderer.getContextModel().getHead().rotate(matrixStack);
            if (item instanceof BlockItem && ((BlockItem)item).getBlock() instanceof AbstractSkullBlock) {
                p = 1.1875F;
                matrixStack.scale(1.1875F, -1.1875F, -1.1875F);
                if (bl) {
                    matrixStack.translate(0.0D, 0.0625D, 0.0D);
                }

                GameProfile gameProfile = null;
                if (itemStack.hasTag()) {
                    CompoundTag compoundTag = itemStack.getTag();
                    if (compoundTag.contains("SkullOwner", 10)) {
                        gameProfile = NbtHelper.toGameProfile(compoundTag.getCompound("SkullOwner"));
                    } else if (compoundTag.contains("SkullOwner", 8)) {
                        String string = compoundTag.getString("SkullOwner");
                        if (!StringUtils.isBlank(string)) {
                            gameProfile = SkullBlockEntity.loadProperties(new GameProfile((UUID)null, string));
                            compoundTag.put("SkullOwner", NbtHelper.fromGameProfile(new CompoundTag(), gameProfile));
                        }
                    }
                }

                matrixStack.translate(-0.5D, 0.0D, -0.5D);
                SkullBlockEntityRenderer.render((Direction)null, 180.0F, ((AbstractSkullBlock)((BlockItem)item).getBlock()).getSkullType(), gameProfile, f, matrixStack, vertexConsumerProvider, i);
            } else if (!(item instanceof ArmorItem) || ((ArmorItem)item).getSlotType() != EquipmentSlot.HEAD) {
                p = 0.625F;
                matrixStack.translate(0.0D, -0.25D, 0.0D);
                matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(180.0F));
                matrixStack.scale(0.625F, -0.625F, -0.625F);
                if (bl) {
                    matrixStack.translate(0.0D, 0.1875D, 0.0D);
                }

                MinecraftClient.getInstance().getHeldItemRenderer().renderItem(livingEntity, itemStack, ModelTransformation.Mode.HEAD, false, matrixStack, vertexConsumerProvider, i);
            }

            matrixStack.pop();
        }
    }

    public static void chooseArm(FeatureRenderer<?, ?> renderer, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, LivingEntity livingEntity, ModifyEquippedItemRenderPower power) {
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

            ModifyEquippedItemRenderUtil.renderItem(renderer, livingEntity, rightHandStack, ModelTransformation.Mode.THIRD_PERSON_RIGHT_HAND, Arm.RIGHT, matrixStack, vertexConsumerProvider, i, power.shouldOverride(), power.shouldMergeWithHeld());
            ModifyEquippedItemRenderUtil.renderItem(renderer, livingEntity, leftHandStack, ModelTransformation.Mode.THIRD_PERSON_LEFT_HAND, Arm.LEFT, matrixStack, vertexConsumerProvider, i, power.shouldOverride(), power.shouldMergeWithHeld());
            matrixStack.pop();
        }
    }

    protected static void renderItem(FeatureRenderer<?, ?> renderer, LivingEntity entity, ItemStack stack, ModelTransformation.Mode transformationMode, Arm arm, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, boolean shouldOverride, boolean shouldMergeWithHeld) {
        boolean isMainArm = entity.getMainArm() == arm;
        if (!stack.isEmpty() && (shouldOverride || ((entity.getEquippedStack(EquipmentSlot.MAINHAND).isEmpty() || shouldMergeWithHeld) && isMainArm || (entity.getEquippedStack(EquipmentSlot.OFFHAND).isEmpty() || shouldMergeWithHeld) && !isMainArm))) {
            matrices.push();
            ((ModelWithArms)renderer.getContextModel()).setArmAngle(arm, matrices);
            matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(-90.0F));
            matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(180.0F));
            boolean bl = arm == Arm.LEFT;
            matrices.translate(((float)(bl ? -1 : 1) / 16.0F), 0.125D, -0.625D);
            MinecraftClient.getInstance().getHeldItemRenderer().renderItem(entity, stack, transformationMode, bl, matrices, vertexConsumers, light);
            matrices.pop();
        }
    }
}
