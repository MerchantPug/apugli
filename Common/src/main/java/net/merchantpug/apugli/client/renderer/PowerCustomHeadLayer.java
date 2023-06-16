package net.merchantpug.apugli.client.renderer;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.merchantpug.apugli.mixin.xplatform.client.accessor.HeadFeatureRendererAccessor;
import net.merchantpug.apugli.platform.Services;
import net.merchantpug.apugli.power.ModifyEquippedItemRenderPower;
import net.merchantpug.apugli.registry.power.ApugliPowers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.SkullModelBase;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.AbstractSkullBlock;
import net.minecraft.world.level.block.SkullBlock;

import java.util.List;

public class PowerCustomHeadLayer<T extends LivingEntity, M extends EntityModel<T> & HeadedModel> extends CustomHeadLayer<T, M> {

    public PowerCustomHeadLayer(RenderLayerParent<T, M> parent, EntityModelSet set) {
        super(parent, set);
    }

    public PowerCustomHeadLayer(RenderLayerParent<T, M> renderLayerParent, EntityModelSet entityModelSet, float scaleX, float scaleY, float scaleZ) {
        super(renderLayerParent, entityModelSet, scaleX, scaleY, scaleZ);
    }

    public void render(PoseStack matrices, MultiBufferSource vertexConsumers, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        List<ModifyEquippedItemRenderPower> powers = Services.POWER.getPowers(entity, ApugliPowers.MODIFY_EQUIPPED_ITEM_RENDER.get());
        powers.forEach(power -> {
            if(power.getSlot() == EquipmentSlot.HEAD) renderIndividualStackOnHead(matrices, vertexConsumers, light, entity, limbAngle, power.getScale() * ((HeadFeatureRendererAccessor)this).getScaleX(), power.getScale() * ((HeadFeatureRendererAccessor)this).getScaleY(), power.getScale() * ((HeadFeatureRendererAccessor)this).getScaleZ(), power.getStack(), Minecraft.getInstance().getItemInHandRenderer());
        });
    }

    private void renderIndividualStackOnHead(PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int i, LivingEntity livingEntity, float f, float scaleX, float scaleY, float scaleZ, ItemStack stack, ItemInHandRenderer heldItemRenderer) {
        if(!stack.isEmpty()) {
            Item item = stack.getItem();
            matrixStack.pushPose();
            matrixStack.scale(scaleX, scaleY, scaleZ);
            boolean bl = livingEntity instanceof Villager || livingEntity instanceof ZombieVillager;
            if(livingEntity.isBaby() && !(livingEntity instanceof Villager)) {
                matrixStack.translate(0.0D, 0.03125D, 0.0D);
                matrixStack.scale(0.7F, 0.7F, 0.7F);
                matrixStack.translate(0.0D, 1.0D, 0.0D);
            }
            getParentModel().getHead().translateAndRotate(matrixStack);
            if(item instanceof BlockItem && ((BlockItem) item).getBlock() instanceof AbstractSkullBlock) {
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
                SkullModelBase skullBlockEntityModel = ((HeadFeatureRendererAccessor)this).getHeadModels().get(skullType);
                RenderType renderLayer = SkullBlockRenderer.getRenderType(skullType, gameProfile);
                SkullBlockRenderer.renderSkull(null, 180.0F, f, matrixStack, vertexConsumerProvider, i, skullBlockEntityModel, renderLayer);
            } else if(!(item instanceof ArmorItem) || ((ArmorItem) item).getSlot() != EquipmentSlot.HEAD) {
                matrixStack.translate(0.0D, -0.25D, 0.0D);
                matrixStack.mulPose(Vector3f.YP.rotationDegrees(180.0F));
                matrixStack.scale(0.625F, -0.625F, -0.625F);
                if(bl) {
                    matrixStack.translate(0.0D, 0.1875D, 0.0D);
                }
                heldItemRenderer.renderItem(livingEntity, stack, ItemTransforms.TransformType.HEAD, false, matrixStack, vertexConsumerProvider, i);
            }
            matrixStack.popPose();
        }
    }

}
