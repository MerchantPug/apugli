package io.github.merchantpug.apugli.entity.feature;

import com.mojang.authlib.GameProfile;
import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.merchantpug.apugli.power.ModifyEquippedItemRenderPower;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.AbstractSkullBlock;
import net.minecraft.block.SkullBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.SkullBlockEntityModel;
import net.minecraft.client.render.block.entity.SkullBlockEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.render.entity.model.ModelWithHead;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.PiglinEntity;
import net.minecraft.entity.mob.ZombieVillagerEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3f;

import java.util.*;

@Environment(EnvType.CLIENT)
public class StackHeadFeatureRenderer<T extends LivingEntity, M extends EntityModel<T> & ModelWithHead> extends FeatureRenderer<T, M> {
    private final Map<SkullBlock.SkullType, SkullBlockEntityModel> headModels;

    public StackHeadFeatureRenderer(FeatureRendererContext<T, M> context, EntityModelLoader loader) {
        super(context);
        this.headModels = SkullBlockEntityRenderer.getModels(loader);
    }

    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        PowerHolderComponent.getPowers(entity, ModifyEquippedItemRenderPower.class).forEach(power -> {
            if (power.slot == EquipmentSlot.HEAD) {
                renderIndividualStack(matrices, vertexConsumers, light, entity, tickDelta, this.getScaleX(entity, power.scale), power.scale, this.getScaleZ(entity, power.scale), power.stack);
            }
        });
    }

    public void renderIndividualStack(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T livingEntity, float f, float scaleX, float scaleY, float scaleZ, ItemStack stack) {
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
            ((ModelWithHead) this.getContextModel()).getHead().rotate(matrixStack);
            if (item instanceof BlockItem && ((BlockItem) item).getBlock() instanceof AbstractSkullBlock) {
                o = 1.1875F;
                matrixStack.scale(1.1875F, -1.1875F, -1.1875F);
                if (bl) {
                    matrixStack.translate(0.0D, 0.0625D, 0.0D);
                }
                GameProfile gameProfile = null;
                if (stack.hasNbt()) {
                    NbtCompound nbtCompound = stack.getNbt();
                    if (nbtCompound.contains("SkullOwner", 10)) {
                        gameProfile = NbtHelper.toGameProfile(nbtCompound.getCompound("SkullOwner"));
                    }
                }
                matrixStack.translate(-0.5D, 0.0D, -0.5D);
                SkullBlock.SkullType skullType = ((AbstractSkullBlock) ((BlockItem) item).getBlock()).getSkullType();
                SkullBlockEntityModel skullBlockEntityModel = (SkullBlockEntityModel) this.headModels.get(skullType);
                RenderLayer renderLayer = SkullBlockEntityRenderer.getRenderLayer(skullType, gameProfile);
                SkullBlockEntityRenderer.renderSkull((Direction) null, 180.0F, f, matrixStack, vertexConsumerProvider, i, skullBlockEntityModel, renderLayer);
            } else if (!(item instanceof ArmorItem) || ((ArmorItem) item).getSlotType() != EquipmentSlot.HEAD) {
                translate(matrixStack, bl);
                MinecraftClient.getInstance().getHeldItemRenderer().renderItem(livingEntity, stack, ModelTransformation.Mode.HEAD, false, matrixStack, vertexConsumerProvider, i);
            }
            matrixStack.pop();
        }
    }

    public static void translate(MatrixStack matrices, boolean villager) {
        float f = 0.625F;
        matrices.translate(0.0D, -0.25D, 0.0D);
        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180.0F));
        matrices.scale(0.625F, -0.625F, -0.625F);
        if (villager) {
            matrices.translate(0.0D, 0.1875D, 0.0D);
        }
    }

    protected float getScaleX(LivingEntity entity, float scaleX) {
        return entity instanceof PiglinEntity ? scaleX * 1.0019531F : scaleX;
    }

    protected float getScaleZ(LivingEntity entity, float scaleZ) {
        return entity instanceof PiglinEntity ? scaleZ * 1.0019531F : scaleZ;
    }
}
